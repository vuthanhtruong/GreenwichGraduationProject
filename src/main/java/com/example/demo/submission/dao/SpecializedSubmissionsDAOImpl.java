package com.example.demo.submission.dao;

import com.example.demo.document.model.SpecializedSubmissionDocuments;
import com.example.demo.post.specializedAssignmentSubmitSlots.model.SpecializedAssignmentSubmitSlots;
import com.example.demo.post.specializedAssignmentSubmitSlots.service.SpecializedAssignmentSubmitSlotsService;
import com.example.demo.submission.model.SpecializedSubmissions;
import com.example.demo.submission.model.SpecializedSubmissionsId;
import com.example.demo.user.student.model.Students;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Repository
@Transactional
public class SpecializedSubmissionsDAOImpl implements SpecializedSubmissionsDAO {

    private final SpecializedAssignmentSubmitSlotsService specializedAssignmentSubmitSlotsService;

    @PersistenceContext
    private EntityManager em;

    public SpecializedSubmissionsDAOImpl(SpecializedAssignmentSubmitSlotsService specializedAssignmentSubmitSlotsService) {
        this.specializedAssignmentSubmitSlotsService = specializedAssignmentSubmitSlotsService;
    }

    @Override
    public void save(SpecializedSubmissions submission) {
        em.persist(submission);
    }

    @Override
    public SpecializedSubmissions getByStudentAndSlot(String studentId, String slotId) {
        try {
            return em.createQuery(
                            "SELECT s FROM SpecializedSubmissions s " +
                                    "WHERE s.id.submittedBy = :studentId AND s.id.assignmentSubmitSlotId = :slotId",
                            SpecializedSubmissions.class)
                    .setParameter("studentId", studentId)
                    .setParameter("slotId", slotId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<SpecializedSubmissions> getBySlotId(String slotId) {
        try {
            return em.createQuery(
                            "SELECT s FROM SpecializedSubmissions s " +
                                    "WHERE s.id.assignmentSubmitSlotId = :slotId " +
                                    "ORDER BY s.createdAt DESC", SpecializedSubmissions.class)
                    .setParameter("slotId", slotId)
                    .getResultList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public boolean exists(String studentId, String slotId) {
        Long count = em.createQuery(
                        "SELECT COUNT(s) FROM SpecializedSubmissions s " +
                                "WHERE s.id.submittedBy = :studentId AND s.id.assignmentSubmitSlotId = :slotId",
                        Long.class)
                .setParameter("studentId", studentId)
                .setParameter("slotId", slotId)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public void submit(Students student, String postId, List<MultipartFile> files) {
        SpecializedAssignmentSubmitSlots slot = specializedAssignmentSubmitSlotsService.findByPostId(postId);
        if (slot == null) {
            throw new IllegalArgumentException("Assignment not found");
        }

        if (slot.getDeadline() != null && LocalDateTime.now().isAfter(slot.getDeadline())) {
            throw new IllegalStateException("Submission deadline has passed");
        }

        if (exists(student.getId(), postId)) {
            throw new IllegalStateException("You have already submitted this assignment");
        }

        SpecializedSubmissions submission = new SpecializedSubmissions();
        submission.setId(new SpecializedSubmissionsId(student.getId(), postId));
        submission.setSubmittedBy(student);
        submission.setAssignmentSubmitSlot(slot);
        submission.setCreatedAt(LocalDateTime.now());

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            SpecializedSubmissionDocuments doc = new SpecializedSubmissionDocuments();
            doc.setSubmission(submission);
            doc.setCreator(student);
            doc.setFilePath(file.getOriginalFilename());

            try {
                doc.setFileData(file.getBytes());
            } catch (Exception e) {
                throw new RuntimeException("Failed to read file: " + file.getOriginalFilename());
            }

            // DÙNG UUID TỰ ĐỘNG
            doc.setSubmissionDocumentId(java.util.UUID.randomUUID().toString());

            submission.addDocument(doc);
        }

        save(submission);
    }
    @Override
    public SpecializedSubmissions findById(SpecializedSubmissionsId id) {
        return em.find(SpecializedSubmissions.class, id);
    }

    @Override
    public List<SpecializedSubmissions> getSubmissionsByAssignment(String assignmentId) {
        try {
            return em.createQuery(
                            "SELECT s FROM SpecializedSubmissions s " +
                                    "WHERE s.id.assignmentSubmitSlotId = :assignmentId " +
                                    "ORDER BY s.createdAt DESC", SpecializedSubmissions.class)
                    .setParameter("assignmentId", assignmentId)
                    .getResultList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Students> getStudentsNotSubmitted(String classId, String assignmentId) {
        try {
            return em.createQuery("""
                SELECT s FROM Students s
                WHERE EXISTS (
                    SELECT 1 FROM Students_SpecializedClasses sc 
                    WHERE sc.student = s 
                      AND sc.classEntity.classId = :classId
                )
                AND s.id NOT IN (
                    SELECT sub.id.submittedBy 
                    FROM SpecializedSubmissions sub 
                    WHERE sub.id.assignmentSubmitSlotId = :assignmentId
                )
                ORDER BY s.firstName, s.lastName
                """, Students.class)
                    .setParameter("classId", classId)
                    .setParameter("assignmentId", assignmentId)
                    .getResultList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}