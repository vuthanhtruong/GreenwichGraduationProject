package com.example.demo.messages.controller;

import com.example.demo.messages.dto.MessageDTO;
import com.example.demo.messages.model.Messages;
import com.example.demo.messages.service.MessagesService;
import com.example.demo.user.person.model.Persons;
import com.example.demo.user.person.service.PersonsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/messages")
public class MessagesController {

    private final MessagesService messagesService;
    private final PersonsService personsService;
    private final SimpMessagingTemplate messagingTemplate;

    public MessagesController(MessagesService messagesService,
                              PersonsService personsService,
                              SimpMessagingTemplate messagingTemplate) {
        this.messagesService = messagesService;
        this.personsService = personsService;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping
    public String showMessages(Model model, HttpSession session) {
        Persons currentUser = personsService.getPerson();
        String currentUserId = currentUser.getId();

        List<Persons> partners = messagesService.getConversationPartners(currentUserId);

        String selectedPartnerId = (String) session.getAttribute("selectedPartnerId");
        Persons selectedPartner = null;
        List<Messages> messages = null;

        if (selectedPartnerId != null && !selectedPartnerId.isBlank()) {
            selectedPartner = messagesService.getPersonById(selectedPartnerId);
            if (selectedPartner != null) {
                messages = messagesService.getMessagesWith(currentUserId, selectedPartnerId, 0, 100);
            } else {
                session.removeAttribute("selectedPartnerId");
            }
        }

        // Map default avatar cho realtime (dự phòng)
        Map<String, String> defaultAvatarMap = new HashMap<>();
        partners.forEach(p -> defaultAvatarMap.put(p.getId(), p.getDefaultAvatarPath()));
        if (selectedPartner != null) defaultAvatarMap.put(selectedPartner.getId(), selectedPartner.getDefaultAvatarPath());
        defaultAvatarMap.put(currentUser.getId(), currentUser.getDefaultAvatarPath());

        model.addAttribute("partners", partners);
        model.addAttribute("selectedPartner", selectedPartner);
        model.addAttribute("messages", messages);
        model.addAttribute("currentUserId", currentUserId);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("defaultAvatarMap", defaultAvatarMap);
        model.addAttribute("home", getHomeUrl(currentUser));

        return "MessagesPage";
    }

    @PostMapping("/select-partner")
    public String selectPartner(@RequestParam String partnerId, HttpSession session) {
        if (messagesService.getPersonById(partnerId) != null) {
            session.setAttribute("selectedPartnerId", partnerId);
        }
        return "redirect:/messages";
    }

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload MessageDTO payload) {
        Persons sender = personsService.getPerson();

        Messages saved = messagesService.sendMessage(
                sender.getId(),
                payload.getRecipientId(),
                payload.getText()
        );

        // Tạo DTO đầy đủ với avatar path đúng
        MessageDTO dto = new MessageDTO(
                saved.getSender().getId(),
                saved.getRecipient().getId(),
                saved.getText(),
                saved.getDatetime(),
                saved.getSender().getDefaultAvatarPath()   // <<< Chính là cái này!
        );

        // Gửi realtime cho cả hai bên
        messagingTemplate.convertAndSend("/topic/messages." + dto.getRecipientId(), dto);
        messagingTemplate.convertAndSend("/topic/messages." + dto.getSenderId(), dto);
    }

    private String getHomeUrl(Persons person) {
        return switch (person) {
            case com.example.demo.user.student.model.Students ignored -> "/student-home";
            case com.example.demo.user.staff.model.Staffs ignored -> "/staff-home";
            case com.example.demo.user.deputyStaff.model.DeputyStaffs ignored -> "/deputy-staff-home";
            case com.example.demo.user.admin.model.Admins ignored -> "/admin-home";
            case com.example.demo.user.minorLecturer.model.MinorLecturers ignored -> "/minor-lecturer-home";
            case com.example.demo.user.majorLecturer.model.MajorLecturers ignored -> "/major-lecturer-home";
            default -> "/";
        };
    }
}