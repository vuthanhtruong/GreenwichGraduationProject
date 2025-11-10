// File: SupportTicketRequestsDAOImpl.java
package com.example.demo.supportTickets.dao;

import com.example.demo.supportTickets.model.SupportTicketRequests;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class SupportTicketRequestsDAOImpl implements SupportTicketRequestsDAO {

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
}