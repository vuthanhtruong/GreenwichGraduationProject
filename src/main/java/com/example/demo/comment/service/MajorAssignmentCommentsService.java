package com.example.demo.comment.service;

import com.example.demo.comment.model.MajorAssignmentComments;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface MajorAssignmentCommentsService {

    void saveComment(MajorAssignmentComments comment);

    MajorAssignmentComments findCommentById(String commentId);

    List<MajorAssignmentComments> findCommentsByAssignmentId(String assignmentId);

    long countCommentsByAssignmentId(String assignmentId);

    void deleteComment(String commentId);

    boolean existsCommentById(String commentId);

    String generateUniqueCommentId(String assignmentId, LocalDate createdDate);

    Map<String, String> validateComment(MajorAssignmentComments comment);

    List<String> getCommentNotificationsForLecturer(String lecturerId);

}
