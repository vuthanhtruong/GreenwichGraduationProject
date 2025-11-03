// src/main/java/com/example/demo/submission/service/SubmissionFeedbacksService.java
package com.example.demo.submissionFeedback.service;

import com.example.demo.entity.Enums.Grades;
import com.example.demo.submissionFeedback.model.SubmissionFeedbacks;
import com.example.demo.submissionFeedback.model.SubmissionFeedbacksId;
import com.example.demo.submission.model.Submissions;

import java.util.List;

public interface SubmissionFeedbacksService {
    void save(SubmissionFeedbacks feedback);
    SubmissionFeedbacks findById(SubmissionFeedbacksId id);
    SubmissionFeedbacks findBySubmission(String submittedBy, String assignmentSlotId);
    List<Submissions> getSubmissionsWithGrade(String assignmentSlotId);
    List<Submissions> getSubmissionsWithoutGrade(String assignmentSlotId);
    SubmissionFeedbacks getFeedback(String submittedBy, String assignmentSlotId, String announcerId);
    void saveFeedback(String submittedBy, String assignmentSlotId, String announcerId, String content, Grades grade);
}