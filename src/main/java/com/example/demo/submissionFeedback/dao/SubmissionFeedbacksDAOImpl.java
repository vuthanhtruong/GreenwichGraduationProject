// com.example.demo.submissionFeedback.dao.SubmissionFeedbacksDAOImpl.java
package com.example.demo.submissionFeedback.dao;

import com.example.demo.entity.Enums.Grades;
import com.example.demo.submission.model.*;
import com.example.demo.submission.service.SubmissionsService;
import com.example.demo.submissionFeedback.model.SubmissionFeedbacks;
import com.example.demo.submissionFeedback.model.SubmissionFeedbacksId;
import com.example.demo.user.majorLecturer.model.MajorLecturers;
import com.example.demo.user.majorLecturer.service.MajorLecturersService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class SubmissionFeedbacksDAOImpl implements SubmissionFeedbacksDAO {

    @PersistenceContext
    private EntityManager em;

    private final MajorLecturersService majorLecturersService;
    private final SubmissionsService submissionsService;

    public SubmissionFeedbacksDAOImpl(MajorLecturersService majorLecturersService, SubmissionsService submissionsService) {
        this.majorLecturersService = majorLecturersService;
        this.submissionsService = submissionsService;
    }

    @Override
    public void save(SubmissionFeedbacks feedback) {
        if (feedback.getId() == null) {
            em.persist(feedback);
        } else {
            em.merge(feedback);
        }
    }

    @Override
    public SubmissionFeedbacks findById(SubmissionFeedbacksId id) {
        return em.find(SubmissionFeedbacks.class, id);
    }

    @Override
    public SubmissionFeedbacks findBySubmission(String submittedBy, String assignmentSlotId) {
        try {
            return em.createQuery("""
                    SELECT f FROM SubmissionFeedbacks f
                    WHERE f.id.submittedBy = :submittedBy
                      AND f.id.assignmentSubmitSlotId = :assignmentSlotId
                    """, SubmissionFeedbacks.class)
                    .setParameter("submittedBy", submittedBy)
                    .setParameter("assignmentSlotId", assignmentSlotId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    // ĐÃ CÓ ĐIỂM (có feedback + grade != null)
    @Override
    public List<Submissions> getSubmissionsWithGrade(String assignmentSlotId) {
        return em.createQuery("""
                SELECT s FROM Submissions s
                JOIN SubmissionFeedbacks f ON s.id = f.submission.id
                WHERE s.id.assignmentSubmitSlotId = :assignmentSlotId
                  AND f.grade IS NOT NULL
                """, Submissions.class)
                .setParameter("assignmentSlotId", assignmentSlotId)
                .getResultList();
    }

    // CHƯA CÓ ĐIỂM (có nộp nhưng chưa chấm)
    @Override
    public List<Submissions> getSubmissionsWithoutGrade(String assignmentSlotId) {
        return em.createQuery("""
                SELECT s FROM Submissions s
                LEFT JOIN SubmissionFeedbacks f 
                  ON s.id.submittedBy = f.id.submittedBy 
                 AND s.id.assignmentSubmitSlotId = f.id.assignmentSubmitSlotId
                WHERE s.id.assignmentSubmitSlotId = :assignmentSlotId
                  AND (f.grade IS NULL OR f.id IS NULL)
                """, Submissions.class)
                .setParameter("assignmentSlotId", assignmentSlotId)
                .getResultList();
    }
    // SubmissionFeedbacksServiceImpl.java
    @Override
    public SubmissionFeedbacks getFeedback(String submittedBy, String assignmentSlotId, String announcerId) {
        SubmissionFeedbacksId id = new SubmissionFeedbacksId(announcerId, submittedBy, assignmentSlotId);
        return findById(id);
    }

    @Override
    public void saveFeedback(String submittedBy, String assignmentSlotId, String announcerId, String content, Grades grade) {
        SubmissionFeedbacksId id = new SubmissionFeedbacksId(announcerId, submittedBy, assignmentSlotId);
        SubmissionFeedbacks feedback = findById(id);

        if (feedback == null) {
            SubmissionsId submissionId = new SubmissionsId(submittedBy, assignmentSlotId);
            Submissions submission = submissionsService.findById(submissionId);
            if (submission == null) {
                throw new IllegalArgumentException("Bài nộp không tồn tại!");
            }
            MajorLecturers lecturer = majorLecturersService.getLecturerById(announcerId);
            feedback = new SubmissionFeedbacks(lecturer, submission);
        }

        feedback.setContent(content);
        feedback.setGrade(grade);
        save(feedback);
    }
}