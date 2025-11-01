package com.example.demo.document.dao;

import com.example.demo.document.model.SubmissionDocuments;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class SubmissionDocumentsDAOImpl implements SubmissionDocumentsDAO {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void saveDocument(SubmissionDocuments document) {
        if (document.getSubmissionDocumentId() == null) {
            document.setSubmissionDocumentId(generateUniqueDocumentId(
                    document.getSubmission().getSubmittedBy().getId(),
                    document.getSubmission().getAssignmentSubmitSlot().getPostId()
            ));
        }
        em.persist(document);
    }

    @Override
    public List<SubmissionDocuments> getDocumentsBySubmission(String submittedBy, String assignmentSlotId) {
        return em.createQuery(
                        "SELECT d FROM SubmissionDocuments d " +
                                "WHERE d.submission.id.submittedBy = :submittedBy " +
                                "AND d.submission.id.assignmentSubmitSlotId = :assignmentSlotId",
                        SubmissionDocuments.class)
                .setParameter("submittedBy", submittedBy)
                .setParameter("assignmentSlotId", assignmentSlotId)
                .getResultList();
    }

    @Override
    public SubmissionDocuments getDocumentById(String submissionDocumentId) {
        return em.find(SubmissionDocuments.class, submissionDocumentId);
    }

    @Override
    public Map<String, String> validateDocument(SubmissionDocuments document) {
        Map<String, String> errors = new HashMap<>();

        if (document.getSubmission() == null) {
            errors.put("submission", "Document must be associated with a submission");
        }
        if (document.getCreator() == null) {
            errors.put("creator", "Document must have a creator (student)");
        }
        if (document.getFileData() == null || document.getFileData().length == 0) {
            errors.put("fileData", "File data cannot be empty");
        }
        if (document.getFilePath() == null || document.getFilePath().trim().isEmpty()) {
            errors.put("filePath", "File name/path cannot be empty");
        }

        return errors;
    }

    @Override
    public String generateUniqueDocumentId(String submittedBy, String assignmentSlotId) {
        String prefix = (submittedBy != null ? submittedBy : "STU") +
                "-" +
                (assignmentSlotId != null ? assignmentSlotId : "SLOT");
        String documentId;
        SecureRandom random = new SecureRandom();

        do {
            String randomDigit = String.format("%03d", random.nextInt(1000)); // 3 chữ số
            documentId = prefix + "-SUBDOC" + randomDigit;
        } while (em.find(SubmissionDocuments.class, documentId) != null);

        return documentId;
    }
}