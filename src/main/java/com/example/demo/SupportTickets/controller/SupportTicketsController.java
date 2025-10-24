// com.example.demo.SupportTickets.controller.SupportTicketsController.java
package com.example.demo.SupportTickets.controller;

import com.example.demo.SupportTickets.model.SupportTickets;
import com.example.demo.SupportTickets.service.SupportTicketsService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/admin-home/support-tickets")
public class SupportTicketsController {

    private final SupportTicketsService service;

    public SupportTicketsController(SupportTicketsService service) {
        this.service = service;
    }

    @GetMapping
    public String listTickets(
            Model model,
            HttpSession session,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize) {

        if (pageSize == null) {
            pageSize = (Integer) session.getAttribute("ticketPageSize");
            if (pageSize == null) pageSize = 10;
        }
        session.setAttribute("ticketPageSize", pageSize);

        long total = service.numberOfTickets();
        int totalPages = Math.max(1, (int) Math.ceil((double) total / pageSize));
        page = Math.max(1, Math.min(page, totalPages));
        session.setAttribute("ticketPage", page);
        session.setAttribute("ticketTotalPages", totalPages);

        int firstResult = (page - 1) * pageSize;
        model.addAttribute("tickets", service.getPaginatedTickets(firstResult, pageSize));
        model.addAttribute("ticket", new SupportTickets());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalTickets", total);
        return "SupportTicketsList";
    }

    @PostMapping("/add-ticket")
    public String addTicket(
            @Valid @ModelAttribute("ticket") SupportTickets ticket,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        Map<String, String> errors = service.validateTicket(ticket);
        if (!errors.isEmpty()) {
            model.addAttribute("openAddOverlay", true);
            model.addAttribute("errors", errors);
            model.addAttribute("ticket", ticket);
            return loadList(model, session);
        }

        service.addTicket(ticket);
        redirectAttributes.addFlashAttribute("message", "Support ticket added successfully!");
        return "redirect:/admin-home/support-tickets";
    }

    @PostMapping("/edit-ticket-form")
    public String showEditForm(@RequestParam String id, Model model) {
        SupportTickets ticket = service.getTicketById(id);
        if (ticket == null) {
            return "redirect:/admin-home/support-tickets?error=Not+found";
        }
        model.addAttribute("ticket", ticket);
        return "EditSupportTicket";
    }

    @PutMapping("/edit-ticket-form")
    public String updateTicket(
            @Valid @ModelAttribute("ticket") SupportTickets ticket,
            Model model,
            RedirectAttributes redirectAttributes) {

        Map<String, String> errors = service.validateTicket(ticket);
        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            return "EditSupportTicket";
        }

        try {
            service.updateTicket(ticket.getSupportTicketId(), ticket);
            redirectAttributes.addFlashAttribute("message", "Ticket updated successfully!");
            return "redirect:/admin-home/support-tickets";
        } catch (Exception e) {
            model.addAttribute("errors", Map.of("general", "Update failed: " + e.getMessage()));
            return "EditSupportTicket";
        }
    }

    @PostMapping("/delete-ticket/{id}")
    public String deleteTicket(@PathVariable String id, RedirectAttributes redirectAttributes) {
        service.deleteTicket(id);
        redirectAttributes.addFlashAttribute("message", "Ticket deleted!");
        return "redirect:/admin-home/support-tickets";
    }

    private String loadList(Model model, HttpSession session) {
        int page = (Integer) session.getAttribute("ticketPage") != null ? (Integer) session.getAttribute("ticketPage") : 1;
        int pageSize = (Integer) session.getAttribute("ticketPageSize") != null ? (Integer) session.getAttribute("ticketPageSize") : 10;
        int firstResult = (page - 1) * pageSize;
        model.addAttribute("tickets", service.getPaginatedTickets(firstResult, pageSize));
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", session.getAttribute("ticketTotalPages"));
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalTickets", service.numberOfTickets());
        return "SupportTicketsList";
    }
}