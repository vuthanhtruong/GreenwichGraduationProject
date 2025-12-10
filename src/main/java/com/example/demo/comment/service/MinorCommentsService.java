package com.example.demo.comment.service;

import com.example.demo.comment.model.MinorComments;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface MinorCommentsService {
    MinorComments getCommentById(String commentId);
    void saveComment(MinorComments comment);
    List<MinorComments> getCommentsByPostId(String postId);
    Map<String, String> validateComment(MinorComments comment);
    String generateUniqueCommentId(String postId, LocalDate createdDate);
    List<String> getCommentNotificationsForLecturer(String lecturerId);
    void deleteComment(String commentId);
}