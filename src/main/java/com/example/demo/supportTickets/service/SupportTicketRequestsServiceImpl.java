// File: SupportTicketRequestsServiceImpl.java
package com.example.demo.supportTickets.service;

import com.example.demo.supportTickets.dao.SupportTicketRequestsDAO;
import com.example.demo.supportTickets.model.SupportTicketRequests;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class SupportTicketRequestsServiceImpl implements SupportTicketRequestsService {

    private final SupportTicketRequestsDAO dao;
    private final SupportTicketRequestsDocumentService documentService;

    public SupportTicketRequestsServiceImpl(SupportTicketRequestsDAO dao,
                                            SupportTicketRequestsDocumentService documentService) {
        this.dao = dao;
        this.documentService = documentService;
    }

    @Override
    public SupportTicketRequests createRequest(SupportTicketRequests request, List<MultipartFile> files) {
        SupportTicketRequests saved = dao.createRequest(request);

        if (files != null && !files.isEmpty()) {
            files.stream()
                    .filter(f -> !f.isEmpty())
                    .forEach(file -> documentService.uploadDocument(file, saved.getRequestId()));
        }

        return saved;
    }

    @Override
    public List<SupportTicketRequests> getRequestsByStudent(String studentId) {
        return dao.getRequestsByStudent(studentId);
    }

    @Override
    public SupportTicketRequests getRequestById(String requestId) {
        return dao.getRequestById(requestId);
    }
}