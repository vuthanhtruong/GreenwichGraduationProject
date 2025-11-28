// File: SupportTicketRequestsDocumentController.java
package com.example.demo.document.controller;

import com.example.demo.document.model.SupportTicketRequestsDocument;
import com.example.demo.document.service.SupportTicketRequestsDocumentService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.charset.StandardCharsets;

@Controller
public class SupportTicketRequestsDocumentController {

    private final SupportTicketRequestsDocumentService service;

    public SupportTicketRequestsDocumentController(SupportTicketRequestsDocumentService service) {
        this.service = service;
    }

    @GetMapping("/student-home/support-tickets/requests/documents/{documentId}")
    public ResponseEntity<Resource> serveDocument(
            @PathVariable String documentId,
            @RequestParam(defaultValue = "false") boolean download) {

        SupportTicketRequestsDocument doc = service.getDocumentById(documentId);
        if (doc == null || doc.getFileData() == null) {
            return ResponseEntity.notFound().build();
        }

        String contentType = doc.getFileType() != null ? doc.getFileType() : "application/octet-stream";
        boolean inline = !download && (contentType.equals("application/pdf") || contentType.startsWith("image/"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentDisposition(ContentDisposition.builder(inline ? "inline" : "attachment")
                .filename(doc.getFileName(), StandardCharsets.UTF_8)
                .build());
        headers.set("X-Content-Type-Options", "nosniff");
        headers.setContentLength(doc.getFileSize());

        return ResponseEntity.ok()
                .headers(headers)
                .body(new ByteArrayResource(doc.getFileData()));
    }
}