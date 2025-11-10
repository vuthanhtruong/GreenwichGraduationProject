// File: SupportTicketRequestsDAOImpl.java
package com.example.demo.supportTickets.dao;

import com.example.demo.entity.Enums.Status;
import com.example.demo.supportTickets.model.SupportTicketRequests;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class SupportTicketRequestsDAOImpl implements SupportTicketRequestsDAO {
    // File: SupportTicketRequestsDAOImpl.java
    @Override
    public void updateRequest(SupportTicketRequests request) {
        entityManager.merge(request);
    }

    @Override
    public List<SupportTicketRequests> getPaginatedPendingRequests(int offset, int size, String search) {
        String jpql = """
        SELECT r FROM SupportTicketRequests r 
        JOIN FETCH r.requester 
        LEFT JOIN FETCH r.handler 
        LEFT JOIN FETCH r.documents 
        WHERE r.status = :status
        """;

        if (search != null && !search.trim().isEmpty()) {
            jpql += " AND (LOWER(r.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(r.description) LIKE LOWER(CONCAT('%', :search, '%')))";
        }

        jpql += " ORDER BY r.createdAt DESC";

        var query = entityManager.createQuery(jpql, SupportTicketRequests.class);
        query.setParameter("status", Status.PROCESSING);
        if (search != null && !search.trim().isEmpty()) {
            query.setParameter("search", search.trim());
        }
        return query.setFirstResult(offset).setMaxResults(size).getResultList();
    }

    @Override
    public long countPendingRequests(String search) {
        String jpql = "SELECT COUNT(r) FROM SupportTicketRequests r WHERE r.status = :status";

        if (search != null && !search.trim().isEmpty()) {
            jpql += " AND (LOWER(r.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(r.description) LIKE LOWER(CONCAT('%', :search, '%')))";
        }

        var query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("status", Status.PROCESSING);
        if (search != null && !search.trim().isEmpty()) {
            query.setParameter("search", search.trim());
        }
        return query.getSingleResult();
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public SupportTicketRequests createRequest(SupportTicketRequests request) {
        entityManager.persist(request);
        return request;
    }

    @Override
    public List<SupportTicketRequests> getRequestsByStudent(String studentId) {
        return entityManager.createQuery(
                        "SELECT r FROM SupportTicketRequests r " +
                                "JOIN FETCH r.requester " +
                                "LEFT JOIN FETCH r.handler " +
                                "LEFT JOIN FETCH r.documents " +
                                "WHERE r.requester.id = :studentId " +
                                "ORDER BY r.createdAt DESC", SupportTicketRequests.class)
                .setParameter("studentId", studentId)
                .getResultList();
    }

    @Override
    public SupportTicketRequests getRequestById(String requestId) {
        return entityManager.find(SupportTicketRequests.class, requestId);
    }
    @Override
    public List<SupportTicketRequests> getPaginatedRequestsByStudent(String studentId, int offset, int size, String search) {
        String jpql = """
        SELECT r FROM SupportTicketRequests r 
        JOIN FETCH r.requester 
        LEFT JOIN FETCH r.handler 
        LEFT JOIN FETCH r.documents 
        WHERE r.requester.id = :studentId
        """;

        if (search != null && !search.trim().isEmpty()) {
            jpql += " AND (LOWER(r.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(r.description) LIKE LOWER(CONCAT('%', :search, '%')))";
        }

        jpql += " ORDER BY r.createdAt DESC";

        var query = entityManager.createQuery(jpql, SupportTicketRequests.class);
        query.setParameter("studentId", studentId);
        if (search != null && !search.trim().isEmpty()) {
            query.setParameter("search", search.trim());
        }
        return query.setFirstResult(offset).setMaxResults(size).getResultList();
    }

    @Override
    public long countRequestsByStudent(String studentId, String search) {
        String jpql = "SELECT COUNT(r) FROM SupportTicketRequests r WHERE r.requester.id = :studentId";

        if (search != null && !search.trim().isEmpty()) {
            jpql += " AND (LOWER(r.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(r.description) LIKE LOWER(CONCAT('%', :search, '%')))";
        }

        var query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("studentId", studentId);
        if (search != null && !search.trim().isEmpty()) {
            query.setParameter("search", search.trim());
        }
        return query.getSingleResult();
    }
}