package com.example.demo.submission.dao;

import com.example.demo.entity.Enums.Grades;
import com.example.demo.submission.model.SubmissionFeedbacks;
import com.example.demo.submission.model.SubmissionFeedbacksId;
import com.example.demo.submission.model.Submissions;

import java.util.List;

public interface SubmissionFeedbacksDAO {
    void save(SubmissionFeedbacks feedback);
    SubmissionFeedbacks findById(SubmissionFeedbacksId id);
    SubmissionFeedbacks findBySubmission(String submittedBy, String assignmentSlotId);
    List<Submissions> getSubmissionsWithGrade(String assignmentSlotId);
    List<Submissions> getSubmissionsWithoutGrade(String assignmentSlotId);
    SubmissionFeedbacks getFeedback(String submittedBy, String assignmentSlotId, String announcerId);
    void saveFeedback(String submittedBy, String assignmentSlotId, String announcerId, String content, Grades grade);
}