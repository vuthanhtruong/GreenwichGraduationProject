package com.example.demo.comment.dao;

import com.example.demo.comment.model.SpecializedComments;

import java.util.List;

public interface SpecializedCommentsDAO {
    void saveComment(SpecializedComments comment);

    SpecializedComments findCommentById(String commentId);

    List<SpecializedComments> findCommentsByPostId(String postId);

    List<SpecializedComments> findPaginatedCommentsByPostId(String postId, int firstResult, int pageSize);

    long countCommentsByPostId(String postId);

    void deleteComment(String commentId);

    boolean existsCommentById(String commentId);
}
