// src/main/java/com/example/demo/submission/service/SubmissionFeedbacksServiceImpl.java
package com.example.demo.submission.service;

import com.example.demo.entity.Enums.Grades;
import com.example.demo.submission.dao.SubmissionFeedbacksDAO;
import com.example.demo.submission.model.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubmissionFeedbacksServiceImpl implements SubmissionFeedbacksService {
    @Override
    public SubmissionFeedbacks getFeedback(String submittedBy, String assignmentSlotId, String announcerId) {
        return feedbackDAO.getFeedback(submittedBy, assignmentSlotId, announcerId);
    }

    @Override
    public void saveFeedback(String submittedBy, String assignmentSlotId, String announcerId, String content, Grades grade) {
        feedbackDAO.saveFeedback(submittedBy, assignmentSlotId, announcerId, content, grade);
    }

    @Override
    public void save(SubmissionFeedbacks feedback) {
        feedbackDAO.save(feedback);
    }

    @Override
    public SubmissionFeedbacks findById(SubmissionFeedbacksId id) {
        return feedbackDAO.findById(id);
    }

    @Override
    public SubmissionFeedbacks findBySubmission(String submittedBy, String assignmentSlotId) {
        return feedbackDAO.findBySubmission(submittedBy, assignmentSlotId);
    }

    @Override
    public List<Submissions> getSubmissionsWithGrade(String assignmentSlotId) {
        return feedbackDAO.getSubmissionsWithGrade(assignmentSlotId);
    }

    @Override
    public List<Submissions> getSubmissionsWithoutGrade(String assignmentSlotId) {
        return feedbackDAO.getSubmissionsWithoutGrade(assignmentSlotId);
    }

    private final SubmissionFeedbacksDAO feedbackDAO;

    public SubmissionFeedbacksServiceImpl(
            SubmissionFeedbacksDAO feedbackDAO) {
        this.feedbackDAO = feedbackDAO;
    }
}