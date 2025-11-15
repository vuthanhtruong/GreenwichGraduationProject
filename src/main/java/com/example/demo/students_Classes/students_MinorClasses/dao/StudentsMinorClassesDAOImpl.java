package com.example.demo.students_Classes.students_MinorClasses.dao;

import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.students_Classes.students_MinorClasses.model.Students_MinorClasses;
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
public class StudentsMinorClassesDAOImpl implements StudentsMinorClassesDAO {

    @Override
    public List<String> getClassNotificationsForStudent(String studentId) {
        String jpql = """
        SELECT CONCAT('You have been added to minor class: ', c.nameClass, ' (', c.minorSubject.subjectName, ')')
        FROM Students_MinorClasses smc
        JOIN smc.minorClass c
        WHERE smc.student.id = :studentId
          AND smc.notificationType = 'NOTIFICATION_003'
        ORDER BY smc.createdAt DESC
        """;

        return entityManager.createQuery(jpql, String.class)
                .setParameter("studentId", studentId)
                .getResultList();  // LẤY TẤT CẢ – KHÔNG GIỚI HẠN
    }

    @PersistenceContext
    private EntityManager entityManager;

    private final DeputyStaffsService deputyStaffsService;

    public StudentsMinorClassesDAOImpl(DeputyStaffsService deputyStaffsService) {
        this.deputyStaffsService = deputyStaffsService;
    }

    @Override
    public List<Students_MinorClasses> getStudentsInClass(String classId) {
        if (classId == null) return Collections.emptyList();
        return entityManager.createQuery(
                        "SELECT smc FROM Students_MinorClasses smc WHERE smc.id.classId = :classId",
                        Students_MinorClasses.class)
                .setParameter("classId", classId)
                .getResultList();
    }

    @Override
    public List<Students> getStudentsByClass(MinorClasses minorClass) {
        if (minorClass == null) return Collections.emptyList();
        return entityManager.createQuery(
                        "SELECT smc.student FROM Students_MinorClasses smc WHERE smc.classEntity = :minorClass",
                        Students.class)
                .setParameter("minorClass", minorClass)
                .getResultList();
    }

    @Override
    public List<Students> getStudentsNotInClassAndSubject(String classId, String subjectId) {
        if (classId == null || subjectId == null) return Collections.emptyList();

        String campus = deputyStaffsService.getCampus().getCampusId();

        try {
            return entityManager.createQuery(
                            "SELECT s FROM Students s " +
                                    "WHERE s.campus = :campus " +
                                    "AND s.id IN (" +
                                    "    SELECT srs.student.id FROM StudentRequiredMinorSubjects srs " +
                                    "    WHERE srs.subject.subjectId = :subjectId" +
                                    ") " +
                                    "AND s.id NOT IN (" +
                                    "    SELECT smc.id.studentId FROM Students_MinorClasses smc " +
                                    "    WHERE smc.id.classId = :classId" +
                                    ")",
                            Students.class)
                    .setParameter("campus", campus)
                    .setParameter("subjectId", subjectId)
                    .setParameter("classId", classId)
                    .getResultList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public void addStudentToClass(Students_MinorClasses studentsMinorClasses) {
        if (studentsMinorClasses == null) {
            throw new IllegalArgumentException("Students_MinorClasses cannot be null");
        }
        entityManager.persist(studentsMinorClasses);
    }

    @Override
    public void removeStudentFromClass(String studentId, String classId) {
        if (studentId == null || classId == null) return;

        Students_MinorClasses entity = entityManager.createQuery(
                        "SELECT smc FROM Students_MinorClasses smc " +
                                "WHERE smc.id.studentId = :studentId AND smc.id.classId = :classId",
                        Students_MinorClasses.class)
                .setParameter("studentId", studentId)
                .setParameter("classId", classId)
                .getResultStream()
                .findFirst()
                .orElse(null);

        if (entity != null) {
            entityManager.remove(entity);
        }
    }

    @Override
    public boolean existsByStudentAndClass(String studentId, String classId) {
        if (studentId == null || classId == null) return false;

        Long count = entityManager.createQuery(
                        "SELECT COUNT(smc) FROM Students_MinorClasses smc " +
                                "WHERE smc.id.studentId = :studentId AND smc.id.classId = :classId",
                        Long.class)
                .setParameter("studentId", studentId)
                .setParameter("classId", classId)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public List<Students_MinorClasses> getStudentsInClassByStudent(String studentId) {
        if (studentId == null) return Collections.emptyList();
        return entityManager.createQuery(
                        "SELECT smc FROM Students_MinorClasses smc WHERE smc.id.studentId = :studentId",
                        Students_MinorClasses.class)
                .setParameter("studentId", studentId)
                .getResultList();
    }
}