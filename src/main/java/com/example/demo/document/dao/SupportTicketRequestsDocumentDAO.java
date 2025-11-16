// File: SupportTicketRequestsDocumentDAO.java
package com.example.demo.document.dao;

import com.example.demo.document.model.SupportTicketRequestsDocument;
import java.util.List;

public interface SupportTicketRequestsDocumentDAO {
    SupportTicketRequestsDocument save(SupportTicketRequestsDocument document);
    List<SupportTicketRequestsDocument> getDocumentsByRequestId(String requestId);
    SupportTicketRequestsDocument getDocumentById(String documentId);
    void delete(String documentId);
}