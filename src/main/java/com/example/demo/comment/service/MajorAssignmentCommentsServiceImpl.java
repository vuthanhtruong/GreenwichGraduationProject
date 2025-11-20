package com.example.demo.comment.service;

import com.example.demo.comment.dao.MajorAssignmentCommentsDAO;
import com.example.demo.comment.model.MajorAssignmentComments;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class MajorAssignmentCommentsServiceImpl implements MajorAssignmentCommentsService {

    private final MajorAssignmentCommentsDAO dao;

    public MajorAssignmentCommentsServiceImpl(MajorAssignmentCommentsDAO dao) {
        this.dao = dao;
    }

    @Override
    public void saveComment(MajorAssignmentComments comment) {
        dao.saveComment(comment);
    }

    @Override
    public MajorAssignmentComments findCommentById(String commentId) {
        return dao.findCommentById(commentId);
    }

    @Override
    public List<MajorAssignmentComments> findCommentsByAssignmentId(String assignmentId) {
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
    public Map<String, String> validateComment(MajorAssignmentComments comment) {
        return dao.validateComment(comment);
    }
}
