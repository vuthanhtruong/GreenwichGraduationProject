package com.example.demo.comment.service;

import com.example.demo.comment.dao.StudentCommentsDAO;
import com.example.demo.comment.model.StudentComments;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class StudentCommentsServiceImpl implements StudentCommentsService{
    @Override
    public Map<String, String> validateComment(StudentComments comment) {
        return studentCommentsDAO.validateComment(comment);
    }

    @Override
    public String generateUniqueCommentId(String postId, LocalDate createdDate) {
        return studentCommentsDAO.generateUniqueCommentId(postId, createdDate);
    }

    @Override
    public void saveComment(StudentComments comment) {
        studentCommentsDAO.saveComment(comment);
    }

    @Override
    public StudentComments findCommentById(String commentId) {
        return studentCommentsDAO.findCommentById(commentId);
    }

    @Override
    public List<StudentComments> findCommentsByPostId(String postId) {
        return studentCommentsDAO.findCommentsByPostId(postId);
    }

    @Override
    public List<StudentComments> findPaginatedCommentsByPostId(String postId, int firstResult, int pageSize) {
        return studentCommentsDAO.findPaginatedCommentsByPostId(postId, firstResult, pageSize);
    }

    @Override
    public long countCommentsByPostId(String postId) {
        return studentCommentsDAO.countCommentsByPostId(postId);
    }

    @Override
    public void deleteComment(String commentId) {
        studentCommentsDAO.deleteComment(commentId);
    }

    @Override
    public boolean existsCommentById(String commentId) {
        return studentCommentsDAO.existsCommentById(commentId);
    }

    private final StudentCommentsDAO studentCommentsDAO;

    public StudentCommentsServiceImpl(StudentCommentsDAO studentCommentsDAO) {
        this.studentCommentsDAO = studentCommentsDAO;
    }
}
