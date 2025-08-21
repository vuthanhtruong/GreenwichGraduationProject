package com.example.demo.news.dao;

import com.example.demo.document.model.Documents;
import com.example.demo.major.model.Majors;
import com.example.demo.news.model.News;
import com.example.demo.document.service.DocumentsService;
import com.example.demo.majorStaff.service.StaffsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
@Transactional
public class NewsDAOImpl implements NewsDAO {

    @Override
    public void deleteDocuments(String id, List<String> documentIds) {
        List<String> newDocuments = entityManager.createQuery("select d.id from Documents d where d.post.id=:id").
                setParameter("id", id).getResultList();
        documentsService.deleteDocuments(documentIds);
    }

    @PersistenceContext
    private EntityManager entityManager;
    private final StaffsService staffsService;
    private final DocumentsService documentsService;

    @Autowired
    public NewsDAOImpl(StaffsService staffsService, DocumentsService documentsService) {
        this.documentsService = documentsService;
        if (staffsService == null) {
            throw new IllegalArgumentException("StaffsService cannot be null");
        }
        this.staffsService = staffsService;
    }

    @Override
    public void addNews(News news, List<Documents> documents) {
        if (news == null) {
            throw new IllegalArgumentException("News object cannot be null");
        }
        // Gán creator từ StaffsService
        news.setCreator(staffsService.getStaff());
        // Gán thời gian tạo nếu chưa có
        if (news.getCreatedAt() == null) {
            news.setCreatedAt(LocalDateTime.now());
        }
        // Gán postId nếu chưa có
        if (news.getPostId() == null) {
            news.setPostId(UUID.randomUUID().toString());
        }

        // Xử lý và lưu Documents trước
        if (documents != null && !documents.isEmpty()) {
            for (Documents doc : documents) {
                if (doc != null) {
                    // Gán documentId nếu chưa có
                    if (doc.getDocumentId() == null) {
                        doc.setDocumentId(UUID.randomUUID().toString());
                    }
                    // Liên kết Document với News
                    doc.setPost(news);
                    // Lưu Document vào database trước
                    entityManager.persist(doc);
                    // Thêm vào danh sách documents của News
                    news.getDocuments().add(doc);
                }
            }
        }

        // Lưu News vào database sau khi đã lưu Documents
        entityManager.persist(news);
    }

    @Override
    public List<News> getNewsByMajor(Majors major) {
        if (major == null) {
            return List.of();
        }
        return entityManager.createQuery(
                        "select n from News n left join fetch n.documents where n.creator.majorManagement=:major", News.class)
                .setParameter("major", major)
                .getResultList();
    }

    @Override
    public News getNewsById(String id) {
        return entityManager.find(News.class, id);
    }

    @Override
    public void updateNews(News news, List<Documents> newDocuments) {
        if (news != null) {
            News existingNews = entityManager.find(News.class, news.getPostId());
            if (existingNews != null) {
                existingNews.setTitle(news.getTitle());
                existingNews.setNotification(news.getNotification());
                existingNews.setContent(news.getContent());

                // Thêm tài liệu mới
                if (newDocuments != null && !newDocuments.isEmpty()) {
                    for (Documents doc : newDocuments) {
                        if (doc != null && doc.getDocumentId() == null) {
                            doc.setDocumentId(UUID.randomUUID().toString());
                            doc.setPost(existingNews);
                            entityManager.persist(doc);
                            existingNews.getDocuments().add(doc);
                        }
                    }
                }
                entityManager.merge(existingNews);
            }
        }
    }
}