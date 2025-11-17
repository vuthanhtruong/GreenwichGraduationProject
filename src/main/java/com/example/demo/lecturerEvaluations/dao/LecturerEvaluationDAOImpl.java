// src/main/java/com/example/demo/lecturerEvaluations/dao/LecturerEvaluationDAOImpl.java
package com.example.demo.lecturerEvaluations.dao;

import com.example.demo.classes.abstractClasses.model.Classes;
import com.example.demo.lecturerEvaluations.model.LecturerEvaluations;
import com.example.demo.lecturerEvaluations.model.MajorLecturerEvaluations;
import com.example.demo.lecturerEvaluations.model.MinorLecturerEvaluations;
import com.example.demo.user.majorLecturer.model.MajorLecturers;
import com.example.demo.user.minorLecturer.model.MinorLecturers;
import com.example.demo.user.student.model.Students;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
@Transactional
public class LecturerEvaluationDAOImpl implements LecturerEvaluationDAO {
    // LecturerEvaluationServiceImpl.java
    @Override
    public List<MinorLecturerEvaluations> findAllMinorLecturerEvaluationsByCampus(String campus) {
        return em.createQuery("SELECT e FROM MinorLecturerEvaluations e Where e.reviewer.campus.campusId=:campus ORDER BY e.createdAt DESC", MinorLecturerEvaluations.class)
                .setParameter("campus",campus).getResultList();
    }

    // LecturerEvaluationServiceImpl.java
    @Override
    public List<MajorLecturerEvaluations> findAllMajorLecturerEvaluationsByCampus(String campus) {
        return em.createQuery("""
            SELECT e FROM MajorLecturerEvaluations e where e.reviewer.campus.campusId=:campus
            ORDER BY e.createdAt DESC
            """, MajorLecturerEvaluations.class).setParameter("campus",campus).getResultList();
    }

    @PersistenceContext
    private EntityManager em;

    private static final SecureRandom random = new SecureRandom();
    private static final String EVAL_PREFIX = "EVAL-";
    private static final int ID_LENGTH = 8; // EVAL-XXXXXXXX → 8 ký tự

    private String generateUniqueEvaluationId() {
        String id;
        int attempts = 0;
        final int maxAttempts = 10; // cực kỳ hiếm khi cần retry quá 5 lần

        do {
            if (attempts++ >= maxAttempts) {
                // Fallback: dùng UUID đầy đủ nếu vẫn trùng (gần như không bao giờ xảy ra)
                return EVAL_PREFIX + UUID.randomUUID().toString().substring(0, 20).toUpperCase();
            }

            // Tạo 8 ký tự hex ngẫu nhiên
            StringBuilder sb = new StringBuilder(ID_LENGTH);
            for (int i = 0; i < ID_LENGTH; i++) {
                sb.append(String.format("%X", random.nextInt(16)));
            }
            id = EVAL_PREFIX + sb.toString();

        } while (isEvaluationIdExists(id)); // kiểm tra trùng

        return id;
    }

    private boolean isEvaluationIdExists(String evaluationId) {
        String jpql = "SELECT COUNT(e) > 0 FROM LecturerEvaluations e WHERE e.evaluationId = :id";
        return em.createQuery(jpql, Boolean.class)
                .setParameter("id", evaluationId)
                .getSingleResult();
    }

    @Override
    public MajorLecturerEvaluations addMajorLecturerEvaluation(
            Students reviewer,
            Classes classEntity,
            MajorLecturers lecturer,
            String text) {

        MajorLecturerEvaluations eval = new MajorLecturerEvaluations();
        eval.setEvaluationId(generateUniqueEvaluationId()); // ← ĐẢM BẢO KHÔNG TRÙNG
        eval.setReviewer(reviewer);
        eval.setClassEntity(classEntity);
        eval.setLecturer(lecturer);
        eval.setText(text != null ? text.trim() : null);
        eval.setCreatedAt(LocalDateTime.now());

        em.persist(eval);
        return eval;
    }

