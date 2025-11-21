package com.example.demo.comment.dao;

import com.example.demo.comment.model.Comments;
import com.example.demo.comment.model.SpecializedAssignmentComments;
import com.example.demo.entity.Enums.OtherNotification;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.*;

@Repository
@Transactional
public class SpecializedAssignmentCommentsDAOImpl implements SpecializedAssignmentCommentsDAO {

    @Override
    public List<String> getCommentNotificationsForLecturer(String lecturerId) {

        String jpql = """
        SELECT CONCAT(
            c.commenter.id,
            ' commented on specialized assignment submission of ',
            p.creator.firstName,
            ': "',
            SUBSTRING(c.content, 1, 50),
            CASE WHEN LENGTH(c.content) > 50 THEN '...' ELSE '' END,
            '" on ',
            c.createdAt
        )
        FROM SpecializedAssignmentComments c
        JOIN c.post p
        WHERE p.creator.id = :lecturerId
          AND c.notificationType = :nt
        ORDER BY c.createdAt DESC
        """;

        return entityManager.createQuery(jpql, String.class)
                .setParameter("lecturerId", lecturerId)
                .setParameter("nt", OtherNotification.COMMENT_MADE_ON_SPECIALIZED_ASSIGNMENT)
                .getResultList();
    }


    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void saveComment(SpecializedAssignmentComments comment) {
        if (entityManager.find(SpecializedAssignmentComments.class, comment.getCommentId()) == null)
            entityManager.persist(comment);
        else
            entityManager.merge(comment);
    }

    @Override
    public SpecializedAssignmentComments findCommentById(String commentId) {
        return entityManager.find(SpecializedAssignmentComments.class, commentId);
    }

    @Override
    public List<SpecializedAssignmentComments> findCommentsByAssignmentId(String assignmentId) {
        return entityManager.createQuery(
                        "SELECT c FROM SpecializedAssignmentComments c WHERE c.post.postId = :id ORDER BY c.createdAt ASC",
                        SpecializedAssignmentComments.class
                ).setParameter("id", assignmentId)
                .getResultList();
    }

    @Override
    public long countCommentsByAssignmentId(String assignmentId) {
        return entityManager.createQuery(
                        "SELECT COUNT(c) FROM SpecializedAssignmentComments c WHERE c.post.postId = :id",
                        Long.class
                ).setParameter("id", assignmentId)
                .getSingleResult();
    }

    @Override
    public void deleteComment(String commentId) {
        SpecializedAssignmentComments c = findCommentById(commentId);
        if (c != null) entityManager.remove(c);
    }

    @Override
    public boolean existsCommentById(String commentId) {
        return entityManager.find(SpecializedAssignmentComments.class, commentId) != null;
    }

    @Override
    public String generateUniqueCommentId(String assignmentId, LocalDate date) {
        if (assignmentId == null) assignmentId = "ASG";
        if (date == null) date = LocalDate.now();

        String prefix = assignmentId.substring(0, Math.min(6, assignmentId.length()));
        String datePart = String.format("%02d%02d", date.getMonthValue(), date.getDayOfMonth());

        SecureRandom rnd = new SecureRandom();
        String id;

        do {
            id = prefix + datePart + String.format("%04d", rnd.nextInt(10000));
        } while (entityManager.find(Comments.class, id) != null);

        return id;
    }

    @Override
    public Map<String, String> validateComment(SpecializedAssignmentComments c) {
        Map<String, String> errors = new HashMap<>();

        if (c.getContent() == null || c.getContent().isBlank())
            errors.put("content", "Comment cannot be empty");

        if (c.getContent() != null && c.getContent().length() > 1000)
            errors.put("content", "Comment cannot exceed 1000 characters");

        return errors;
    }
}
