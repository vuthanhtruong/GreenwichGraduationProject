package com.example.demo.submission.dao;

import com.example.demo.submission.model.SubmissionFeedbacks;
import com.example.demo.submission.model.SubmissionFeedbacksId;
import com.example.demo.submission.model.Submissions;
import com.example.demo.user.majorLecturer.model.MajorLecturers;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class SubmissionFeedbacksDAOImpl implements SubmissionFeedbacksDAO {

    private static final Logger logger = LoggerFactory.getLogger(SubmissionFeedbacksDAOImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public SubmissionFeedbacks saveFeedback(MajorLecturers announcer,
                                            Submissions submission,
                                            String content,
                                            com.example.demo.entity.Enums.Grades grade) {
        if (announcer == null || submission == null) {
            throw new IllegalArgumentException("Announcer and submission cannot be null");
        }

        SubmissionFeedbacksId id = new SubmissionFeedbacksId(
                announcer.getId(),
                submission.getId().getSubmittedBy(),
                submission.getId().getAssignmentSubmitSlotId()
        );

        SubmissionFeedbacks feedback = entityManager.find(SubmissionFeedbacks.class, id);
        if (feedback == null) {
            feedback = new SubmissionFeedbacks(announcer, submission);
        }

        feedback.setContent(content);
        feedback.setGrade(grade);

        try {
            feedback = entityManager.merge(feedback);
            logger.info("Saved feedback for submission: {}-{}",
                    submission.getId().getSubmittedBy(), submission.getId().getAssignmentSubmitSlotId());
            return feedback;
        } catch (Exception e) {
            logger.error("Error saving feedback: {}", e.getMessage());
            throw new RuntimeException("Failed to save feedback: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<SubmissionFeedbacks> findByAnnouncerAndSubmission(MajorLecturers announcer, Submissions submission) {
        if (announcer == null || submission == null) return Optional.empty();

        SubmissionFeedbacksId id = new SubmissionFeedbacksId(
                announcer.getId(),
                submission.getId().getSubmittedBy(),
                submission.getId().getAssignmentSubmitSlotId()
        );

        SubmissionFeedbacks feedback = entityManager.find(SubmissionFeedbacks.class, id);
        return Optional.ofNullable(feedback);
    }

    @Override
    public Optional<SubmissionFeedbacks> findBySubmission(Submissions submission) {
        if (submission == null) return Optional.empty();

        String jpql = "SELECT f FROM SubmissionFeedbacks f WHERE " +
                "f.submission.id.submittedBy = :submittedBy AND " +
                "f.submission.id.assignmentSubmitSlotId = :slotId";

        try {
            SubmissionFeedbacks feedback = entityManager.createQuery(jpql, SubmissionFeedbacks.class)
                    .setParameter("submittedBy", submission.getId().getSubmittedBy())
                    .setParameter("slotId", submission.getId().getAssignmentSubmitSlotId())
                    .getSingleResult();
            return Optional.ofNullable(feedback);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public void delete(SubmissionFeedbacks feedback) {
        if (feedback == null) return;
        try {
            SubmissionFeedbacks managed = entityManager.contains(feedback) ? feedback : entityManager.merge(feedback);
            entityManager.remove(managed);
            logger.info("Deleted feedback for submission: {}-{}",
                    feedback.getSubmission().getId().getSubmittedBy(),
                    feedback.getSubmission().getId().getAssignmentSubmitSlotId());
        } catch (Exception e) {
            logger.error("Error deleting feedback: {}", e.getMessage());
            throw new RuntimeException("Failed to delete feedback", e);
        }
    }

    @Override
    public boolean existsBySubmission(Submissions submission) {
        return findBySubmission(submission).isPresent();
    }

    @Override
    public List<Submissions> getSubmissionsWithFeedback(String assignmentSlotId) {
        String jpql = """
            SELECT DISTINCT s FROM Submissions s
            JOIN FETCH s.submittedBy
            JOIN SubmissionFeedbacks f ON 
                f.submission.id.submittedBy = s.id.submittedBy AND 
                f.submission.id.assignmentSubmitSlotId = s.id.assignmentSubmitSlotId
            WHERE s.id.assignmentSubmitSlotId = :slotId
            """;
        return entityManager.createQuery(jpql, Submissions.class)
                .setParameter("slotId", assignmentSlotId)
                .getResultList();
    }

    @Override
    public List<Submissions> getSubmissionsWithoutFeedback(String assignmentSlotId) {
        String jpql = """
            SELECT s FROM Submissions s
            JOIN FETCH s.submittedBy
            WHERE s.id.assignmentSubmitSlotId = :slotId
              AND NOT EXISTS (
                SELECT 1 FROM SubmissionFeedbacks f 
                WHERE f.submission.id.submittedBy = s.id.submittedBy 
                  AND f.submission.id.assignmentSubmitSlotId = s.id.assignmentSubmitSlotId
              )
            """;
        return entityManager.createQuery(jpql, Submissions.class)
                .setParameter("slotId", assignmentSlotId)
                .getResultList();
    }

    @Override
    public long countSubmissionsWithFeedback(String assignmentSlotId) {
        String jpql = """
            SELECT COUNT(DISTINCT s.id) FROM Submissions s
            JOIN SubmissionFeedbacks f ON 
                f.submission.id.submittedBy = s.id.submittedBy AND 
                f.submission.id.assignmentSubmitSlotId = s.id.assignmentSubmitSlotId
            WHERE s.id.assignmentSubmitSlotId = :slotId
            """;
        return entityManager.createQuery(jpql, Long.class)
                .setParameter("slotId", assignmentSlotId)
                .getSingleResult();
    }

    @Override
    public long countSubmissionsWithoutFeedback(String assignmentSlotId) {
        String jpql = """
            SELECT COUNT(s) FROM Submissions s
            WHERE s.id.assignmentSubmitSlotId = :slotId
              AND NOT EXISTS (
                SELECT 1 FROM SubmissionFeedbacks f 
                WHERE f.submission.id.submittedBy = s.id.submittedBy 
                  AND f.submission.id.assignmentSubmitSlotId = s.id.assignmentSubmitSlotId
              )
            """;
        return entityManager.createQuery(jpql, Long.class)
                .setParameter("slotId", assignmentSlotId)
                .getSingleResult();
    }
}