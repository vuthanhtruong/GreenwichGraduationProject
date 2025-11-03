package com.example.demo.messages.service;

import com.example.demo.messages.model.Messages;
import com.example.demo.user.person.model.Persons;

import java.util.List;

public interface MessagesService {
    List<Persons> getRecentChatUsers(String currentUserId); // Limit 20 mặc định
    List<Messages> getMessagesBetween(String currentUserId, String otherUserId, int page, int size);
    Messages sendMessage(String senderId, String recipientId, String text);
    Long getUnreadCount(String currentUserId);
    void markAsRead(String currentUserId, String senderId);
}