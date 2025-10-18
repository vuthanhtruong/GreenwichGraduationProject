package com.example.demo.post.majorClassPosts.dao;

import com.example.demo.post.classPost.model.ClassPosts;
import com.example.demo.post.majorClassPosts.model.MajorClassPosts;
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
public class MajorClassPostsDAOImpl implements MajorClassPostsDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public MajorClassPosts getMajorClassPost(String majorClassPostsId) {
        return entityManager.find(MajorClassPosts.class, majorClassPostsId);
    }

    @Override
    public void saveMajorClassPosts(MajorClassPosts majorClassPosts) {
        entityManager.persist(majorClassPosts);
    }

    @Override
    public List<MajorClassPosts> getClassPostByClass(String classId) {
        return entityManager.createQuery("FROM MajorClassPosts m WHERE m.majorClass.classId = :classId", MajorClassPosts.class)
                .setParameter("classId", classId)
                .getResultList();
    }

    @Override
    public Map<String, String> validatePost(MajorClassPosts post) {
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
            String randomDigit = String.format("%03d", random.nextInt(1000)); // 3 chữ số ngẫu nhiên
            postId = prefix + year + date + randomDigit;
        } while (entityManager.find(ClassPosts.class, postId) != null);
        return postId;
    }
}