package com.example.demo.comment.service;

import com.example.demo.comment.model.StudentComments;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface StudentCommentsService {
    void saveComment(StudentComments comment);

    StudentComments findCommentById(String commentId);

    List<StudentComments> findCommentsByPostId(String postId);

    List<StudentComments> findPaginatedCommentsByPostId(String postId, int firstResult, int pageSize);

    long countCommentsByPostId(String postId);

    void deleteComment(String commentId);

    boolean existsCommentById(String commentId);

    String generateUniqueCommentId(String postId, LocalDate createdDate);

    Map<String, String> validateComment(StudentComments comment);
}
