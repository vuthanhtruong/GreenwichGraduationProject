// src/main/java/com/example/demo/document/controller/SubmissionDocumentDownloadController.java

package com.example.demo.document.controller;

import com.example.demo.document.model.SubmissionDocuments;
import com.example.demo.document.service.SubmissionDocumentsService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.file.Files;
import java.nio.file.Paths;

@Controller
public class SubmissionDocumentDownloadController {

    private final SubmissionDocumentsService service;

    public SubmissionDocumentDownloadController(SubmissionDocumentsService service) {
        this.service = service;
    }

    @GetMapping("/classroom/download-submission-file")
    public ResponseEntity<byte[]> downloadFile(@RequestParam("docId") String docId) {
        SubmissionDocuments doc = service.getDocumentById(docId);
        if (doc == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] data = null;
        String fileName = "submission_file";

        // Ưu tiên 1: Dùng fileData (trong DB)
        if (doc.getFileData() != null && doc.getFileData().length > 0) {
            data = doc.getFileData();
            fileName = extractFileName(doc.getFilePath());
        }
        // Ưu tiên 2: Dùng filePath (trên disk)
        else if (doc.getFilePath() != null && !doc.getFilePath().isEmpty()) {
            try {
                data = Files.readAllBytes(Paths.get(doc.getFilePath()));
                fileName = extractFileName(doc.getFilePath());
            } catch (Exception e) {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }

        String contentType = getContentType(fileName);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename(sanitizeFileName(fileName))
                .build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(data);
    }

    // === HELPER ===
    private String getContentType(String fileName) {
        if (fileName == null || !fileName.contains(".")) return "application/octet-stream";
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

    private String sanitizeFileName(String fileName) {
        if (fileName == null) return "file";
        return fileName.replaceAll("[^a-zA-Z0-9.-]", "_");
    }

    private String extractFileName(String filePath) {
        if (filePath == null || filePath.isEmpty()) return "file";
        int slash = Math.max(filePath.lastIndexOf('/'), filePath.lastIndexOf('\\'));
        return slash >= 0 ? filePath.substring(slash + 1) : filePath;
    }
}