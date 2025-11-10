package com.example.demo.supportTickets.service;

import com.example.demo.supportTickets.model.SupportTicketRequests;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface SupportTicketRequestsService {
    SupportTicketRequests createRequest(SupportTicketRequests request, List<MultipartFile> files);
    List<SupportTicketRequests> getRequestsByStudent(String studentId);
    SupportTicketRequests getRequestById(String requestId);
    List<SupportTicketRequests> getPaginatedRequestsByStudent(String studentId, int offset, int size, String search);
    long countRequestsByStudent(String studentId, String search);
    void updateRequest(SupportTicketRequests request);

    List<SupportTicketRequests> getPaginatedPendingRequests(int offset, int size, String search);

    long countPendingRequests(String search);
}