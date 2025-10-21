package com.example.demo.comment.dao;

import com.example.demo.comment.model.SpecializedComments;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class SpecializedCommentsDAOImpl implements SpecializedCommentsDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void saveComment(SpecializedComments comment) {
        if (comment == null || comment.getCommentId() == null) {
            throw new IllegalArgumentException("Comment or Comment ID cannot be null");
        }
        try {
            if (entityManager.find(SpecializedComments.class, comment.getCommentId()) == null) {
                entityManager.persist(comment);
            } else {
                entityManager.merge(comment);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error saving comment: " + e.getMessage(), e);
        }
    }

    @Override
    public SpecializedComments findCommentById(String commentId) {
        if (commentId == null) {
            return null;
        }
        try {
            return entityManager.find(SpecializedComments.class, commentId);
        } catch (Exception e) {
            throw new RuntimeException("Error finding comment by ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<SpecializedComments> findCommentsByPostId(String postId) {
        if (postId == null) {
            return List.of();
        }
        try {
            return entityManager.createQuery(
                            "SELECT c FROM SpecializedComments c WHERE c.post.postId = :postId ORDER BY c.createdAt DESC",
                            SpecializedComments.class)
                    .setParameter("postId", postId)
                    .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error finding comments by post ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<SpecializedComments> findPaginatedCommentsByPostId(String postId, int firstResult, int pageSize) {
        if (postId == null || pageSize <= 0) {
            return List.of();
        }
        try {
            return entityManager.createQuery(
                            "SELECT c FROM SpecializedComments c WHERE c.post.postId = :postId ORDER BY c.createdAt DESC",
                            SpecializedComments.class)
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
                            "SELECT COUNT(c) FROM SpecializedComments c WHERE c.post.postId = :postId",
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
            SpecializedComments comment = findCommentById(commentId);
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
            return entityManager.find(SpecializedComments.class, commentId) != null;
        } catch (Exception e) {
            throw new RuntimeException("Error checking comment existence: " + e.getMessage(), e);
        }
    }
}