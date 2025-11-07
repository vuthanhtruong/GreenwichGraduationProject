package com.example.demo.messages.controller;

import com.example.demo.messages.service.MessagesService;
import com.example.demo.user.person.model.Persons;
import com.example.demo.user.person.service.PersonsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
            @RequestParam(required = false) String partner,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            Model model,
            Authentication auth,
            HttpSession session) {

        String currentUserId = personsService.getPerson().getId();

        model.addAttribute("partners", messagesService.getConversationPartners(currentUserId));

        String partnerId = partner;
        if (partnerId == null || partnerId.isBlank()) {
            partnerId = (String) session.getAttribute("selectedPartnerId");
        }

        if (partnerId != null && !partnerId.isBlank()) {
            Persons partnerPerson = messagesService.getPersonById(partnerId);

            if (partnerPerson != null) {
                model.addAttribute("selectedPartner", partnerPerson);

                model.addAttribute("messages", messagesService.getMessagesWith(currentUserId, partnerId, page, size));
                model.addAttribute("totalMessages", messagesService.countMessagesWith(currentUserId, partnerId));
                model.addAttribute("currentPage", page);
                model.addAttribute("pageSize", size);
            } else {
                model.addAttribute("errorMessage", "User not found");
            }
        }

        model.addAttribute("currentUserId", currentUserId);
        return "MessagesPage";
    }

    @PostMapping("/select-partner")
    public String selectPartner(@RequestParam String partnerId) {
        return "redirect:/messages?partner=" + partnerId;
    }

    @PostMapping("/send")
    public String sendMessage(
            @RequestParam String recipientId,
            @RequestParam String text,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {

        String senderId = personsService.getPerson().getId();

        if (text != null && !text.trim().isEmpty()) {
            messagesService.sendMessage(senderId, recipientId, text);
        }

        return "redirect:/messages?partner=" + recipientId + "&page=" + page + "&size=" + size;
    }
}