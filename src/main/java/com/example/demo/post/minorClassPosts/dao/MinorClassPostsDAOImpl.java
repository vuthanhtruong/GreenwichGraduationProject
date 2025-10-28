package com.example.demo.post.minorClassPosts.dao;

import com.example.demo.post.classPost.model.ClassPosts;
import com.example.demo.post.minorClassPosts.model.MinorClassPosts;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class MinorClassPostsDAOImpl implements MinorClassPostsDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public MinorClassPosts getMinorClassPost(String minorClassPostsId) {
        return entityManager.find(MinorClassPosts.class, minorClassPostsId);
    }

    @Override
    public void saveMinorClassPosts(MinorClassPosts minorClassPosts) {
        entityManager.persist(minorClassPosts);
    }

    @Override
    public List<MinorClassPosts> getClassPostByClass(String classId) {
        return entityManager.createQuery(
                        "FROM MinorClassPosts m WHERE m.minorClass.classId = :classId", MinorClassPosts.class)
                .setParameter("classId", classId)
                .getResultList();
    }

    @Override
    public Map<String, String> validatePost(MinorClassPosts post) {
        Map<String, String> errors = new HashMap<>();

        if (post.getContent() == null || post.getContent().trim().isEmpty()) {
            errors.put("content", "Post content cannot be empty");
        }

        return errors;
    }

    @Override
    public String generateUniquePostId(String classId, LocalDate createdDate) {
        String prefix = classId != null ? classId : "CLS";
        String year = String.format("%02d", createdDate.getYear() % 100);
        String date = String.format("%02d%02d", createdDate.getMonthValue(), createdDate.getDayOfMonth());
        String postId;
        SecureRandom random = new SecureRandom();

        do {
            String randomDigit = String.format("%03d", random.nextInt(1000));
            postId = prefix + year + date + randomDigit;
        } while (entityManager.find(ClassPosts.class, postId) != null);

        return postId;
    }
}