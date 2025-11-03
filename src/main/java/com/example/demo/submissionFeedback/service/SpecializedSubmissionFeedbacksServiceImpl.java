// com.example.demo.submissionFeedback.service.SpecializedSubmissionFeedbacksServiceImpl.java
package com.example.demo.submissionFeedback.service;

import com.example.demo.entity.Enums.Grades;
import com.example.demo.submission.model.SpecializedSubmissions;
import com.example.demo.submissionFeedback.dao.SpecializedSubmissionFeedbacksDAO;
import com.example.demo.submissionFeedback.model.SpecializedSubmissionFeedbacks;
import com.example.demo.submissionFeedback.model.SpecializedSubmissionFeedbacksId;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecializedSubmissionFeedbacksServiceImpl implements SpecializedSubmissionFeedbacksService {

    private final SpecializedSubmissionFeedbacksDAO feedbackDAO;

    public SpecializedSubmissionFeedbacksServiceImpl(SpecializedSubmissionFeedbacksDAO feedbackDAO) {
        this.feedbackDAO = feedbackDAO;
    }

    @Override
    public void save(SpecializedSubmissionFeedbacks feedback) {
        feedbackDAO.save(feedback);
    }

    @Override
    public SpecializedSubmissionFeedbacks findById(SpecializedSubmissionFeedbacksId id) {
        return feedbackDAO.findById(id);
    }

    @Override
    public SpecializedSubmissionFeedbacks findBySubmission(String submittedBy, String assignmentSlotId) {
        return feedbackDAO.findBySubmission(submittedBy, assignmentSlotId);
    }

    @Override
    public List<SpecializedSubmissions> getSubmissionsWithGrade(String assignmentSlotId) {
        return feedbackDAO.getSubmissionsWithGrade(assignmentSlotId);
    }

    @Override
    public List<SpecializedSubmissions> getSubmissionsWithoutGrade(String assignmentSlotId) {
        return feedbackDAO.getSubmissionsWithoutGrade(assignmentSlotId);
    }

    @Override
    public SpecializedSubmissionFeedbacks getFeedback(String submittedBy, String assignmentSlotId, String announcerId) {
        return feedbackDAO.getFeedback(submittedBy, assignmentSlotId, announcerId);
    }

    @Override
    public void saveFeedback(String submittedBy, String assignmentSlotId, String announcerId, String content, Grades grade) {
        feedbackDAO.saveFeedback(submittedBy, assignmentSlotId, announcerId, content, grade);
    }
}