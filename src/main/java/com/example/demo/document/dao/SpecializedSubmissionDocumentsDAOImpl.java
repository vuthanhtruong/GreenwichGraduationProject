// src/main/java/com/example/demo/document/dao/SpecializedSubmissionDocumentsDAOImpl.java

package com.example.demo.document.dao;

import com.example.demo.document.model.SpecializedSubmissionDocuments;
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
public class SpecializedSubmissionDocumentsDAOImpl implements SpecializedSubmissionDocumentsDAO {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void saveDocument(SpecializedSubmissionDocuments document) {
        if (document.getSubmissionDocumentId() == null) {
            document.setSubmissionDocumentId(generateUniqueDocumentId(
                    document.getSubmission().getSubmittedBy().getId(),
                    document.getSubmission().getAssignmentSubmitSlot().getPostId()
            ));
        }
        em.persist(document);
    }

    @Override
    public List<SpecializedSubmissionDocuments> getDocumentsBySubmission(String submittedBy, String assignmentSlotId) {
        return em.createQuery(
                        "SELECT d FROM SpecializedSubmissionDocuments d " +
                                "WHERE d.submission.submittedBy = :submittedBy " +
                                "AND d.submission.assignmentSubmitSlot = :assignmentSlotId",
                        SpecializedSubmissionDocuments.class)
                .setParameter("submittedBy", submittedBy)
                .setParameter("assignmentSlotId", assignmentSlotId)
                .getResultList();
    }

    @Override
    public SpecializedSubmissionDocuments getDocumentById(String submissionDocumentId) {
        return em.find(SpecializedSubmissionDocuments.class, submissionDocumentId);
    }

    @Override
    public Map<String, String> validateDocument(SpecializedSubmissionDocuments document) {
        Map<String, String> errors = new HashMap<>();

        if (document.getSubmission() == null) {
            errors.put("submission", "Document must be associated with a submission");
        }
        if (document.getCreator() == null) {
            errors.put("creator", "Document must have a creator (student)");
        }
        if ((document.getFileData() == null || document.getFileData().length == 0)
                && (document.getFilePath() == null || document.getFilePath().trim().isEmpty())) {
            errors.put("file", "Either file data or file path must be provided");
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
            String randomDigit = String.format("%03d", random.nextInt(1000));
            documentId = prefix + "-SPDOC" + randomDigit;  // SPDOC = Specialized DOC
        } while (em.find(SpecializedSubmissionDocuments.class, documentId) != null);

        return documentId;
    }
}