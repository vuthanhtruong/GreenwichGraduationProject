package com.example.demo.messages.service;

import com.example.demo.messages.model.Messages;
import com.example.demo.user.person.model.Persons;

import java.util.List;

public interface MessagesService {
    List<Persons> getConversationPartners(String currentUserId);
    List<Messages> getMessagesWith(String currentUserId, String partnerId, int page, int size);
    long countMessagesWith(String currentUserId, String partnerId);
    Messages sendMessage(String senderId, String recipientId, String text);
    Persons getPersonById(String personId);
}