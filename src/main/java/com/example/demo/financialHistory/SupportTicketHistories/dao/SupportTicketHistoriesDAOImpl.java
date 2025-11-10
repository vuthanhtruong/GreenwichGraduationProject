// File: SupportTicketHistoriesDAOImpl.java
package com.example.demo.financialHistory.SupportTicketHistories.dao;

import com.example.demo.financialHistory.SupportTicketHistories.model.SupportTicketHistories;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class SupportTicketHistoriesDAOImpl implements SupportTicketHistoriesDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public SupportTicketHistories createHistory(SupportTicketHistories history) {
        if (history.getHistoryId() == null || history.getHistoryId().trim().isEmpty()) {
            throw new IllegalArgumentException("HistoryID must not be null or empty");
        }
        entityManager.persist(history);
        return history;
    }

    @Override
    public List<SupportTicketHistories> getHistoriesByStudentId(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("StudentID must not be null or empty");
        }

        String jpql = """
            SELECT h FROM SupportTicketHistories h 
            JOIN FETCH h.supportTicket t 
            JOIN FETCH h.student s 
            JOIN FETCH h.accountBalance 
            WHERE s.id = :studentId 
            ORDER BY h.createdAt DESC
            """;

        return entityManager.createQuery(jpql, SupportTicketHistories.class)
                .setParameter("studentId", studentId)
                .getResultList();
    }

    @Override
    public List<SupportTicketHistories> getAllHistories() {
        String jpql = """
            SELECT h FROM SupportTicketHistories h 
            JOIN FETCH h.supportTicket 
            JOIN FETCH h.student 
            JOIN FETCH h.accountBalance 
            ORDER BY h.createdAt DESC
            """;

        return entityManager.createQuery(jpql, SupportTicketHistories.class)
                .getResultList();
    }

    @Override
    public SupportTicketHistories getHistoryById(String historyId) {
        if (historyId == null || historyId.trim().isEmpty()) {
            return null;
        }

        String jpql = """
            SELECT h FROM SupportTicketHistories h 
            JOIN FETCH h.supportTicket 
            JOIN FETCH h.student 
            JOIN FETCH h.accountBalance 
            WHERE h.historyId = :historyId
            """;

        TypedQuery<SupportTicketHistories> query = entityManager.createQuery(jpql, SupportTicketHistories.class);
        query.setParameter("historyId", historyId);

        try {
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public long countByStudentId(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            return 0L;
        }

        String jpql = "SELECT COUNT(h) FROM SupportTicketHistories h WHERE h.student.id = :studentId";
        return entityManager.createQuery(jpql, Long.class)
                .setParameter("studentId", studentId)
                .getSingleResult();
    }
}