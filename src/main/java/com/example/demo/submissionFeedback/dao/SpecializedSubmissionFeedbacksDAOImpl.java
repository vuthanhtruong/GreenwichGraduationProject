// com.example.demo.submissionFeedback.dao.SpecializedSubmissionFeedbacksDAOImpl.java
package com.example.demo.submissionFeedback.dao;

import com.example.demo.entity.Enums.Grades;
import com.example.demo.submission.model.SpecializedSubmissions;
import com.example.demo.submission.model.SpecializedSubmissionsId;
import com.example.demo.submission.service.SpecializedSubmissionsService;
import com.example.demo.submissionFeedback.model.SpecializedSubmissionFeedbacks;
import com.example.demo.submissionFeedback.model.SpecializedSubmissionFeedbacksId;
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
public class SpecializedSubmissionFeedbacksDAOImpl implements SpecializedSubmissionFeedbacksDAO {

    @PersistenceContext
    private EntityManager em;

    private final MajorLecturersService lecturerService;
    private final SpecializedSubmissionsService submissionsService;

    public SpecializedSubmissionFeedbacksDAOImpl(
            MajorLecturersService lecturerService,
            SpecializedSubmissionsService submissionsService) {
        this.lecturerService = lecturerService;
        this.submissionsService = submissionsService;
    }

    @Override
    public void save(SpecializedSubmissionFeedbacks feedback) {
        if (feedback.getId() == null) {
            em.persist(feedback);
        } else {
            em.merge(feedback);
        }
    }

    @Override
    public SpecializedSubmissionFeedbacks findById(SpecializedSubmissionFeedbacksId id) {
        return em.find(SpecializedSubmissionFeedbacks.class, id );
        }

        @Override
        public SpecializedSubmissionFeedbacks findBySubmission(String submittedBy, String assignmentSlotId) {
            try {
                return em.createQuery("""
                    SELECT f FROM SpecializedSubmissionFeedbacks f
                    WHERE f.id.submittedBy = :submittedBy
                      AND f.id.assignmentSubmitSlotId = :assignmentSlotId
                    """, SpecializedSubmissionFeedbacks.class)
                        .setParameter("submittedBy", submittedBy)
                        .setParameter("assignmentSlotId", assignmentSlotId)
                        .getSingleResult();
            } catch (NoResultException e) {
                return null;
            }
        }

        @Override
        public List<SpecializedSubmissions> getSubmissionsWithGrade(String assignmentSlotId) {
            return em.createQuery("""
                SELECT s FROM SpecializedSubmissions s
                JOIN SpecializedSubmissionFeedbacks f ON s.id = f.submission.id
                WHERE s.id.assignmentSubmitSlotId = :assignmentSlotId
                  AND f.grade IS NOT NULL
                """, SpecializedSubmissions.class)
                    .setParameter("assignmentSlotId", assignmentSlotId)
                    .getResultList();
        }

        @Override
        public List<SpecializedSubmissions> getSubmissionsWithoutGrade(String assignmentSlotId) {
            return em.createQuery("""
                SELECT s FROM SpecializedSubmissions s
                LEFT JOIN SpecializedSubmissionFeedbacks f 
                  ON s.id.submittedBy = f.id.submittedBy 
                 AND s.id.assignmentSubmitSlotId = f.id.assignmentSubmitSlotId
                WHERE s.id.assignmentSubmitSlotId = :assignmentSlotId
                  AND (f.grade IS NULL OR f.id IS NULL)
                """, SpecializedSubmissions.class)
                    .setParameter("assignmentSlotId", assignmentSlotId)
                    .getResultList();
        }

        @Override
        public SpecializedSubmissionFeedbacks getFeedback(String submittedBy, String assignmentSlotId, String announcerId) {
            SpecializedSubmissionFeedbacksId id = new SpecializedSubmissionFeedbacksId(announcerId, submittedBy, assignmentSlotId);
            return findById(id);
        }

        @Override
        public void saveFeedback(String submittedBy, String assignmentSlotId, String announcerId, String content, Grades grade) {
            SpecializedSubmissionFeedbacksId id = new SpecializedSubmissionFeedbacksId(announcerId, submittedBy, assignmentSlotId);
            SpecializedSubmissionFeedbacks feedback = findById(id);

            if (feedback == null) {
                SpecializedSubmissionsId submissionId = new SpecializedSubmissionsId(submittedBy, assignmentSlotId);
                SpecializedSubmissions submission = submissionsService.findById(submissionId);
                if (submission == null) {
                    throw new IllegalArgumentException("Bài nộp không tồn tại!");
                }
                MajorLecturers lecturer = lecturerService.getLecturerById(announcerId);
                feedback = new SpecializedSubmissionFeedbacks(lecturer, submission);
            }

            feedback.setContent(content);
            feedback.setGrade(grade);
            save(feedback);
        }
    }