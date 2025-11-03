// src/main/java/com/example/demo/messages/controller/MessagesController.java
package com.example.demo.messages.controller;

import com.example.demo.messages.dto.MessageDTO;
import com.example.demo.messages.model.Messages;
import com.example.demo.messages.service.MessagesService;
import com.example.demo.user.person.dao.PersonsDAO;
import com.example.demo.user.person.model.Persons;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/messages")
public class MessagesController {

    private final MessagesService messagesService;
    private final PersonsDAO personsDAO;

    public MessagesController(MessagesService messagesService, PersonsDAO personsDAO) {
        this.messagesService = messagesService;
        this.personsDAO = personsDAO;
    }

    @GetMapping
    public String getMessagesPage(Model model, @ModelAttribute("openChatWith") String openChatWith) {
        Persons currentUser = personsDAO.getPerson();
        if (currentUser == null) return "redirect:/login";

        String currentUserId = currentUser.getId(); // ← LẤY ID

        List<Persons> recentUsers = messagesService.getRecentChatUsers(currentUserId);
        Long unreadCount = messagesService.getUnreadCount(currentUserId);

        model.addAttribute("recentUsers", recentUsers);
        model.addAttribute("unreadCount", unreadCount);
        model.addAttribute("openChatWith", openChatWith);
        model.addAttribute("currentUserId", currentUserId); // ← THÊM DÒNG NÀY

        return "messages";
    }

    // MỞ CHAT TỪ CLASS
    @PostMapping("/open-chat")
    public String openChatFromClass(@RequestParam String otherUserId, RedirectAttributes ra) {
        ra.addFlashAttribute("openChatWith", otherUserId);
        return "redirect:/messages";
    }

    @PostMapping("/load-chat")
    @ResponseBody
    public Map<String, Object> loadChat(@RequestBody Map<String, Object> req) {
        String otherUserId = (String) req.get("otherUserId");
        int page = req.containsKey("page") ? (Integer) req.get("page") : 0;

        Persons currentUser = personsDAO.getPerson();
        List<Messages> messages = messagesService.getMessagesBetween(currentUser.getId(), otherUserId, page, 50);
        messagesService.markAsRead(currentUser.getId(), otherUserId);

        // Chuyển sang DTO
        List<MessageDTO> dtoList = messages.stream()
                .map(MessageDTO::new)
                .toList();

        Map<String, Object> res = new HashMap<>();
        res.put("messages", dtoList);
        return res;
    }

    @GetMapping("/user-info")
    @ResponseBody
    public Map<String, Object> getUserInfo(@RequestParam String userId) {
        Persons person = personsDAO.getPersonById(userId);
        if (person == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        Map<String, Object> info = new HashMap<>();
        info.put("id", person.getId());
        info.put("fullName", person.getFullName());
        info.put("avatar", person.getAvatar() != null ? "/persons/avatar/" + person.getId() : null);
        return info;
    }
}