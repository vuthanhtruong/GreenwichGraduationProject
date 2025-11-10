// File: SupportTicketRequestsServiceImpl.java
package com.example.demo.supportTickets.service;

import com.example.demo.accountBalance.service.AccountBalancesService;
import com.example.demo.financialHistory.SupportTicketHistories.model.SupportTicketHistories;
import com.example.demo.financialHistory.SupportTicketHistories.service.SupportTicketHistoriesService;
import com.example.demo.supportTickets.dao.SupportTicketRequestsDAO;
import com.example.demo.supportTickets.model.SupportTicketRequests;
import com.example.demo.supportTickets.model.SupportTickets;
import com.example.demo.user.student.model.Students;
import com.example.demo.entity.Enums.Status;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class SupportTicketRequestsServiceImpl implements SupportTicketRequestsService {
    // File: SupportTicketRequestsServiceImpl.java
    @Override
    public void updateRequest(SupportTicketRequests request) {
        dao.updateRequest(request);
    }

    @Override
    public List<SupportTicketRequests> getPaginatedPendingRequests(int offset, int size, String search) {
        return dao.getPaginatedPendingRequests(offset, size, search);
    }

    @Override
    public long countPendingRequests(String search) {
        return dao.countPendingRequests(search);
    }
    @Override
    public List<SupportTicketRequests> getPaginatedRequestsByStudent(String studentId, int offset, int size, String search) {
        return dao.getPaginatedRequestsByStudent(studentId, offset, size, search);
    }

    @Override
    public long countRequestsByStudent(String studentId, String search) {
        return dao.countRequestsByStudent(studentId, search);
    }

    private final SupportTicketRequestsDAO dao;
    private final SupportTicketRequestsDocumentService documentService;
    private final AccountBalancesService accountBalancesService;
    private final SupportTicketHistoriesService historiesService;
    private final SupportTicketsService supportTicketsService;

    public SupportTicketRequestsServiceImpl(
            SupportTicketRequestsDAO dao,
            SupportTicketRequestsDocumentService documentService,
            AccountBalancesService accountBalancesService,
            SupportTicketHistoriesService historiesService,
            SupportTicketsService supportTicketsService) {
        this.dao = dao;
        this.documentService = documentService;
        this.accountBalancesService = accountBalancesService;
        this.historiesService = historiesService;
        this.supportTicketsService = supportTicketsService;
    }

    @Override
    public SupportTicketRequests createRequest(SupportTicketRequests request, List<MultipartFile> files) {
        // 1. Lấy thông tin gói hỗ trợ
        SupportTickets ticket = supportTicketsService.getTicketById(request.getSupportTicketId());
        if (ticket == null || ticket.getCost() == null || ticket.getCost() <= 0) {
            throw new IllegalStateException("Gói hỗ trợ không hợp lệ hoặc chưa có giá.");
        }

        Students student = request.getRequester();
        double cost = ticket.getCost();

        if (!accountBalancesService.hasSufficientBalance(student.getId(), cost)) {
            throw new IllegalStateException(
                    "Số dư không đủ. Cần: " + String.format("%,.0f", cost) + " VNĐ"
            );
        }
        SupportTicketRequests saved = dao.createRequest(request);
        if (files != null && !files.isEmpty()) {
            files.stream()
                    .filter(f -> !f.isEmpty())
                    .forEach(file -> documentService.uploadDocument(file, saved.getRequestId()));
        }

        // 5. Trừ tiền
        if (!accountBalancesService.deductBalance(student.getId(), cost)) {
            throw new IllegalStateException("Trừ tiền thất bại. Vui lòng thử lại.");
        }

        // 6. Lưu lịch sử
        SupportTicketHistories history = new SupportTicketHistories();
        String historyId = "SUP-HIST-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        history.setHistoryId(historyId);
        history.setStudent(student);
        history.setAccountBalance(accountBalancesService.findByStudentId(student.getId()));
        history.setCurrentAmount(BigDecimal.valueOf(-cost));
        history.setCreatedAt(LocalDateTime.now());
        history.setStatus(Status.COMPLETED);
        history.setSupportTicket(ticket);
        history.setTicketTime(LocalDateTime.now());
        history.setDescription(
                "Support Request: " + ticket.getTicketName() +
                        " | Cost: " + String.format("%,.0f", cost) + " VNĐ" +
                        " | Request ID: " + saved.getRequestId()
        );

        historiesService.createHistory(history);

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