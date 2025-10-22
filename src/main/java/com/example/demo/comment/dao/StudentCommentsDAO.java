package com.example.demo.comment.dao;

import com.example.demo.comment.model.StudentComments;

import java.util.List;

public interface StudentCommentsDAO {

    void saveComment(StudentComments comment);

    StudentComments findCommentById(String commentId);

    List<StudentComments> findCommentsByPostId(String postId);

    List<StudentComments> findPaginatedCommentsByPostId(String postId, int firstResult, int pageSize);

    long countCommentsByPostId(String postId);

    void deleteComment(String commentId);

    boolean existsCommentById(String commentId);
}