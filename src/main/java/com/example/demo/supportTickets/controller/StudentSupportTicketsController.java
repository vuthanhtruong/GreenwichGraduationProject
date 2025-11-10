// File: StudentSupportTicketsController.java
package com.example.demo.supportTickets.controller;

import com.example.demo.supportTickets.model.SupportTicketRequests;
import com.example.demo.supportTickets.model.SupportTickets;
import com.example.demo.supportTickets.service.SupportTicketRequestsService;
import com.example.demo.supportTickets.service.SupportTicketsService;
import com.example.demo.user.student.service.StudentsService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/student-home/support-tickets")
public class StudentSupportTicketsController {

    private final SupportTicketsService ticketsService;
    private final SupportTicketRequestsService requestService;
    private final StudentsService studentsService;

    public StudentSupportTicketsController(SupportTicketsService ticketsService,
                                           SupportTicketRequestsService requestService,
                                           StudentsService studentsService) {
        this.ticketsService = ticketsService;
        this.requestService = requestService;
        this.studentsService = studentsService;
    }

    @GetMapping
    public String listTickets(
            Model model,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword) {

        searchType = (searchType == null || searchType.isEmpty()) ? "name" : searchType;
        keyword = (keyword == null) ? "" : keyword.trim();

        List<SupportTickets> tickets = ticketsService.getAllTickets(); // LẤY TẤT CẢ
        long totalTickets = tickets.size();

        model.addAttribute("tickets", tickets);
        model.addAttribute("totalTickets", totalTickets);
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);

        return "StudentSupportTicketsList";
    }

    @PostMapping("/set-current/{id}")
    @ResponseBody
    public String setCurrentTicket(@PathVariable String id, HttpSession session) {
        SupportTickets ticket = ticketsService.getTicketById(id);
        if (ticket != null) {
            session.setAttribute("currentTicket", ticket);
            return "OK";
        }
        return "NOT_FOUND";
    }

    @GetMapping("/view/current")
    public String viewCurrentTicket(HttpSession session, Model model) {
        SupportTickets ticket = (SupportTickets) session.getAttribute("currentTicket");
        if (ticket == null) {
            return "redirect:/student-home/support-tickets";
        }
        model.addAttribute("ticket", ticket);
        model.addAttribute("request", new SupportTicketRequests());
        model.addAttribute("student", studentsService.getStudent());
        return "StudentSupportTicketDetail";
    }

    @PostMapping(value = "/request", consumes = "multipart/form-data")
    public String createRequest(
            @Valid @ModelAttribute("request") SupportTicketRequests request,
            BindingResult result,
            @RequestParam String studentId,
            @RequestParam(required = false) List<MultipartFile> files,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model) {

        SupportTickets ticket = (SupportTickets) session.getAttribute("currentTicket");
        if (ticket == null) {
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            redirectAttributes.addFlashAttribute("alertMessage", "Không tìm thấy gói hỗ trợ!");
            return "redirect:/student-home/support-tickets";
        }

        if (result.hasErrors()) {
            model.addAttribute("ticket", ticket);
            model.addAttribute("student", studentsService.getStudent());
            return "StudentSupportTicketDetail";
        }

        try {
            var student = studentsService.getStudentById(studentId);
            if (student == null) throw new IllegalArgumentException("Sinh viên không tồn tại");

            request.setRequester(student);
            request.setSupportTicketId(ticket.getSupportTicketId());

            requestService.createRequest(request, files);
            session.removeAttribute("currentTicket");

            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
            redirectAttributes.addFlashAttribute("alertMessage", "Yêu cầu đã được gửi thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            redirectAttributes.addFlashAttribute("alertMessage", "Lỗi: " + e.getMessage());
        }

        return "redirect:/student-home/support-tickets/view/current";
    }

}