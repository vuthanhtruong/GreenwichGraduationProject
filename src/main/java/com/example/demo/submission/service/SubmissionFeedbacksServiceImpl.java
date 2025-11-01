// src/main/java/com/example/demo/submission/service/SubmissionFeedbacksServiceImpl.java
package com.example.demo.submission.service;

import com.example.demo.submission.dao.SubmissionFeedbacksDAO;
import com.example.demo.submission.model.SubmissionFeedbacks;
import com.example.demo.submission.model.Submissions;
import com.example.demo.user.majorLecturer.model.MajorLecturers;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubmissionFeedbacksServiceImpl implements SubmissionFeedbacksService {
    @Override
    public Optional<SubmissionFeedbacks> findByAnnouncerAndSubmission(MajorLecturers announcer, Submissions submission) {
        return feedbackDAO.findByAnnouncerAndSubmission(announcer, submission);
    }

    @Override
    public Optional<SubmissionFeedbacks> findBySubmission(Submissions submission) {
        return feedbackDAO.findBySubmission(submission);
    }

    @Override
    public void delete(SubmissionFeedbacks feedback) {
        feedbackDAO.delete(feedback);
    }

    @Override
    public boolean existsBySubmission(Submissions submission) {
        return feedbackDAO.existsBySubmission(submission);
    }

    @Override
    public List<Submissions> getSubmissionsWithFeedback(String assignmentSlotId) {
        return feedbackDAO.getSubmissionsWithFeedback(assignmentSlotId);
    }

    @Override
    public List<Submissions> getSubmissionsWithoutFeedback(String assignmentSlotId) {
        return feedbackDAO.getSubmissionsWithoutFeedback(assignmentSlotId);
    }

    @Override
    public long countSubmissionsWithFeedback(String assignmentSlotId) {
        return feedbackDAO.countSubmissionsWithFeedback(assignmentSlotId);
    }

    @Override
    public long countSubmissionsWithoutFeedback(String assignmentSlotId) {
        return feedbackDAO.countSubmissionsWithoutFeedback(assignmentSlotId);
    }

    private final SubmissionFeedbacksDAO feedbackDAO;

    public SubmissionFeedbacksServiceImpl(SubmissionFeedbacksDAO feedbackDAO) {
        this.feedbackDAO = feedbackDAO;
    }

    @Override
    public SubmissionFeedbacks saveFeedback(MajorLecturers announcer, Submissions submission, String content, com.example.demo.entity.Enums.Grades grade) {
        return feedbackDAO.saveFeedback(announcer, submission, content, grade);
    }

}