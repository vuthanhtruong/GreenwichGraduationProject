// File: SupportTicketRequestsDocumentDAOImpl.java
package com.example.demo.supportTickets.dao;

import com.example.demo.supportTickets.model.SupportTicketRequestsDocument;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class SupportTicketRequestsDocumentDAOImpl implements SupportTicketRequestsDocumentDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public SupportTicketRequestsDocument save(SupportTicketRequestsDocument document) {
        if (document.getDocumentId() == null) {
            entityManager.persist(document);
        } else {
            entityManager.merge(document);
        }
        return document;
    }

    @Override
    public List<SupportTicketRequestsDocument> getDocumentsByRequestId(String requestId) {
        return entityManager.createQuery(
                        "SELECT d FROM SupportTicketRequestsDocument d " +
                                "WHERE d.supportTicketRequest.requestId = :requestId " +
                                "ORDER BY d.uploadedAt DESC", SupportTicketRequestsDocument.class)
                .setParameter("requestId", requestId)
                .getResultList();
    }

    @Override
    public SupportTicketRequestsDocument getDocumentById(String documentId) {
        return entityManager.find(SupportTicketRequestsDocument.class, documentId);
    }

    @Override
    public void delete(String documentId) {
        SupportTicketRequestsDocument doc = getDocumentById(documentId);
        if (doc != null) {
            entityManager.remove(doc);
        }
    }
}