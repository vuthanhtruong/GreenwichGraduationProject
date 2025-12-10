package com.example.demo.document.dao;

import com.example.demo.document.model.Documents;
import com.example.demo.post.Blog.model.PublicPosts;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Repository
@Transactional
public class DocumentsDAOImpl implements DocumentsDAO {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Documents> getDocumentsByPost(PublicPosts post) {
        if (post == null || post.getPostId() == null) return List.of();
        return getDocumentsByPostId(post.getPostId());
    }

    @Override
    public List<Documents> getDocumentsByPostId(String postId) {
        return em.createQuery(
                        "SELECT d FROM Documents d WHERE d.post.postId = :postId", Documents.class)
                .setParameter("postId", postId)
                .getResultList();
    }

    @Override
    public Documents getDocumentById(String docId) {
        return em.find(Documents.class, docId);
    }

    @Override
    public void saveDocument(Documents doc) {
        if (doc.getDocumentId() == null) {
            em.persist(doc);
        } else {
            em.merge(doc);
        }
    }

    @Override
    public void deleteDocuments(List<String> documentIds) {
        if (documentIds == null || documentIds.isEmpty()) return;
        for (String id : documentIds) {
            Documents doc = getDocumentById(id);
            if (doc != null) {
                em.remove(doc);
            }
        }
    }

    @Override
    public void addDocuments(PublicPosts post, MultipartFile[] files) {
        if (files == null || files.length == 0) return;

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;
            if (file.getSize() > 5 * 1024 * 1024) {
                throw new IllegalArgumentException(
                        "File '" + file.getOriginalFilename() + "' exceeds 5 MB limit."
                );
            }

            Documents doc = new Documents();
            doc.setDocumentId(generateDocId());
            doc.setDocumentTitle(file.getOriginalFilename());
            try {
                doc.setFileData(file.getBytes());
            } catch (Exception e) {
                throw new RuntimeException("Failed to read file: " + file.getOriginalFilename(), e);
            }
            doc.setPost(post);

            saveDocument(doc);
        }
    }
    private String generateDocId() {
        return "DOC" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }
}