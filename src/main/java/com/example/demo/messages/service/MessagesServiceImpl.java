// src/main/java/com/example/demo/messages/service/MessagesServiceImpl.java
package com.example.demo.messages.service;

import com.example.demo.messages.dao.MessagesDAO;
import com.example.demo.messages.model.Messages;
import com.example.demo.user.person.model.Persons;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessagesServiceImpl implements MessagesService {

    private final MessagesDAO messagesDAO;

    public MessagesServiceImpl(MessagesDAO messagesDAO) {
        this.messagesDAO = messagesDAO;
    }

    @Override
    public List<Persons> getConversationPartners(String currentUserId) {
        return messagesDAO.getConversationPartners(currentUserId);
    }

    @Override
    public List<Messages> getMessagesWith(String currentUserId, String partnerId, int page, int size) {
        return messagesDAO.getMessagesWith(currentUserId, partnerId, page, size);
    }

    @Override
    public long countMessagesWith(String currentUserId, String partnerId) {
        return messagesDAO.countMessagesWith(currentUserId, partnerId);
    }

    @Override
    public Messages sendMessage(String senderId, String recipientId, String text) {
        return messagesDAO.sendMessage(senderId, recipientId, text);
    }

    @Override
    public Persons getPersonById(String personId) {
        return messagesDAO.getPersonById(personId);
    }
}