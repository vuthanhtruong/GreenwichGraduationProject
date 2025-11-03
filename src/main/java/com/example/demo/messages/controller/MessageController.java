package com.example.demo.messages.controller;

import com.example.demo.messages.dto.MessageDTO;
import com.example.demo.messages.dto.MessageRequest;
import com.example.demo.messages.model.Messages;
import com.example.demo.messages.service.MessagesService;
import com.example.demo.user.person.dao.PersonsDAO;
import com.example.demo.user.person.model.Persons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.security.Principal;

@Controller
public class MessageController {

    private static final Logger log = LoggerFactory.getLogger(MessageController.class);
    private final MessagesService messagesService;
    private final SimpMessagingTemplate messagingTemplate;
    private final PersonsDAO personsDAO;

    public MessageController(MessagesService messagesService,
                             SimpMessagingTemplate messagingTemplate,
                             PersonsDAO personsDAO) {
        this.messagesService = messagesService;
        this.messagingTemplate = messagingTemplate;
        this.personsDAO = personsDAO;
    }

    @MessageMapping("/chat.send")
    public void sendMessage(MessageRequest request, Principal principal) {
        if (principal == null || principal.getName() == null) {
            log.error("❌ No principal found");
            return;
        }

        String email = principal.getName();

        try {
            Persons sender = personsDAO.getPersonByEmail(email);
            if (sender == null) {
                log.error("❌ Sender not found: {}", email);
                return;
            }

            String senderId = sender.getId();
            log.info("📨 {} → {}: {}", senderId, request.getRecipientId(), request.getText());

            Messages message = messagesService.sendMessage(senderId, request.getRecipientId(), request.getText());
            MessageDTO dto = new MessageDTO(message);

            // Gửi cho người nhận
            messagingTemplate.convertAndSendToUser(
                    request.getRecipientId(),
                    "/queue/messages",
                    dto
            );

            // Gửi lại cho người gửi
            messagingTemplate.convertAndSendToUser(
                    senderId,
                    "/queue/messages",
                    dto
            );

            log.info("✅ Message sent successfully");

        } catch (Exception e) {
            log.error("❌ Error: {}", e.getMessage(), e);
        }
    }
}