package com.example.demo.document.service;

import com.example.demo.document.model.SupportTicketRequestsDocument;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface SupportTicketRequestsDocumentService {
    SupportTicketRequestsDocument uploadDocument(MultipartFile file, String requestId);
    List<SupportTicketRequestsDocument> getDocumentsByRequestId(String requestId);
    ByteArrayResource downloadDocument(String documentId); // Trả về file từ DB
    void deleteDocument(String documentId);
    SupportTicketRequestsDocument getDocumentById(String documentId);
}