package com.example.demo.document.dao;

import com.example.demo.document.model.Documents;
import com.example.demo.news.model.News;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.List;

@Repository
@Transactional
public class DocumentsDAOImpl implements DocumentsDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Documents> getDocumentsByNews(News news) {
        if (news == null || news.getPostId() == null) {
            return List.of();
        }
        return entityManager.createQuery(
                        "select d from Documents d where d.post.postId = :postId", Documents.class)
                .setParameter("postId", news.getPostId())
                .getResultList();
    }

    @Override
    public void deleteDocuments(List<String> documentIds) {
        if (documentIds != null && !documentIds.isEmpty()) {
            for (String docId : documentIds) {
                Documents doc = entityManager.find(Documents.class, docId);
                if (doc != null) {
                    entityManager.remove(doc);
                    // Xóa file khỏi filesystem nếu tồn tại
                    File file = new File(doc.getFilePath());
                    if (file.exists()) {
                        file.delete();
                    }
                }
            }
        }
    }
}