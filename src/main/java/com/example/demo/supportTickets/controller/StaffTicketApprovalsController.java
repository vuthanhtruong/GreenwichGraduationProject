// File: StaffTicketApprovalsController.java
package com.example.demo.supportTickets.controller;

import com.example.demo.entity.Enums.Status;
import com.example.demo.supportTickets.model.SupportTicketRequests;
import com.example.demo.document.model.SupportTicketRequestsDocument;
import com.example.demo.document.service.SupportTicketRequestsDocumentService;
import com.example.demo.supportTickets.service.SupportTicketRequestsService;
import com.example.demo.supportTickets.service.SupportTicketsService;
import com.example.demo.user.staff.service.StaffsService;
import com.example.demo.accountBalance.service.AccountBalancesService;
import com.example.demo.financialHistory.SupportTicketHistories.model.SupportTicketHistories;
import com.example.demo.financialHistory.SupportTicketHistories.service.SupportTicketHistoriesService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/staff-home/ticket-approvals")
public class StaffTicketApprovalsController {

    private final SupportTicketRequestsService requestsService;
    private final SupportTicketRequestsDocumentService documentService;
    private final SupportTicketsService ticketsService;
    private final StaffsService staffsService;
    private final AccountBalancesService accountBalancesService;
    private final SupportTicketHistoriesService historiesService;

    public StaffTicketApprovalsController(
            SupportTicketRequestsService requestsService,
            SupportTicketRequestsDocumentService documentService,
            SupportTicketsService ticketsService,
            StaffsService staffsService,
            AccountBalancesService accountBalancesService,
            SupportTicketHistoriesService historiesService) {
        this.requestsService = requestsService;
        this.documentService = documentService;
        this.ticketsService = ticketsService;
        this.staffsService = staffsService;
        this.accountBalancesService = accountBalancesService;
        this.historiesService = historiesService;
    }

    @GetMapping
    public String listPendingRequests(
            Model model,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false) String search) {

        int offset = (page - 1) * size;

        List<SupportTicketRequests> requests = requestsService.getPaginatedPendingRequests(offset, size, search);
        long total = requestsService.countPendingRequests(search);
        int totalPages = (int) Math.ceil((double) total / size);

        model.addAttribute("requests", requests);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", total);
        model.addAttribute("search", search);

        return "StaffTicketApprovalsList";
    }

    // XEM CHI TIẾT — DÙNG POST ĐỂ TRUYỀN ID ẨN
    @PostMapping("/view")
    public String viewRequestDetail(@RequestParam String requestId, Model model, RedirectAttributes ra) {
        SupportTicketRequests request = requestsService.getRequestById(requestId);
        if (request == null || request.getStatus() != Status.PROCESSING) {
            ra.addFlashAttribute("alertClass", "alert-danger");
            ra.addFlashAttribute("alertMessage", "Request not found or not pending.");
            return "redirect:/staff-home/ticket-approvals";
        }

        List<SupportTicketRequestsDocument> documents = documentService.getDocumentsByRequestId(requestId);
        var ticket = ticketsService.getTicketById(request.getSupportTicketId());

        model.addAttribute("request", request);
        model.addAttribute("documents", documents);
        model.addAttribute("ticket", ticket);
        return "StaffTicketApprovalDetail";
    }

    // PHÊ DUYỆT — DÙNG POST
    @PostMapping("/approve")
    public String approveRequest(
            @RequestParam String requestId,
            @RequestParam(required = false) String note,
            RedirectAttributes redirectAttributes) {

        try {
            SupportTicketRequests request = requestsService.getRequestById(requestId);
            if (request == null || request.getStatus() != Status.PROCESSING) {
                throw new IllegalStateException("Request is not pending.");
            }

            request.setStatus(Status.COMPLETED);
            request.setHandler(staffsService.getStaff());
            request.setCompletedAt(LocalDateTime.now());
            request.setUpdatedAt(LocalDateTime.now());
            requestsService.updateRequest(request);

            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
            redirectAttributes.addFlashAttribute("alertMessage", "Request approved successfully!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            redirectAttributes.addFlashAttribute("alertMessage", "Error: " + e.getMessage());
        }

        return "redirect:/staff-home/ticket-approvals";
    }

    // TỪ CHỐI + HOÀN TIỀN — DÙNG POST
    @PostMapping("/reject")
    public String rejectRequest(
            @RequestParam String requestId,
            @RequestParam(required = false) String note,
            RedirectAttributes redirectAttributes) {

        try {
            SupportTicketRequests request = requestsService.getRequestById(requestId);
            if (request == null || request.getStatus() != Status.PROCESSING) {
                throw new IllegalStateException("Request is not pending.");
            }

            var ticket = ticketsService.getTicketById(request.getSupportTicketId());
            double cost = ticket != null && ticket.getCost() != null ? ticket.getCost() : 0.0;

            if (cost > 0) {
                accountBalancesService.refundBalance(request.getRequester().getId(), cost);
            }

            request.setStatus(Status.CANCELLED);
            request.setHandler(staffsService.getStaff());
            request.setCompletedAt(LocalDateTime.now());
            request.setUpdatedAt(LocalDateTime.now());
            requestsService.updateRequest(request);

            if (cost > 0) {
                SupportTicketHistories history = new SupportTicketHistories();
                String historyId = "REF-HIST-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

                history.setHistoryId(historyId);
                history.setStudent(request.getRequester());
                history.setAccountBalance(accountBalancesService.findByStudentId(request.getRequester().getId()));
                history.setCurrentAmount(BigDecimal.valueOf(cost));
                history.setCreatedAt(LocalDateTime.now());
                history.setStatus(Status.COMPLETED);
                history.setSupportTicket(ticket);
                history.setDescription(
                        "Refund for cancelled support request: " + ticket.getTicketName() +
                                " | Amount: " + String.format("%,.0f", cost) + " VNĐ" +
                                " | Request ID: " + requestId
                );

                historiesService.createHistory(history);
            }

            redirectAttributes.addFlashAttribute("alertClass", "alert-info");
            redirectAttributes.addFlashAttribute("alertMessage", "Request rejected and refund issued: " + String.format("%,.0f", cost) + " VNĐ.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            redirectAttributes.addFlashAttribute("alertMessage", "Error: " + e.getMessage());
        }

        return "redirect:/staff-home/ticket-approvals";
    }

    // TẢI FILE — DÙNG POST + HIDDEN ID
    @PostMapping("/download")
    public ResponseEntity<ByteArrayResource> downloadDocument(@RequestParam String documentId) {
        var doc = documentService.getDocumentById(documentId);
        if (doc == null) {
            return ResponseEntity.notFound().build();
        }

        ByteArrayResource resource = documentService.downloadDocument(documentId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(doc.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getFileName() + "\"")
                .body(resource);
    }
}