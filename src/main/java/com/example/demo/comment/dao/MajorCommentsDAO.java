package com.example.demo.comment.dao;

import com.example.demo.comment.model.MajorComments;

import java.util.List;

public interface MajorCommentsDAO {
    void saveComment(MajorComments comment);

    MajorComments findCommentById(String commentId);

    List<MajorComments> findCommentsByPostId(String postId);

    List<MajorComments> findPaginatedCommentsByPostId(String postId, int firstResult, int pageSize);

    long countCommentsByPostId(String postId);

    void deleteComment(String commentId);

    boolean existsCommentById(String commentId);
}
