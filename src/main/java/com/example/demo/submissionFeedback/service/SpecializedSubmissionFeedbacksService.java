package com.example.demo.submissionFeedback.service;

import com.example.demo.entity.Enums.Grades;
import com.example.demo.submission.model.SpecializedSubmissions;
import com.example.demo.submissionFeedback.model.SpecializedSubmissionFeedbacks;
import com.example.demo.submissionFeedback.model.SpecializedSubmissionFeedbacksId;

import java.util.List;

public interface SpecializedSubmissionFeedbacksService {
    void save(SpecializedSubmissionFeedbacks feedback);
    SpecializedSubmissionFeedbacks findById(SpecializedSubmissionFeedbacksId id);
    SpecializedSubmissionFeedbacks findBySubmission(String submittedBy, String assignmentSlotId);
    List<SpecializedSubmissions> getSubmissionsWithGrade(String assignmentSlotId);
    List<SpecializedSubmissions> getSubmissionsWithoutGrade(String assignmentSlotId);
    SpecializedSubmissionFeedbacks getFeedback(String submittedBy, String assignmentSlotId, String announcerId);
    void saveFeedback(String submittedBy, String assignmentSlotId, String announcerId, String content, Grades grade);
}
