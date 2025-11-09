package com.example.demo.messages.controller;

import com.example.demo.messages.service.MessagesService;
import com.example.demo.user.admin.model.Admins;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import com.example.demo.user.majorLecturer.model.MajorLecturers;
import com.example.demo.user.minorLecturer.model.MinorLecturers;
import com.example.demo.user.person.model.Persons;
import com.example.demo.user.person.service.PersonsService;
import com.example.demo.user.staff.model.Staffs;
import com.example.demo.user.student.model.Students;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/messages")
public class MessagesController {

    private final MessagesService messagesService;
    private final PersonsService personsService;

    public MessagesController(MessagesService messagesService, PersonsService personsService) {
        this.messagesService = messagesService;
        this.personsService = personsService;
    }

    @GetMapping
    public String showMessages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            Model model,
            HttpSession session) {

        String currentUserId = personsService.getPerson().getId();

        // === 1. Danh sách người chat ===
        model.addAttribute("partners", messagesService.getConversationPartners(currentUserId));

        // === 2. Home URL ===
        model.addAttribute("home", getHomeUrl(personsService.getPerson()));

        // === 3. Lấy partner từ session (KHÔNG từ URL) ===
        String partnerId = (String) session.getAttribute("selectedPartnerId");

        if (partnerId != null && !partnerId.isBlank()) {
            Persons partnerPerson = messagesService.getPersonById(partnerId);
            if (partnerPerson != null) {
                model.addAttribute("selectedPartner", partnerPerson);
                model.addAttribute("messages", messagesService.getMessagesWith(currentUserId, partnerId, page, size));
                model.addAttribute("totalMessages", messagesService.countMessagesWith(currentUserId, partnerId));
            } else {
                session.removeAttribute("selectedPartnerId");
            }
        }

        model.addAttribute("currentUserId", currentUserId);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);

        return "MessagesPage";
    }

    // === CHỌN PARTNER → POST → KHÔNG LỘ ID ===
    @PostMapping("/select-partner")
    public String selectPartner(@RequestParam String partnerId, HttpSession session) {
        Persons partner = messagesService.getPersonById(partnerId);
        if (partner != null) {
            session.setAttribute("selectedPartnerId", partnerId);
        }
        return "redirect:/messages"; // → URL luôn là /messages
    }

    // === GỬI TIN NHẮN ===
    @PostMapping("/send")
    @ResponseBody
    public Map<String, Object> sendMessage(@RequestParam String recipientId, @RequestParam String text, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        String senderId = personsService.getPerson().getId();
        if (text != null && !text.trim().isEmpty()) {
            messagesService.sendMessage(senderId, recipientId, text);
            session.setAttribute("selectedPartnerId", recipientId);
            response.put("success", true);
        } else {
            response.put("success", false);
        }
        return response;
    }

    // === HOME URL ===
    private String getHomeUrl(Persons person) {
        if (person instanceof Students) return "/student-home";
        if (person instanceof Staffs) return "/staff-home";
        if (person instanceof DeputyStaffs) return "/deputy-staff-home";
        if (person instanceof Admins) return "/admin-home";
        if (person instanceof MinorLecturers) return "/minor-lecturer-home";
        if (person instanceof MajorLecturers) return "/major-lecturer-home";
        return "/";
    }
}