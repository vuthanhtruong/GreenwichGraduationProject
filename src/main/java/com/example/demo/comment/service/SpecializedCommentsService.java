package com.example.demo.comment.service;

import com.example.demo.comment.model.SpecializedComments;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface SpecializedCommentsService {
    void saveComment(SpecializedComments comment);

    SpecializedComments findCommentById(String commentId);

    List<SpecializedComments> findCommentsByPostId(String postId);

    List<SpecializedComments> findPaginatedCommentsByPostId(String postId, int firstResult, int pageSize);

    long countCommentsByPostId(String postId);

    void deleteComment(String commentId);

    boolean existsCommentById(String commentId);

    String generateUniqueCommentId(String postId, LocalDate createdDate);

    Map<String, String> validateComment(SpecializedComments comment);
    List<String> getCommentNotificationsForLecturer(String lecturerId);
}
