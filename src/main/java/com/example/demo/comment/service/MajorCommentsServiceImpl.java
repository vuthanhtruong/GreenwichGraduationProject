package com.example.demo.comment.service;

import com.example.demo.comment.dao.MajorCommentsDAO;
import com.example.demo.comment.model.MajorComments;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class MajorCommentsServiceImpl implements MajorCommentsService{
    @Override
    public Map<String, String> validateComment(MajorComments comment) {
        return majorCommentsDAO.validateComment(comment);
    }

    @Override
    public String generateUniqueCommentId(String postId, LocalDate createdDate) {
        return majorCommentsDAO.generateUniqueCommentId(postId, createdDate);
    }

    @Override
    public void saveComment(MajorComments comment) {
        majorCommentsDAO.saveComment(comment);
    }

    @Override
    public MajorComments findCommentById(String commentId) {
        return majorCommentsDAO.findCommentById(commentId);
    }

    @Override
    public List<MajorComments> findCommentsByPostId(String postId) {
        return majorCommentsDAO.findCommentsByPostId(postId);
    }

    @Override
    public List<MajorComments> findPaginatedCommentsByPostId(String postId, int firstResult, int pageSize) {
        return majorCommentsDAO.findPaginatedCommentsByPostId(postId, firstResult, pageSize);
    }

    @Override
    public long countCommentsByPostId(String postId) {
        return majorCommentsDAO.countCommentsByPostId(postId);
    }

    @Override
    public void deleteComment(String commentId) {
        majorCommentsDAO.deleteComment(commentId);
    }

    @Override
    public boolean existsCommentById(String commentId) {
        return majorCommentsDAO.existsCommentById(commentId);
    }

    private final MajorCommentsDAO majorCommentsDAO;

    public MajorCommentsServiceImpl(MajorCommentsDAO majorCommentsDAO) {
        this.majorCommentsDAO = majorCommentsDAO;
    }
}
