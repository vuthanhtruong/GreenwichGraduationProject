package com.example.demo.comment.dao;

import com.example.demo.comment.model.Comments;
import com.example.demo.comment.model.MajorComments;
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
public class MajorCommentsDAOImpl implements MajorCommentsDAO {

    @Override
    public List<String> getCommentNotificationsForLecturer(String lecturerId) {
        String jpql = """
        SELECT CONCAT(c.commenter.id, 
                      ' commented on your post in ', 
                      p.majorClass.nameClass, 
                      ': "', 
                      SUBSTRING(c.content, 1, 50), 
                      CASE WHEN LENGTH(c.content) > 50 THEN '...' ELSE '' END, 
                      '" on ', c.createdAt)
        FROM MajorComments c
        JOIN c.post p
        WHERE p.creator.id = :lecturerId
          AND c.notificationType = 'COMMENT_MADE_ON_MAJOR_POST'
        ORDER BY c.createdAt DESC
        """;

        return entityManager.createQuery(jpql, String.class)
                .setParameter("lecturerId", lecturerId)
                .getResultList();
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void saveComment(MajorComments comment) {
        if (comment == null || comment.getCommentId() == null) {
            throw new IllegalArgumentException("Comment or Comment ID cannot be null");
        }
        try {
            if (entityManager.find(MajorComments.class, comment.getCommentId()) == null) {
                entityManager.persist(comment);
            } else {
                entityManager.merge(comment);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error saving comment: " + e.getMessage(), e);
        }
    }

    @Override
    public MajorComments findCommentById(String commentId) {
        if (commentId == null) {
            return null;
        }
        try {
            return entityManager.find(MajorComments.class, commentId);
        } catch (Exception e) {
            throw new RuntimeException("Error finding comment by ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<MajorComments> findCommentsByPostId(String postId) {
        if (postId == null) {
            return List.of();
        }
        try {
            return entityManager.createQuery(
                            "SELECT c FROM MajorComments c WHERE c.post.postId = :postId ORDER BY c.createdAt DESC",
                            MajorComments.class)
                    .setParameter("postId", postId)
                    .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error finding comments by post ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<MajorComments> findPaginatedCommentsByPostId(String postId, int firstResult, int pageSize) {
        if (postId == null || pageSize <= 0) {
            return List.of();
        }
        try {
            return entityManager.createQuery(
                            "SELECT c FROM MajorComments c WHERE c.post.postId = :postId ORDER BY c.createdAt DESC",
                            MajorComments.class)
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
                            "SELECT COUNT(c) FROM MajorComments c WHERE c.post.postId = :postId",
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
            MajorComments comment = findCommentById(commentId);
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
            return entityManager.find(MajorComments.class, commentId) != null;
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

    @Override
    public Map<String, String> validateComment(MajorComments comment) {
        Map<String, String> errors = new HashMap<>();
        if (comment.getContent() == null || comment.getContent().trim().isEmpty()) {
            errors.put("content", "Comment content cannot be empty");
        } else if (comment.getContent().length() > 1000) {
            errors.put("content", "Comment cannot exceed 1000 characters");
        }
        return errors;
    }
}