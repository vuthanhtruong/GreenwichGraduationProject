package com.example.demo.comment.service;

import com.example.demo.comment.dao.SpecializedCommentsDAO;
import com.example.demo.comment.model.SpecializedComments;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class SpecializedCommentsServiceImpl implements SpecializedCommentsService {
    @Override
    public List<String> getCommentNotificationsForLecturer(String lecturerId) {
        return specializedCommentsDAO.getCommentNotificationsForLecturer(lecturerId);
    }

    @Override
    public Map<String, String> validateComment(SpecializedComments comment) {
        return specializedCommentsDAO.validateComment(comment);
    }

    @Override
    public String generateUniqueCommentId(String postId, LocalDate createdDate) {
        return specializedCommentsDAO.generateUniqueCommentId(postId, createdDate);
    }

    private final SpecializedCommentsDAO specializedCommentsDAO;

    public SpecializedCommentsServiceImpl(SpecializedCommentsDAO specializedCommentsDAO) {
        this.specializedCommentsDAO = specializedCommentsDAO;
    }

    @Override
    public void saveComment(SpecializedComments comment) {
        specializedCommentsDAO.saveComment(comment);
    }

    @Override
    public SpecializedComments findCommentById(String commentId) {
        return specializedCommentsDAO.findCommentById(commentId);
    }

    @Override
    public List<SpecializedComments> findCommentsByPostId(String postId) {
        return specializedCommentsDAO.findCommentsByPostId(postId);
    }

    @Override
    public List<SpecializedComments> findPaginatedCommentsByPostId(String postId, int firstResult, int pageSize) {
        return specializedCommentsDAO.findPaginatedCommentsByPostId(postId, firstResult, pageSize);
    }

    @Override
    public long countCommentsByPostId(String postId) {
        return specializedCommentsDAO.countCommentsByPostId(postId);
    }

    @Override
    public void deleteComment(String commentId) {
        specializedCommentsDAO.deleteComment(commentId);
    }

    @Override
    public boolean existsCommentById(String commentId) {
        return specializedCommentsDAO.existsCommentById(commentId);
    }
}
