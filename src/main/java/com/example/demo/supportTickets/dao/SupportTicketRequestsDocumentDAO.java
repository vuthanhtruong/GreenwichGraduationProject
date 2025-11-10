// File: SupportTicketRequestsDocumentDAO.java
package com.example.demo.supportTickets.dao;

import com.example.demo.supportTickets.model.SupportTicketRequestsDocument;
import java.util.List;

public interface SupportTicketRequestsDocumentDAO {
    SupportTicketRequestsDocument save(SupportTicketRequestsDocument document);
    List<SupportTicketRequestsDocument> getDocumentsByRequestId(String requestId);
    SupportTicketRequestsDocument getDocumentById(String documentId);
    void delete(String documentId);
}