package com.example.demo.supportTickets.dao;

import com.example.demo.supportTickets.model.SupportTicketRequests;
import java.util.List;

public interface SupportTicketRequestsDAO {
    SupportTicketRequests createRequest(SupportTicketRequests request);
    List<SupportTicketRequests> getRequestsByStudent(String studentId);
    SupportTicketRequests getRequestById(String requestId);
}