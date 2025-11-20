package com.example.demo.comment.dao;

import com.example.demo.comment.model.Comments;
import com.example.demo.comment.model.MajorAssignmentComments;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.*;

@Repository
@Transactional
public class MajorAssignmentCommentsDAOImpl implements MajorAssignmentCommentsDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void saveComment(MajorAssignmentComments comment) {
        if (comment == null || comment.getCommentId() == null)
            throw new IllegalArgumentException("Comment or ID cannot be null");

        if (entityManager.find(MajorAssignmentComments.class, comment.getCommentId()) == null)
            entityManager.persist(comment);
        else
            entityManager.merge(comment);
    }

    @Override
    public MajorAssignmentComments findCommentById(String commentId) {
        return entityManager.find(MajorAssignmentComments.class, commentId);
    }

    @Override
    public List<MajorAssignmentComments> findCommentsByAssignmentId(String assignmentId) {
        return entityManager.createQuery(
                        "SELECT c FROM MajorAssignmentComments c WHERE c.post.postId = :id ORDER BY c.createdAt ASC",
                        MajorAssignmentComments.class
                ).setParameter("id", assignmentId)
                .getResultList();
    }

    @Override
    public long countCommentsByAssignmentId(String assignmentId) {
        return entityManager.createQuery(
                        "SELECT COUNT(c) FROM MajorAssignmentComments c WHERE c.post.postId = :id",
                        Long.class
                ).setParameter("id", assignmentId)
                .getSingleResult();
    }

    @Override
    public void deleteComment(String commentId) {
        MajorAssignmentComments c = findCommentById(commentId);
        if (c != null) entityManager.remove(c);
    }

    @Override
    public boolean existsCommentById(String commentId) {
        return entityManager.find(MajorAssignmentComments.class, commentId) != null;
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
    public Map<String, String> validateComment(MajorAssignmentComments c) {
        Map<String, String> e = new HashMap<>();

        if (c.getContent() == null || c.getContent().trim().isEmpty())
            e.put("content", "Comment cannot be empty");

        if (c.getContent() != null && c.getContent().length() > 1000)
            e.put("content", "Comment cannot exceed 1000 characters");

        return e;
    }
}
