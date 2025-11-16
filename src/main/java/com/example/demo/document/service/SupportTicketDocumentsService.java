package com.example.demo.document.service;

import com.example.demo.document.model.SupportTicketDocuments;
import java.util.List;
import java.util.Map;

public interface SupportTicketDocumentsService {
    List<SupportTicketDocuments> getDocumentsByTicketId(String ticketId);
    SupportTicketDocuments getDocumentById(Long id);
    SupportTicketDocuments saveDocument(SupportTicketDocuments doc);
    void deleteDocument(Long id);
    long countDocumentsByTicketId(String ticketId); // ĐÃ CÓ
    Map<String, String> validateDocument(SupportTicketDocuments doc);
}