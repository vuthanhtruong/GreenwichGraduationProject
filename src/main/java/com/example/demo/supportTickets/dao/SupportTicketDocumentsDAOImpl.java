package com.example.demo.supportTickets.dao;

import com.example.demo.supportTickets.model.SupportTicketDocuments;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class SupportTicketDocumentsDAOImpl implements SupportTicketDocumentsDAO {

    @PersistenceContext
    private EntityManager em;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final String[] ALLOWED_TYPES = {
            "application/pdf",
            "image/jpeg",
            "image/png",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    };

    @Override
    public List<SupportTicketDocuments> getDocumentsByTicketId(String ticketId) {
        return em.createQuery(
                        "SELECT d FROM SupportTicketDocuments d WHERE d.supportTicket.supportTicketId = :ticketId ORDER BY d.uploadedAt DESC",
                        SupportTicketDocuments.class)
                .setParameter("ticketId", ticketId)
                .getResultList();
    }

    @Override
    public SupportTicketDocuments getDocumentById(Long id) {
        return em.find(SupportTicketDocuments.class, id);
    }

    @Override
    public SupportTicketDocuments saveDocument(SupportTicketDocuments doc) {
        Map<String, String> errors = validateDocument(doc);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + errors);
        }

        if (doc.getDocumentId() == null) {
            em.persist(doc);
        } else {
            em.merge(doc);
        }
        return doc;
    }

    @Override
    public void deleteDocument(Long id) {
        SupportTicketDocuments doc = getDocumentById(id);
        if (doc != null) {
            em.remove(doc);
        }
    }

    @Override
    public long countDocumentsByTicketId(String ticketId) {
        return em.createQuery(
                        "SELECT COUNT(d) FROM SupportTicketDocuments d WHERE d.supportTicket.supportTicketId = :ticketId",
                        Long.class)
                .setParameter("ticketId", ticketId)
                .getSingleResult();
    }

    @Override
    public Map<String, String> validateDocument(SupportTicketDocuments doc) {
        Map<String, String> errors = new HashMap<>();

        if (doc.getFileName() == null || doc.getFileName().trim().isEmpty()) errors.put("fileName", "File name is required.");
        else if (doc.getFileName().length() > 255) errors.put("fileName", "File name too long.");

        if (doc.getFileType() == null || !isValidFileType(doc.getFileType())) errors.put("fileType", "Invalid file type.");

        if (doc.getFileSize() <= 0) errors.put("fileSize", "File size must be positive.");
        else if (doc.getFileSize() > 10 * 1024 * 1024) errors.put("fileSize", "File exceeds 10MB.");

        if (doc.getFileData() == null) errors.put("fileData", "File content is missing.");
        else if (doc.getFileData().length == 0) errors.put("fileData", "File is empty.");
        else if (doc.getFileData().length > 10 * 1024 * 1024) errors.put("fileData", "File content exceeds 10MB.");
        else if (doc.getFileData().length != doc.getFileSize()) errors.put("fileData", "File size mismatch.");

        if (doc.getSupportTicket() == null) errors.put("supportTicket", "Ticket reference required.");

        return errors;
    }

    private boolean isValidFileType(String type) {
        if (type == null) return false;
        for (String allowed : ALLOWED_TYPES) {
            if (allowed.equals(type)) return true;
        }
        return false;
    }
}