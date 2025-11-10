// File: StudentMyRequestsController.java
package com.example.demo.supportTickets.controller;

import com.example.demo.supportTickets.model.SupportTicketRequests;
import com.example.demo.supportTickets.model.SupportTicketRequestsDocument;
import com.example.demo.supportTickets.service.SupportTicketRequestsService;
import com.example.demo.supportTickets.service.SupportTicketRequestsDocumentService;
import com.example.demo.user.student.service.StudentsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/student-home/support-tickets/my-requests")
public class StudentMyRequestsController {

    private final SupportTicketRequestsService requestsService;
    private final SupportTicketRequestsDocumentService documentService;
    private final StudentsService studentsService;

    public StudentMyRequestsController(
            SupportTicketRequestsService requestsService,
            SupportTicketRequestsDocumentService documentService,
            StudentsService studentsService) {
        this.requestsService = requestsService;
        this.documentService = documentService;
        this.studentsService = studentsService;
    }

    @GetMapping
    public String listMyRequests(
            Model model,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            HttpSession session) {

        String studentId = studentsService.getStudent().getId();
        int offset = (page - 1) * size;

        List<SupportTicketRequests> requests = requestsService.getPaginatedRequestsByStudent(
                studentId, offset, size, search);

        long total = requestsService.countRequestsByStudent(studentId, search);
        int totalPages = (int) Math.ceil((double) total / size);

        model.addAttribute("requests", requests);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", total);
        model.addAttribute("search", search);

        return "StudentMyRequestsList";
    }

    @GetMapping("/view/{requestId}")
    public String viewRequestDetail(@PathVariable String requestId, Model model) {
        SupportTicketRequests request = requestsService.getRequestById(requestId);
        if (request == null || !request.getRequester().getId().equals(studentsService.getStudent().getId())) {
            return "redirect:/student-home/support-tickets/my-requests";
        }

        List<SupportTicketRequestsDocument> documents = documentService.getDocumentsByRequestId(requestId);

        model.addAttribute("request", request);
        model.addAttribute("documents", documents);
        return "StudentMyRequestDetail";
    }

}