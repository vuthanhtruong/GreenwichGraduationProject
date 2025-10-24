// com.example.demo.SupportTickets.dao.SupportTicketsDAOImpl.java
package com.example.demo.supportTickets.dao;

import com.example.demo.supportTickets.model.SupportTickets;
import com.example.demo.user.admin.service.AdminsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class SupportTicketsDAOImpl implements SupportTicketsDAO {

    @PersistenceContext
    private EntityManager entityManager;
    private final AdminsService adminsService;

    public SupportTicketsDAOImpl(AdminsService adminsService) {
        this.adminsService = adminsService;
    }

    @Override
    public List<SupportTickets> getPaginatedTickets(int firstResult, int pageSize) {
        return entityManager.createQuery(
                        "SELECT t FROM SupportTickets t JOIN FETCH t.creator ORDER BY t.createdAt DESC", SupportTickets.class)
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public long numberOfTickets() {
        return entityManager.createQuery("SELECT COUNT(t) FROM SupportTickets t", Long.class)
                .getSingleResult();
    }

    @Override
    public SupportTickets getTicketById(String id) {
        return entityManager.find(SupportTickets.class, id);
    }

    @Override
    public SupportTickets addTicket(SupportTickets ticket) {
        ticket.setCreator(adminsService.getAdmin());
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setSupportTicketId(generateUniqueTicketId());
        entityManager.persist(ticket);
        return ticket;
    }

    @Override
    public void updateTicket(String id, SupportTickets updated) {
        SupportTickets existing = getTicketById(id);
        if (existing == null) throw new IllegalArgumentException("Ticket not found");

        existing.setTicketName(updated.getTicketName());
        existing.setDescription(updated.getDescription());
        existing.setCost(updated.getCost());
        entityManager.merge(existing);
    }

    @Override
    public void deleteTicket(String id) {
        SupportTickets ticket = getTicketById(id);
        if (ticket != null) {
            entityManager.remove(ticket);
        }
    }

    @Override
    public Map<String, String> validateTicket(SupportTickets ticket) {
        Map<String, String> errors = new HashMap<>();

        if (ticket.getTicketName() == null || ticket.getTicketName().trim().isEmpty()) {
            errors.put("ticketName", "Ticket name is required.");
        } else if (ticket.getTicketName().length() > 255) {
            errors.put("ticketName", "Ticket name must not exceed 255 characters.");
        }

        if (ticket.getCost() == null) {
            errors.put("cost", "Cost is required.");
        } else if (ticket.getCost() < 0) {
            errors.put("cost", "Cost cannot be negative.");
        }

        if (ticket.getDescription() != null && ticket.getDescription().length() > 1000) {
            errors.put("description", "Description must not exceed 1000 characters.");
        }

        return errors;
    }

    @Override
    public List<SupportTickets> searchTickets(String searchType, String keyword, int firstResult, int pageSize) {
        String jpql = "SELECT t FROM SupportTickets t JOIN FETCH t.creator WHERE 1=1";
        Map<String, Object> params = new HashMap<>();

        if ("name".equalsIgnoreCase(searchType) && keyword != null && !keyword.trim().isEmpty()) {
            String[] words = keyword.toLowerCase().trim().split("\\s+");
            StringBuilder condition = new StringBuilder();
            for (int i = 0; i < words.length; i++) {
                if (i > 0) condition.append(" AND ");
                condition.append("(LOWER(t.ticketName) LIKE :word").append(i).append(")");
                params.put("word" + i, "%" + words[i] + "%");
            }
            jpql += " AND " + condition;
        } else if ("id".equalsIgnoreCase(searchType) && keyword != null && !keyword.trim().isEmpty()) {
            jpql += " AND LOWER(t.supportTicketId) = LOWER(:keyword)";
            params.put("keyword", keyword.trim());
        } else {
            return List.of();
        }

        var query = entityManager.createQuery(jpql, SupportTickets.class);
        params.forEach(query::setParameter);
        return query.setFirstResult(firstResult).setMaxResults(pageSize).getResultList();
    }

    @Override
    public long countSearchResults(String searchType, String keyword) {
        String jpql = "SELECT COUNT(t) FROM SupportTickets t WHERE 1=1";
        Map<String, Object> params = new HashMap<>();

        if ("name".equalsIgnoreCase(searchType) && keyword != null && !keyword.trim().isEmpty()) {
            String[] words = keyword.toLowerCase().trim().split("\\s+");
            StringBuilder condition = new StringBuilder();
            for (int i = 0; i < words.length; i++) {
                if (i > 0) condition.append(" AND ");
                condition.append("(LOWER(t.ticketName) LIKE :word").append(i).append(")");
                params.put("word" + i, "%" + words[i] + "%");
            }
            jpql += " AND " + condition;
        } else if ("id".equalsIgnoreCase(searchType) && keyword != null && !keyword.trim().isEmpty()) {
            jpql += " AND LOWER(t.supportTicketId) = LOWER(:keyword)";
            params.put("keyword", keyword.trim());
        } else {
            return 0L;
        }

        var query = entityManager.createQuery(jpql, Long.class);
        params.forEach(query::setParameter);
        return query.getSingleResult();
    }

    @Override
    public String generateUniqueTicketId() {
        SecureRandom random = new SecureRandom();
        String id;
        do {
            id = "TKT" + String.format("%08d", random.nextInt(100000000));
        } while (getTicketById(id) != null);
        return id;
    }
}