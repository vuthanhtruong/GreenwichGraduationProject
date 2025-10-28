package com.example.demo.studentRequiredSubjects.studentRequiredMinorSubjects.dao;

import com.example.demo.subject.minorSubject.model.MinorSubjects;
import com.example.demo.studentRequiredSubjects.studentRequiredMinorSubjects.model.StudentRequiredMinorSubjects;
import com.example.demo.user.deputyStaff.service.DeputyStaffsService;
import com.example.demo.user.student.model.Students;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
@Transactional
public class StudentRequiredMinorSubjectsDAOImpl implements StudentRequiredMinorSubjectsDAO {

    @PersistenceContext
    private EntityManager entityManager;

    private final DeputyStaffsService deputyStaffsService;

    public StudentRequiredMinorSubjectsDAOImpl(DeputyStaffsService deputyStaffsService) {
        if (deputyStaffsService == null) {
            throw new IllegalArgumentException("DeputyStaffsService cannot be null");
        }
        this.deputyStaffsService = deputyStaffsService;
    }

    @Override
    public List<StudentRequiredMinorSubjects> getStudentRequiredMinorSubjects(MinorSubjects subject) {
        if (subject == null) return Collections.emptyList();

        String campus = deputyStaffsService.getCampus().getCampusId();

        return entityManager.createQuery(
                        "SELECT srm FROM StudentRequiredMinorSubjects srm " +
                                "WHERE srm.subject = :subject AND srm.student.campus = :campus",
                        StudentRequiredMinorSubjects.class)
                .setParameter("subject", subject)
                .setParameter("campus", campus)
                .getResultList();
    }

    @Override
    public List<Students> getStudentsNotRequiredMinorSubject(MinorSubjects subject) {
        if (subject == null) return Collections.emptyList();

        String campus = deputyStaffsService.getCampus().getCampusId();

        return entityManager.createQuery(
                        "SELECT s FROM Students s " +
                                "WHERE s.campus = :campus " +
                                "AND s.id NOT IN (" +
                                "    SELECT srm.student.id FROM StudentRequiredMinorSubjects srm " +
                                "    WHERE srm.subject = :subject" +
                                ")",
                        Students.class)
                .setParameter("campus", campus)
                .setParameter("subject", subject)
                .getResultList();
    }

    @Override
    public boolean isStudentAlreadyRequiredForSubject(String studentId, String subjectId) {
        if (studentId == null || subjectId == null) return false;

        Long count = entityManager.createQuery(
                        "SELECT COUNT(srm) FROM StudentRequiredMinorSubjects srm " +
                                "WHERE srm.student.id = :studentId AND srm.subject.subjectId = :subjectId",
                        Long.class)
                .setParameter("studentId", studentId)
                .setParameter("subjectId", subjectId)
                .getSingleResult();

        return count > 0;
    }

    @Override
    public void addStudentRequiredMinorSubject(StudentRequiredMinorSubjects srm) {
        if (srm == null) {
            throw new IllegalArgumentException("StudentRequiredMinorSubjects cannot be null");
        }
        entityManager.persist(srm);
    }

    @Override
    public boolean removeStudentRequiredMinorSubject(String studentId, String subjectId) {
        if (studentId == null || subjectId == null) return false;

        int deleted = entityManager.createQuery(
                        "DELETE FROM StudentRequiredMinorSubjects srm " +
                                "WHERE srm.student.id = :studentId AND srm.subject.subjectId = :subjectId")
                .setParameter("studentId", studentId)
                .setParameter("subjectId", subjectId)
                .executeUpdate();

        return deleted > 0;
    }
}