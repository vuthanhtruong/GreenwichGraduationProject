package com.example.demo.messages.service;

import com.example.demo.messages.dao.MessagesDAO;
import com.example.demo.messages.model.Messages;
import com.example.demo.user.person.model.Persons;
import com.example.demo.user.person.service.PersonsService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MessagesServiceImpl implements MessagesService {

    private final MessagesDAO messagesDAO;
    private final PersonsService personsService;

    public MessagesServiceImpl(MessagesDAO messagesDAO, PersonsService personsService) {
        this.messagesDAO = messagesDAO;
        this.personsService = personsService;
    }

    @Override
    public List<Persons> getRecentChatUsers(String currentUserId) {
        return messagesDAO.getRecentChatUsers(currentUserId, 20);
    }

    @Override
    public List<Messages> getMessagesBetween(String currentUserId, String otherUserId, int page, int size) {
        return messagesDAO.getMessagesBetweenUsers(currentUserId, otherUserId, page, size);
    }

    @Override
    public Messages sendMessage(String senderId, String recipientId, String text) {
        Persons sender = personsService.getPersonById(senderId);
        Persons recipient = personsService.getPersonById(recipientId);

        if (sender == null || recipient == null) {
            throw new IllegalArgumentException("Sender or recipient not found");
        }

        Messages message = new Messages(
                UUID.randomUUID().toString(),
                sender,
                recipient,
                LocalDateTime.now()
        );
        message.setText(text);

        return messagesDAO.save(message);
    }

    @Override
    public Long getUnreadCount(String currentUserId) {
        return messagesDAO.countUnreadMessages(currentUserId);
    }

    @Override
    public void markAsRead(String currentUserId, String senderId) {
        messagesDAO.markAsRead(currentUserId, senderId);
    }
}