    @Override
    public MinorLecturerEvaluations addMinorLecturerEvaluation(
            Students reviewer,
            Classes classEntity,
            MinorLecturers lecturer,
            String text) {

        MinorLecturerEvaluations eval = new MinorLecturerEvaluations();
        eval.setEvaluationId(generateUniqueEvaluationId()); // ← ĐẢM BẢO KHÔNG TRÙNG
        eval.setReviewer(reviewer);
        eval.setClassEntity(classEntity);
        eval.setLecturer(lecturer);
        eval.setText(text != null ? text.trim() : null);
        eval.setCreatedAt(LocalDateTime.now());

        em.persist(eval);
        return eval;
    }

    @Override
    public <T extends LecturerEvaluations> T save(T evaluation) {
        if (evaluation.getEvaluationId() == null) {
            em.persist(evaluation);
            return evaluation;
        } else {
            return em.merge(evaluation);
        }
    }

    @Override
    public LecturerEvaluations findById(String evaluationId) {
        return em.find(LecturerEvaluations.class, evaluationId);
    }

    @Override
    public List<LecturerEvaluations> findAll() {
        return em.createQuery(
                        "SELECT e FROM LecturerEvaluations e " +
                                "LEFT JOIN FETCH e.reviewer " +
                                "LEFT JOIN FETCH e.classEntity " +
                                "ORDER BY e.createdAt DESC", LecturerEvaluations.class)
                .getResultList();
    }

    @Override
    public List<LecturerEvaluations> findByStudentId(String studentId) {
        return em.createQuery(
                        "SELECT e FROM LecturerEvaluations e " +
                                "WHERE e.reviewer.id = :studentId " +
                                "ORDER BY e.createdAt DESC", LecturerEvaluations.class)
                .setParameter("studentId", studentId)
                .getResultList();
    }

    @Override
    public List<LecturerEvaluations> findByClassIdByStudentId(String classId, String studentId) {
        return em.createQuery(
                        "SELECT e FROM LecturerEvaluations e " +
                                "WHERE e.classEntity.classId = :classId And e.reviewer.id = :studentId " +
                                "ORDER BY e.createdAt DESC", LecturerEvaluations.class)
                .setParameter("classId", classId)
                .setParameter("studentId", studentId)
                .getResultList();
    }

    @Override
    public List<MajorLecturerEvaluations> findMajorByLecturerId(String lecturerId) {
        return em.createQuery(
                        "SELECT e FROM MajorLecturerEvaluations e " +
                                "WHERE e.lecturer.id = :lecturerId " +
                                "ORDER BY e.createdAt DESC", MajorLecturerEvaluations.class)
                .setParameter("lecturerId", lecturerId)
                .getResultList();
    }

    @Override
    public List<MinorLecturerEvaluations> findMinorByLecturerId(String lecturerId) {
        return em.createQuery(
                        "SELECT e FROM MinorLecturerEvaluations e " +
                                "WHERE e.lecturer.id = :lecturerId " +
                                "ORDER BY e.createdAt DESC", MinorLecturerEvaluations.class)
                .setParameter("lecturerId", lecturerId)
                .getResultList();
    }

    @Override
    public List<LecturerEvaluations> findAllByLecturerId(String lecturerId) {
        List<MajorLecturerEvaluations> major = findMajorByLecturerId(lecturerId);
        List<MinorLecturerEvaluations> minor = findMinorByLecturerId(lecturerId);

        return java.util.stream.Stream.concat(major.stream(), minor.stream())
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .toList();
    }

    @Override
    public long countByLecturerId(String lecturerId) {
        Long majorCount = em.createQuery(
                        "SELECT COUNT(e) FROM MajorLecturerEvaluations e WHERE e.lecturer.id = :id", Long.class)
                .setParameter("id", lecturerId)
                .getSingleResult();

        Long minorCount = em.createQuery(
                        "SELECT COUNT(e) FROM MinorLecturerEvaluations e WHERE e.lecturer.id = :id", Long.class)
                .setParameter("id", lecturerId)
                .getSingleResult();

        return majorCount + minorCount;
    }

    @Override
    public long countByStudentId(String studentId) {
        return em.createQuery(
                        "SELECT COUNT(e) FROM LecturerEvaluations e WHERE e.reviewer.id = :id", Long.class)
                .setParameter("id", studentId)
                .getSingleResult();
    }

    @Override
    public void deleteById(String evaluationId) {
        LecturerEvaluations e = em.getReference(LecturerEvaluations.class, evaluationId);
        em.remove(e);
    }
}