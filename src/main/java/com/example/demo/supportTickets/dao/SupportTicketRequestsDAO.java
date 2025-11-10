package com.example.demo.supportTickets.dao;

import com.example.demo.supportTickets.model.SupportTicketRequests;
import java.util.List;

public interface SupportTicketRequestsDAO {
    SupportTicketRequests createRequest(SupportTicketRequests request);
    List<SupportTicketRequests> getRequestsByStudent(String studentId);
    SupportTicketRequests getRequestById(String requestId);
    List<SupportTicketRequests> getPaginatedRequestsByStudent(String studentId, int offset, int size, String search);
    long countRequestsByStudent(String studentId, String search);
    void updateRequest(SupportTicketRequests request);

    List<SupportTicketRequests> getPaginatedPendingRequests(int offset, int size, String search);

    long countPendingRequests(String search);
}