package com.example.demo.messages.dao;

import com.example.demo.messages.model.Messages;
import com.example.demo.user.person.model.Persons;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
public class MessagesDAOImpl implements MessagesDAO {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Persons> getRecentChatUsers(String currentUserId, int limit) {
        String jpql = """
        SELECT p 
        FROM Messages m 
        JOIN m.sender p 
        WHERE m.recipient.id = :currentUserId 
           OR (m.sender.id = :currentUserId AND m.recipient.id <> :currentUserId)
        GROUP BY p.id 
        ORDER BY MAX(m.datetime) DESC
        """;

        return em.createQuery(jpql, Persons.class)
                .setParameter("currentUserId", currentUserId)
                .setMaxResults(limit)
                .getResultList();
    }

    @Override
    public List<Messages> getMessagesBetweenUsers(String user1Id, String user2Id, int page, int size) {
        String jpql = """
            SELECT m FROM Messages m 
            WHERE (m.sender.id = :user1Id AND m.recipient.id = :user2Id) 
            OR (m.sender.id = :user2Id AND m.recipient.id = :user1Id) 
            ORDER BY m.datetime ASC
            """;
        TypedQuery<Messages> query = em.createQuery(jpql, Messages.class)
                .setParameter("user1Id", user1Id)
                .setParameter("user2Id", user2Id)
                .setFirstResult(page * size)
                .setMaxResults(size);
        return query.getResultList();
    }

    @Override
    public Messages save(Messages message) {
        if (message.getMessageId() == null) {
            em.persist(message);
        } else {
            em.merge(message);
        }
        return message;
    }

    @Override
    public Long countUnreadMessages(String recipientId) {
        String jpql = "SELECT COUNT(m) FROM Messages m WHERE m.recipient.id = :recipientId AND m.notification = 'NOTIFICATION_002'";
        return em.createQuery(jpql, Long.class)
                .setParameter("recipientId", recipientId)
                .getSingleResult();
    }

    @Override
    public void markAsRead(String currentUserId, String senderId) {
        // Cập nhật notification cho tin nhắn từ sender này
        em.createQuery("""
            UPDATE Messages m SET m.notification = null 
            WHERE m.recipient.id = :currentUserId AND m.sender.id = :senderId
            """)
                .setParameter("currentUserId", currentUserId)
                .setParameter("senderId", senderId)
                .executeUpdate();
    }

    @Override
    public Messages getLatestMessage(String user1Id, String user2Id) {
        String jpql = """
            SELECT m FROM Messages m 
            WHERE (m.sender.id = :user1Id AND m.recipient.id = :user2Id) 
            OR (m.sender.id = :user2Id AND m.recipient.id = :user1Id) 
            ORDER BY m.datetime DESC
            """;
        TypedQuery<Messages> query = em.createQuery(jpql, Messages.class)
                .setParameter("user1Id", user1Id)
                .setParameter("user2Id", user2Id)
                .setMaxResults(1);
        return query.getResultList().stream().findFirst().orElse(null);
    }
}