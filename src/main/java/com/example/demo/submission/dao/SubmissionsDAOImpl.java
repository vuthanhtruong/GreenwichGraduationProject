package com.example.demo.submission.dao;

import com.example.demo.document.model.SubmissionDocuments;
import com.example.demo.document.service.SubmissionDocumentsService;
import com.example.demo.post.majorAssignmentSubmitSlots.model.AssignmentSubmitSlots;
import com.example.demo.post.majorAssignmentSubmitSlots.service.AssignmentSubmitSlotsService;
import com.example.demo.submission.model.Submissions;
import com.example.demo.submission.model.SubmissionsId;
import com.example.demo.user.student.model.Students;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
@Transactional
public class SubmissionsDAOImpl implements SubmissionsDAO {

    @PersistenceContext
    private EntityManager em;

    private final AssignmentSubmitSlotsService assignmentSubmitSlotsService;
    private final SubmissionDocumentsService submissionDocumentsService; // THÊM

    public SubmissionsDAOImpl(AssignmentSubmitSlotsService assignmentSubmitSlotsService, SubmissionDocumentsService submissionDocumentsService) {
        this.assignmentSubmitSlotsService = assignmentSubmitSlotsService;
        this.submissionDocumentsService = submissionDocumentsService;
    }
    @Override
    public List<Students> getStudentsNotSubmitted(String classId, String assignmentId) {
        try {
            return em.createQuery("""
                SELECT s FROM Students s
                WHERE EXISTS (
                    SELECT 1 FROM Students_Classes sc 
                    WHERE sc.student = s 
                      AND sc.classEntity.classId = :classId
                )
                AND s.id NOT IN (
                    SELECT sub.id.submittedBy 
                    FROM Submissions sub 
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

    @Override
    public List<Submissions> getSubmissionsByAssignment(String assignmentId) {
        try {
            return em.createQuery(
                            "SELECT s FROM Submissions s " +
                                    "WHERE s.id.assignmentSubmitSlotId = :assignmentId " +
                                    "ORDER BY s.createdAt DESC", Submissions.class)
                    .setParameter("assignmentId", assignmentId)
                    .getResultList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Submissions> getSubmissionsByClassId(String classId) {
        try {
            return em.createQuery(
                            "SELECT s FROM Submissions s " +
                                    "JOIN s.assignmentSubmitSlot slot " +
                                    "WHERE slot.classEntity.classId = :classId " +
                                    "ORDER BY s.createdAt DESC", Submissions.class)
                    .setParameter("classId", classId)
                    .getResultList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public Submissions getSubmissionByStudentAndAssignment(String studentId, String assignmentId) {
        try {
            return em.createQuery(
                            "SELECT s FROM Submissions s " +
                                    "WHERE s.id.submittedBy = :studentId AND s.id.assignmentSubmitSlotId = :assignmentId",
                            Submissions.class)
                    .setParameter("studentId", studentId)
                    .setParameter("assignmentId", assignmentId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Submissions getSubmissionByStudentId(String studentId) {
        try {
            return em.createQuery(
                            "SELECT s FROM Submissions s " +
                                    "WHERE s.submittedBy.id = :studentId",
                            Submissions.class)
                    .setParameter("studentId", studentId)
                    .getResultList()
                    .stream()
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void save(Submissions submission) {
        try {
            em.persist(submission);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save submission: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean exists(String studentId, String assignmentId) {
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(s) FROM Submissions s " +
                                    "WHERE s.id.submittedBy = :studentId AND s.id.assignmentSubmitSlotId = :assignmentId",
                            Long.class)
                    .setParameter("studentId", studentId)
                    .setParameter("assignmentId", assignmentId)
                    .getSingleResult();
            return count > 0;
        } catch (Exception e) {
            return false;
        }
    }
    @Override
    public void submitAssignment(Students student, String postId, List<MultipartFile> files) {
        // 1. Kiểm tra slot
        AssignmentSubmitSlots slot = assignmentSubmitSlotsService.findByPostId(postId);
        if (slot == null) throw new IllegalArgumentException("Assignment not found");

        // 2. Kiểm tra deadline
        if (slot.getDeadline() != null && LocalDateTime.now().isAfter(slot.getDeadline())) {
            throw new IllegalStateException("Submission deadline has passed");
        }

        // 3. Kiểm tra đã nộp chưa
        if (exists(student.getId(), postId)) {
            throw new IllegalStateException("You have already submitted this assignment");
        }

        // 4. Tạo submission
        Submissions submission = new Submissions();
        submission.setId(new SubmissionsId(student.getId(), postId));
        submission.setSubmittedBy(student);
        submission.setAssignmentSubmitSlot(slot);
        submission.setCreatedAt(LocalDateTime.now());

        // 5. Xử lý từng file
        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            SubmissionDocuments doc = new SubmissionDocuments();
            doc.setSubmission(submission);
            doc.setCreator(student);
            doc.setFilePath(file.getOriginalFilename());

            try {
                doc.setFileData(file.getBytes());
            } catch (Exception e) {
                throw new RuntimeException("Failed to read file: " + file.getOriginalFilename());
            }

            // DÙNG SERVICE ĐỂ SINH ID VÀ VALIDATE
            String docId = submissionDocumentsService.generateUniqueDocumentId(student.getId(), postId);
            doc.setSubmissionDocumentId(docId);

            Map<String, String> errors = submissionDocumentsService.validateDocument(doc);
            if (!errors.isEmpty()) {
                throw new IllegalArgumentException("Invalid document: " + errors);
            }

            // GỌI SERVICE ĐỂ LƯU
            submissionDocumentsService.saveDocument(doc);

            // Thêm vào danh sách (nếu cần hiển thị)
            submission.getSubmissionDocuments().add(doc);
        }

        // 6. Lưu submission
        save(submission);
    }

    @Override
    public Submissions findById(SubmissionsId id) {
        return em.find(Submissions.class, id);
    }

    // Trong SubmissionsDAOImpl.java
    @Override
    public void deleteByStudentAndSlot(String studentId, String slotId) {
        Submissions submission = getSubmissionByStudentAndAssignment(studentId, slotId);
        if (submission != null) {
            // Xóa cascade: documents sẽ tự xóa nhờ @OnDelete(action = OnDeleteAction.CASCADE)
            em.remove(submission);
        }
    }
}