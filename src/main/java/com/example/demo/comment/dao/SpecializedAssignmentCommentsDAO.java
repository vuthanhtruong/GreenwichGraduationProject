package com.example.demo.comment.dao;

import com.example.demo.comment.model.SpecializedAssignmentComments;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface SpecializedAssignmentCommentsDAO {

    void saveComment(SpecializedAssignmentComments comment);

    SpecializedAssignmentComments findCommentById(String commentId);

    List<SpecializedAssignmentComments> findCommentsByAssignmentId(String assignmentId);

    long countCommentsByAssignmentId(String assignmentId);

    void deleteComment(String commentId);

    boolean existsCommentById(String commentId);

    String generateUniqueCommentId(String assignmentId, LocalDate createdDate);

    Map<String, String> validateComment(SpecializedAssignmentComments comment);

}
