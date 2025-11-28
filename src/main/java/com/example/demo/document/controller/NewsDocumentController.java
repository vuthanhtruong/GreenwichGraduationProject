// File: NewsDocumentController.java
package com.example.demo.document.controller;

import com.example.demo.document.model.Documents;
import com.example.demo.document.service.DocumentsService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class NewsDocumentController {

    private final DocumentsService documentsService;

    // ĐƯỜNG DẪN THƯ MỤC LƯU FILE TRÊN DISK (thay bằng của bạn)
    private static final String UPLOAD_DIR = "D:/uploads/news/";  // <-- THAY ĐÚNG ĐƯỜNG DẪN CỦA BẠN

    public NewsDocumentController(DocumentsService documentsService) {
        this.documentsService = documentsService;
    }

    @GetMapping("/check-news/download/{documentId}")
    public ResponseEntity<Resource> serveDocument(
            @PathVariable String documentId,
            @RequestParam(defaultValue = "false") boolean view) {

        Documents doc = documentsService.getDocumentById(documentId);
        if (doc == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] data = null;
        String fileName = doc.getDocumentTitle() != null && !doc.getDocumentTitle().isBlank()
                ? doc.getDocumentTitle() : "document";

        String contentType = inferContentType(fileName);

        // Ưu tiên 1: Dùng fileData trong DB
        if (doc.getFileData() != null && doc.getFileData().length > 0) {
            data = doc.getFileData();
        }
        // Ưu tiên 2: Dùng filePath trên disk (AN TOÀN 100% - chống Path Traversal)
        else if (doc.getFilePath() != null && !doc.getFilePath().isBlank()) {
            try {
                Path safePath = getSafePath(doc.getFilePath());
                if (Files.exists(safePath) && Files.isRegularFile(safePath)) {
                    data = Files.readAllBytes(safePath);
                    contentType = Files.probeContentType(safePath);
                    if (contentType == null || contentType.isBlank()) {
                        contentType = inferContentType(fileName);
                    }
                }
            } catch (Exception e) {
                return ResponseEntity.notFound().build();
            }
        }

        if (data == null || data.length == 0) {
            return ResponseEntity.notFound().build();
        }

        boolean canInline = view && (
                "application/pdf".equals(contentType) ||
                        contentType.startsWith("image/")
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentDisposition(ContentDisposition.builder(canInline ? "inline" : "attachment")
                .filename(fileName, StandardCharsets.UTF_8)
                .build());
        headers.set("X-Content-Type-Options", "nosniff");
        headers.setCacheControl("no-cache, no-store, must-revalidate");
        headers.setContentLength(data.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(new ByteArrayResource(data));
    }

    // === AN TOÀN TUYỆT ĐỐI: CHỐNG PATH TRAVERSAL ===
    private Path getSafePath(String filePathFromDb) {
        // Chỉ chấp nhận tên file hoặc relative path ngắn
        Path path = Paths.get(filePathFromDb).normalize();
        Path resolved = Paths.get(UPLOAD_DIR).resolve(path).normalize();

        // Bắt buộc phải nằm trong UPLOAD_DIR
        if (!resolved.startsWith(Paths.get(UPLOAD_DIR).normalize())) {
            throw new SecurityException("Access denied: Invalid file path");
        }
        return resolved;
    }

    // === Dự phòng nếu không detect được content type ===
    private String inferContentType(String fileName) {
        if (fileName == null) return MediaType.APPLICATION_OCTET_STREAM_VALUE;
        String ext = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        return switch (ext) {
            case "pdf" -> "application/pdf";
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "zip", "rar" -> "application/zip";
            default -> MediaType.APPLICATION_OCTET_STREAM_VALUE;
        };
    }
}