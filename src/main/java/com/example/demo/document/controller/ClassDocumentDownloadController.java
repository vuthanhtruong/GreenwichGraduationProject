// src/main/java/com/example/demo/document/controller/ClassDocumentDownloadController.java

package com.example.demo.document.controller;

import com.example.demo.document.model.ClassDocuments;
import com.example.demo.document.service.ClassDocumentsService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.nio.file.Files;
import java.nio.file.Paths;

@Controller
@RequestMapping("/documents")
public class ClassDocumentDownloadController {

    private final ClassDocumentsService classDocumentsService;

    public ClassDocumentDownloadController(ClassDocumentsService classDocumentsService) {
        this.classDocumentsService = classDocumentsService;
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable String documentId) {
        ClassDocuments document = classDocumentsService.getDocumentById(documentId);
        if (document == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] data = null;
        String contentType = "application/octet-stream";
        String fileName = "document";

        // Ưu tiên 1: Dùng fileData (trong DB)
        if (document.getFileData() != null && document.getFileData().length > 0) {
            data = document.getFileData();
            fileName = document.getDocumentTitle();
        }
        // Ưu tiên 2: Dùng filePath (trên disk)
        else if (document.getFilePath() != null && !document.getFilePath().isEmpty()) {
            try {
                data = Files.readAllBytes(Paths.get(document.getFilePath()));
                fileName = document.getDocumentTitle();
            } catch (Exception e) {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }

        // Lấy content type từ tên file
        contentType = getContentType(fileName);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename(sanitizeFileName(fileName))
                .build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(data);
    }

    // Lấy content type từ tên file
    private String getContentType(String fileName) {
        if (fileName == null) return "application/octet-stream";
        String ext = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        return switch (ext) {
            case "pdf" -> "application/pdf";
            case "doc" -> "application/msword";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "txt" -> "text/plain";
            case "ppt" -> "application/vnd.ms-powerpoint";
            case "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            case "zip" -> "application/zip";
            default -> "application/octet-stream";
        };
    }

    // Làm sạch tên file (giống syllabus)
    private String sanitizeFileName(String fileName) {
        if (fileName == null) return "file";
        return fileName.replaceAll("[^a-zA-Z0-9.-]", "_");
    }
}