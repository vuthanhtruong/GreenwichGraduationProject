package com.example.demo.document.dao;

import com.example.demo.document.model.ClassDocuments;
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
public class ClassDocumentsDAOImpl implements ClassDocumentsDAO {
    @Override
    public ClassDocuments getDocumentById(String id) {
        return entityManager.find(ClassDocuments.class, id);
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void saveDocument(ClassDocuments document) {
        if (document.getDocumentId() == null) {
            document.setDocumentId(generateUniqueDocumentId(
                    document.getPost() != null ? document.getPost().getPostId() : "UNKNOWN"
            ));
        }
        entityManager.persist(document);
    }

    @Override
    public List<ClassDocuments> getDocumentsByPostId(String postId) {
        return entityManager.createQuery("FROM ClassDocuments d WHERE d.post.postId = :postId", ClassDocuments.class)
                .setParameter("postId", postId)
                .getResultList();
    }

    @Override
    public Map<String, String> validateDocument(ClassDocuments document) {
        Map<String, String> errors = new HashMap<>();
        if (document.getPost() == null) {
            errors.put("post", "Document must be associated with a post");
        }
        if (document.getFileData() == null || document.getFileData().length == 0) {
            errors.put("fileData", "File data cannot be empty");
        }
        if (document.getDocumentTitle() == null || document.getDocumentTitle().trim().isEmpty()) {
            errors.put("documentTitle", "Document title cannot be empty");
        }
        return errors;
    }

    @Override
    public String generateUniqueDocumentId(String postId) {
        String prefix = postId != null ? postId : "UNKNOWN";
        String documentId;
        SecureRandom random = new SecureRandom();
        do {
            String randomDigit = String.format("%03d", random.nextInt(1000)); // 3 chữ số ngẫu nhiên
            documentId = prefix + "-DOC" + randomDigit;
        } while (entityManager.find(ClassDocuments.class, documentId) != null);
        return documentId;
    }
}