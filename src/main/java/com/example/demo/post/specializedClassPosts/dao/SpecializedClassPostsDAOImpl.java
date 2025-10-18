package com.example.demo.post.specializedClassPosts.dao;

import com.example.demo.post.classPost.model.ClassPosts;
import com.example.demo.post.specializedClassPosts.model.SpecializedClassPosts;
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
public class SpecializedClassPostsDAOImpl implements SpecializedClassPostsDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<SpecializedClassPosts> getClassPostsByClass(String classId) {
        return entityManager.createQuery("FROM SpecializedClassPosts s WHERE s.specializedClass.classId = :classId", SpecializedClassPosts.class)
                .setParameter("classId", classId)
                .getResultList();
    }

    @Override
    public void saveSpecializedClassPosts(SpecializedClassPosts specializedClassPosts) {
        if (specializedClassPosts.getPostId() == null) {
            specializedClassPosts.setPostId(generateUniquePostId(
                    specializedClassPosts.getSpecializedClass() != null ? specializedClassPosts.getSpecializedClass().getClassId() : "SPC",
                    specializedClassPosts.getCreatedAt() != null ? specializedClassPosts.getCreatedAt().toLocalDate() : LocalDate.now()
            ));
        }
        entityManager.persist(specializedClassPosts);
    }

    @Override
    public SpecializedClassPosts getSpecializedClassPost(String postId) {
        return entityManager.find(SpecializedClassPosts.class, postId);
    }

    @Override
    public Map<String, String> validatePost(SpecializedClassPosts post) {
        Map<String, String> errors = new HashMap<>();
        if (post.getContent() == null || post.getContent().trim().isEmpty()) {
            errors.put("content", "Post content cannot be empty");
        }
        if (post.getSpecializedClass() == null) {
            errors.put("specializedClass", "Specialized class must be specified");
        }
        return errors;
    }

    @Override
    public String generateUniquePostId(String classId, LocalDate createdDate) {
        String prefix = classId != null ? classId : "SPC";
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