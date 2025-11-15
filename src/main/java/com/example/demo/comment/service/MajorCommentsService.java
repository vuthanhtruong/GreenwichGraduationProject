package com.example.demo.comment.service;

import com.example.demo.comment.model.MajorComments;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface MajorCommentsService {
    void saveComment(MajorComments comment);

    MajorComments findCommentById(String commentId);

    List<MajorComments> findCommentsByPostId(String postId);

    List<MajorComments> findPaginatedCommentsByPostId(String postId, int firstResult, int pageSize);

    long countCommentsByPostId(String postId);

    void deleteComment(String commentId);

    boolean existsCommentById(String commentId);

    String generateUniqueCommentId(String postId, LocalDate createdDate);

    Map<String, String> validateComment(MajorComments comment);
    List<String> getCommentNotificationsForLecturer(String lecturerId);
}
