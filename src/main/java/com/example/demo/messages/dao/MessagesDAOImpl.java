// src/main/java/com/example/demo/messages/dao/MessagesDAOImpl.java
package com.example.demo.messages.dao;

import com.example.demo.messages.model.Messages;
import com.example.demo.user.person.model.Persons;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public class MessagesDAOImpl implements MessagesDAO {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Persons> getConversationPartners(String currentUserId) {
        String jpql = """
            SELECT DISTINCT p FROM Persons p
            WHERE p.id IN (
                SELECT m.sender.id FROM Messages m WHERE m.recipient.id = :currentUserId
                UNION
                SELECT m.recipient.id FROM Messages m WHERE m.sender.id = :currentUserId
            )
            AND p.id != :currentUserId
            ORDER BY p.firstName, p.lastName
            """;

        return em.createQuery(jpql, Persons.class)
                .setParameter("currentUserId", currentUserId)
                .getResultList();
    }

    @Override
    public List<Messages> getMessagesWith(String currentUserId, String partnerId, int page, int size) {
        String jpql = """
            FROM Messages m
            WHERE (m.sender.id = :userId1 AND m.recipient.id = :userId2)
               OR (m.sender.id = :userId2 AND m.recipient.id = :userId1)
            ORDER BY m.datetime ASC
            """;

        return em.createQuery(jpql, Messages.class)
                .setParameter("userId1", currentUserId)
                .setParameter("userId2", partnerId)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    @Override
    public long countMessagesWith(String currentUserId, String partnerId) {
        String jpql = """
            SELECT COUNT(m) FROM Messages m
            WHERE (m.sender.id = :userId1 AND m.recipient.id = :userId2)
               OR (m.sender.id = :userId2 AND m.recipient.id = :userId1)
            """;

        return em.createQuery(jpql, Long.class)
                .setParameter("userId1", currentUserId)
                .setParameter("userId2", partnerId)
                .getSingleResult();
    }

    @Override
    @Transactional
    public Messages sendMessage(String senderId, String recipientId, String text) {
        Persons sender = em.find(Persons.class, senderId);
        Persons recipient = em.find(Persons.class, recipientId);

        if (sender == null || recipient == null) {
            throw new IllegalArgumentException("Sender or recipient not found");
        }
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Message text cannot be empty");
        }

        Messages message = new Messages();
        message.setMessageId(UUID.randomUUID().toString());
        message.setSender(sender);
        message.setRecipient(recipient);
        message.setText(text.trim());
        message.setDatetime(LocalDateTime.now());

        em.persist(message);
        return message;
    }

    @Override
    public Persons getPersonById(String personId) {
        return em.find(Persons.class, personId);
    }
}