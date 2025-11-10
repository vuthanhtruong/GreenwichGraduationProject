package com.example.demo.supportTickets.service;

import com.example.demo.supportTickets.model.SupportTicketRequests;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface SupportTicketRequestsService {
    SupportTicketRequests createRequest(SupportTicketRequests request, List<MultipartFile> files);
    List<SupportTicketRequests> getRequestsByStudent(String studentId);
    SupportTicketRequests getRequestById(String requestId);

}