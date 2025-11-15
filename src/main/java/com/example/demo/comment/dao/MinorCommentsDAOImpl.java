package com.example.demo.comment.dao;

import com.example.demo.comment.model.Comments;
import com.example.demo.comment.model.MinorComments;
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
public class MinorCommentsDAOImpl implements MinorCommentsDAO {

    @Override
    public List<String> getCommentNotificationsForLecturer(String lecturerId) {
        String jpql = """
        SELECT CONCAT(c.commenter.id, 
                      ' commented on your post in ', 
                      p.minorClass.nameClass, 
                      ': "', 
                      SUBSTRING(c.content, 1, 50), 
                      CASE WHEN LENGTH(c.content) > 50 THEN '...' ELSE '' END, 
                      '" on ', c.createdAt)
        FROM MinorComments c
        JOIN c.post p
        WHERE p.creator.id = :lecturerId
          AND c.notificationType = 'COMMENT_MADE_ON_MINOR_POST'
        ORDER BY c.createdAt DESC
        """;

        return entityManager.createQuery(jpql, String.class)
                .setParameter("lecturerId", lecturerId)
                .getResultList();
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public MinorComments getCommentById(String commentId) {
        return entityManager.find(MinorComments.class, commentId);
    }

    @Override
    public void saveComment(MinorComments comment) {
        entityManager.persist(comment);
    }

    @Override
    public List<MinorComments> getCommentsByPostId(String postId) {
        return entityManager.createQuery(
                        "FROM MinorComments c WHERE c.post.postId = :postId ORDER BY c.createdAt ASC",
                        MinorComments.class)
                .setParameter("postId", postId)
                .getResultList();
    }

    @Override
    public Map<String, String> validateComment(MinorComments comment) {
        Map<String, String> errors = new HashMap<>();

        if (comment.getContent() == null || comment.getContent().trim().isEmpty()) {
            errors.put("content", "Comment content cannot be empty");
        } else if (comment.getContent().length() > 1000) {
            errors.put("content", "Comment cannot exceed 1000 characters");
        }

        return errors;
    }

    @Override
    public String generateUniqueCommentId(String postId, LocalDate createdDate) {
        String prefix = postId != null ? postId.substring(0, Math.min(postId.length(), 6)) : "POST";
        String date = String.format("%02d%02d", createdDate.getMonthValue(), createdDate.getDayOfMonth());
        String commentId;
        SecureRandom random = new SecureRandom();

        do {
            String randomDigit = String.format("%04d", random.nextInt(10000)); // 4 chữ số
            commentId = prefix + date + randomDigit;
        } while (entityManager.find(Comments.class, commentId) != null);

        return commentId;
    }
}