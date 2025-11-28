// SupportTicketDocumentsController.java
package com.example.demo.document.controller;

import com.example.demo.document.model.SupportTicketDocuments;
import com.example.demo.document.service.SupportTicketDocumentsService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/support-tickets/documents")
public class SupportTicketDocumentsController {

    private final SupportTicketDocumentsService documentsService;

    public SupportTicketDocumentsController(SupportTicketDocumentsService documentsService) {
        this.documentsService = documentsService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> downloadOrView(@PathVariable Long id) {
        SupportTicketDocuments doc = documentsService.getDocumentById(id);
        if (doc == null || doc.getFileData() == null || doc.getFileData().length == 0) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(doc.getFileType()));

        // Tên file: dùng fileName gốc + đuôi đúng
        String fileName = doc.getFileName();
        String extension = getFileExtension(doc.getFileType());
        if (!fileName.toLowerCase().endsWith(extension)) {
            fileName = fileName + extension;
        }

        // Xem trong trình duyệt nếu là PDF hoặc ảnh
        boolean isViewable = doc.getFileType().startsWith("image/") ||
                doc.getFileType().equals("application/pdf");

        headers.setContentDisposition(
                isViewable
                        ? ContentDisposition.inline().filename(fileName).build()
                        : ContentDisposition.attachment().filename(fileName).build()
        );

        return ResponseEntity.ok()
                .headers(headers)
                .body(doc.getFileData());
    }

    private String getFileExtension(String contentType) {
        return switch (contentType) {
            case "application/pdf" -> ".pdf";
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            case "application/msword" -> ".doc";
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> ".docx";
            default -> "";
        };
    }
}