package com.example.demo.post.news.dao;

import com.example.demo.document.model.Documents;
import com.example.demo.post.news.model.News;
import com.example.demo.user.staff.model.Staffs;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class NewsDAOImpl implements NewsDAO {

    private static final Logger logger = LoggerFactory.getLogger(NewsDAOImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<News> getPaginatedNews(int firstResult, int pageSize, Staffs creator) {
        return entityManager.createQuery(
                        "SELECT n FROM News n JOIN FETCH n.creator WHERE n.creator = :creator ORDER BY n.createdAt DESC",
                        News.class)
                .setParameter("creator", creator)
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public long countNewsByCreator(Staffs creator) {
        return entityManager.createQuery(
                        "SELECT COUNT(n) FROM News n WHERE n.creator = :creator", Long.class)
                .setParameter("creator", creator)
                .getSingleResult();
    }

    @Override
    public News getNewsById(String postId) {
        return entityManager.find(News.class, postId);
    }

    @Override
    public void addNews(News news) {
        entityManager.persist(news);
    }

    @Override
    public void updateNews(News news) {
        entityManager.merge(news);
    }

    @Override
    public void deleteNews(String postId) {
        News news = getNewsById(postId);
        if (news != null) {
            entityManager.remove(news);
        }
    }

    @Override
    public List<News> searchNews(String searchType, String keyword, int firstResult, int pageSize, Staffs creator) {
        String jpql = "SELECT n FROM News n JOIN FETCH n.creator WHERE n.creator = :creator";
        if ("title".equalsIgnoreCase(searchType)) {
            jpql += " AND LOWER(n.title) LIKE LOWER(:keyword)";
        } else if ("id".equalsIgnoreCase(searchType)) {
            jpql += " AND n.postId = :keyword";
        } else {
            return List.of();
        }
        jpql += " ORDER BY n.createdAt DESC";

        TypedQuery<News> query = entityManager.createQuery(jpql, News.class)
                .setParameter("creator", creator)
                .setFirstResult(firstResult)
                .setMaxResults(pageSize);

        if ("title".equalsIgnoreCase(searchType)) {
            query.setParameter("keyword", "%" + keyword.toLowerCase() + "%");
        } else {
            query.setParameter("keyword", keyword);
        }
        return query.getResultList();
    }

    @Override
    public long countSearchResults(String searchType, String keyword, Staffs creator) {
        String jpql = "SELECT COUNT(n) FROM News n WHERE n.creator = :creator";
        if ("title".equalsIgnoreCase(searchType)) {
            jpql += " AND LOWER(n.title) LIKE LOWER(:keyword)";
        } else if ("id".equalsIgnoreCase(searchType)) {
            jpql += " AND n.postId = :keyword";
        } else {
            return 0L;
        }

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
                .setParameter("creator", creator);

        if ("title".equalsIgnoreCase(searchType)) {
            query.setParameter("keyword", "%" + keyword.toLowerCase() + "%");
        } else {
            query.setParameter("keyword", keyword);
        }
        return query.getSingleResult();
    }

    @Override
    public String generateUniqueNewsId(Staffs creator) {
        String prefix = creator.getMajorManagement() != null ? creator.getMajorManagement().getMajorId() : "NEWS";
        String year = String.format("%02d", LocalDate.now().getYear() % 100);
        String date = String.format("%02d%02d", LocalDate.now().getMonthValue(), LocalDate.now().getDayOfMonth());
        String newsId;
        SecureRandom random = new SecureRandom();
        do {
            newsId = prefix + year + date + random.nextInt(10);
        } while (getNewsById(newsId) != null);
        return newsId;
    }

    @Override
    public Map<String, String> validateNews(News news) {
        Map<String, String> errors = new HashMap<>();
        if (news.getTitle() == null || news.getTitle().trim().isEmpty()) {
            errors.put("title", "Title cannot be blank");
        } else if (news.getTitle().length() > 255) {
            errors.put("title", "Title no more than 255 characters");
        }
        if (news.getContent() != null && news.getContent().length() > 1000) {
            errors.put("content", "Content no more than 1000 characters");
        }
        return errors;
    }
}