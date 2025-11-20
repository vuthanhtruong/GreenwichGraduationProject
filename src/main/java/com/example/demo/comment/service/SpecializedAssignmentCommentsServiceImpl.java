package com.example.demo.comment.service;

import com.example.demo.comment.dao.SpecializedAssignmentCommentsDAO;
import com.example.demo.comment.model.SpecializedAssignmentComments;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class SpecializedAssignmentCommentsServiceImpl implements SpecializedAssignmentCommentsService {

    private final SpecializedAssignmentCommentsDAO dao;

    public SpecializedAssignmentCommentsServiceImpl(SpecializedAssignmentCommentsDAO dao) {
        this.dao = dao;
    }

    @Override
    public void saveComment(SpecializedAssignmentComments comment) {
        dao.saveComment(comment);
    }

    @Override
    public SpecializedAssignmentComments findCommentById(String commentId) {
        return dao.findCommentById(commentId);
    }

    @Override
    public List<SpecializedAssignmentComments> findCommentsByAssignmentId(String assignmentId) {
        return dao.findCommentsByAssignmentId(assignmentId);
    }

    @Override
    public long countCommentsByAssignmentId(String assignmentId) {
        return dao.countCommentsByAssignmentId(assignmentId);
    }

    @Override
    public void deleteComment(String commentId) {
        dao.deleteComment(commentId);
    }

    @Override
    public boolean existsCommentById(String commentId) {
        return dao.existsCommentById(commentId);
    }

    @Override
    public String generateUniqueCommentId(String assignmentId, LocalDate createdDate) {
        return dao.generateUniqueCommentId(assignmentId, createdDate);
    }

    @Override
    public Map<String, String> validateComment(SpecializedAssignmentComments comment) {
        return dao.validateComment(comment);
    }
}
