package com.example.demo.submission.dao;

import com.example.demo.submission.model.SubmissionFeedbacks;
import com.example.demo.submission.model.Submissions;
import com.example.demo.user.majorLecturer.model.MajorLecturers;

import java.util.List;
import java.util.Optional;

public interface SubmissionFeedbacksDAO {
    SubmissionFeedbacks saveFeedback(MajorLecturers announcer, Submissions submission, String content, com.example.demo.entity.Enums.Grades grade);
    Optional<SubmissionFeedbacks> findByAnnouncerAndSubmission(MajorLecturers announcer, Submissions submission);
    Optional<SubmissionFeedbacks> findBySubmission(Submissions submission);
    void delete(SubmissionFeedbacks feedback);
    boolean existsBySubmission(Submissions submission);
    List<Submissions> getSubmissionsWithFeedback(String assignmentSlotId);
    List<Submissions> getSubmissionsWithoutFeedback(String assignmentSlotId);
    long countSubmissionsWithFeedback(String assignmentSlotId);
    long countSubmissionsWithoutFeedback(String assignmentSlotId);
}
