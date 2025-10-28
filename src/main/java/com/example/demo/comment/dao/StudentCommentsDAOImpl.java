package com.example.demo.comment.dao;

import com.example.demo.comment.model.Comments;
import com.example.demo.comment.model.StudentComments;
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
public class StudentCommentsDAOImpl implements StudentCommentsDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void saveComment(StudentComments comment) {
        if (comment == null || comment.getCommentId() == null) {
            throw new IllegalArgumentException("Comment or Comment ID cannot be null");
        }
        try {
            if (entityManager.find(StudentComments.class, comment.getCommentId()) == null) {
                entityManager.persist(comment);
            } else {
                entityManager.merge(comment);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error saving comment: " + e.getMessage(), e);
        }
    }

    @Override
    public StudentComments findCommentById(String commentId) {
        if (commentId == null) {
            return null;
        }
        try {
            return entityManager.find(StudentComments.class, commentId);
        } catch (Exception e) {
            throw new RuntimeException("Error finding comment by ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<StudentComments> findCommentsByPostId(String postId) {
        if (postId == null) {
            return List.of();
        }
        try {
            return entityManager.createQuery(
                            "SELECT c FROM StudentComments c WHERE c.post.postId = :postId ORDER BY c.createdAt DESC",
                            StudentComments.class)
                    .setParameter("postId", postId)
                    .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error finding comments by post ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<StudentComments> findPaginatedCommentsByPostId(String postId, int firstResult, int pageSize) {
        if (postId == null || pageSize <= 0) {
            return List.of();
        }
        try {
            return entityManager.createQuery(
                            "SELECT c FROM StudentComments c WHERE c.post.postId = :postId ORDER BY c.createdAt DESC",
                            StudentComments.class)
                    .setParameter("postId", postId)
                    .setFirstResult(firstResult)
                    .setMaxResults(pageSize)
                    .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error finding paginated comments by post ID: " + e.getMessage(), e);
        }
    }

    @Override
    public long countCommentsByPostId(String postId) {
        if (postId == null) {
            return 0;
        }
        try {
            return entityManager.createQuery(
                            "SELECT COUNT(c) FROM StudentComments c WHERE c.post.postId = :postId",
                            Long.class)
                    .setParameter("postId", postId)
                    .getSingleResult();
        } catch (Exception e) {
            throw new RuntimeException("Error counting comments by post ID: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteComment(String commentId) {
        if (commentId == null) {
            throw new IllegalArgumentException("Comment ID cannot be null");
        }
        try {
            StudentComments comment = findCommentById(commentId);
            if (comment != null) {
                entityManager.remove(comment);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error deleting comment: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsCommentById(String commentId) {
        if (commentId == null) {
            return false;
        }
        try {
            return entityManager.find(StudentComments.class, commentId) != null;
        } catch (Exception e) {
            throw new RuntimeException("Error checking comment existence: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateUniqueCommentId(String postId, LocalDate createdDate) {
        if (postId == null || postId.trim().isEmpty()) {
            postId = "POST";
        }
        if (createdDate == null) {
            createdDate = LocalDate.now();
        }

        String prefix = postId.length() >= 6 ? postId.substring(0, 6) : postId; // Lấy 6 ký tự đầu
        String datePart = String.format("%02d%02d", createdDate.getMonthValue(), createdDate.getDayOfMonth());
        String commentId;
        SecureRandom random = new SecureRandom();

        do {
            int randomNum = random.nextInt(10000); // 0000 - 9999
            commentId = prefix + datePart + String.format("%04d", randomNum);
        } while (entityManager.find(Comments.class, commentId) != null); // Kiểm tra trùng

        return commentId;
    }
    // StudentCommentsDAOImpl.java
    @Override
    public Map<String, String> validateComment(StudentComments comment) {
        Map<String, String> errors = new HashMap<>();
        if (comment.getContent() == null || comment.getContent().trim().isEmpty()) {
            errors.put("content", "Comment content cannot be empty");
        } else if (comment.getContent().length() > 1000) {
            errors.put("content", "Comment cannot exceed 1000 characters");
        }
        return errors;
    }
}