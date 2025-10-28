package com.example.demo.comment.service;

import com.example.demo.comment.dao.MinorCommentsDAO;
import com.example.demo.comment.model.MinorComments;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class MinorCommentsServiceImpl implements MinorCommentsService {

    private final MinorCommentsDAO minorCommentsDAO;

    public MinorCommentsServiceImpl(MinorCommentsDAO minorCommentsDAO) {
        this.minorCommentsDAO = minorCommentsDAO;
    }

    @Override
    public MinorComments getCommentById(String commentId) {
        return minorCommentsDAO.getCommentById(commentId);
    }

    @Override
    public void saveComment(MinorComments comment) {
        minorCommentsDAO.saveComment(comment);
    }

    @Override
    public List<MinorComments> getCommentsByPostId(String postId) {
        return minorCommentsDAO.getCommentsByPostId(postId);
    }

    @Override
    public Map<String, String> validateComment(MinorComments comment) {
        return minorCommentsDAO.validateComment(comment);
    }

    @Override
    public String generateUniqueCommentId(String postId, LocalDate createdDate) {
        return minorCommentsDAO.generateUniqueCommentId(postId, createdDate);
    }
}