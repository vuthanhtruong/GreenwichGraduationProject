package com.example.demo.student_class.dao;

import com.example.demo.classes.model.MajorClasses;
import com.example.demo.entity.*;
import com.example.demo.entity.Enums.Grades;
import com.example.demo.entity.Enums.Status;
import com.example.demo.major.model.Majors;
import com.example.demo.majorstaff.model.Staffs;
import com.example.demo.majorstaff.service.StaffsService;
import com.example.demo.student.service.StudentsService;
import com.example.demo.student.model.Students;
import com.example.demo.subject.model.MajorSubjects;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Repository
@Transactional
public class Students_ClassesDAOImpl implements Students_ClassesDAO {

    @PersistenceContext
    private EntityManager entityManager;

    private final StaffsService staffsService;
    private final StudentsService studentsService;

    public Students_ClassesDAOImpl(StaffsService staffsService, StudentsService studentsService) {
        if (staffsService == null || studentsService == null) {
            throw new IllegalArgumentException("Services cannot be null");
        }
        this.staffsService = staffsService;
        this.studentsService = studentsService;
    }

    private boolean isValidClassAndMajor(MajorClasses classes, Majors major) {
        return classes != null && major != null && classes.getClassId() != null;
    }

    @Override
    public void addStudentsToClass(MajorClasses classes, List<String> studentIds) {
        if (classes == null || studentIds == null || studentIds.isEmpty()) {
            throw new IllegalArgumentException("Classes or studentIds cannot be null or empty");
        }
        Majors major = staffsService.getStaffMajor();
        if (!isValidClassAndMajor(classes, major)) {
            throw new IllegalArgumentException("Invalid class or major");
        }

        for (String studentId : studentIds) {
            if (studentId == null || studentId.trim().isEmpty()) continue;

            Students student = studentsService.getStudentById(studentId);
            if (student == null || !major.equals(student.getMajor())) continue;

            Students_MajorClasses sc = new Students_MajorClasses();
            sc.setId(new StudentsClassesId(studentId, classes.getClassId()));
            sc.setClassEntity(classes);
            sc.setStudent(student);
            sc.setCreatedAt(LocalDateTime.now());
            Staffs addedBy = staffsService.getStaff();
            if (addedBy == null) throw new IllegalStateException("Staff not found for adding student to class");
            sc.setAddedBy(addedBy);
            entityManager.persist(sc);
        }
    }

    @Override
    public List<Students_MajorClasses> listStudentsInClass(MajorClasses classes) {
        if (classes == null || staffsService.getStaffMajor() == null) return Collections.emptyList();
        Majors major = staffsService.getStaffMajor();

        return entityManager.createQuery(
                        "SELECT sc FROM Students_MajorClasses sc " +
                                "WHERE sc.classEntity.classId = :classId AND sc.student.major = :major",
                        Students_MajorClasses.class)
                .setParameter("classId", classes.getClassId())
                .setParameter("major", major)
                .getResultList();
    }

    @Override
    public List<Students> listStudentsNotInClass(MajorClasses classes) {
        if (classes == null || staffsService.getStaffMajor() == null) return Collections.emptyList();
        Majors major = staffsService.getStaffMajor();

        return entityManager.createQuery(
                        "SELECT s FROM Students s " +
                                "WHERE s.major = :major " +
                                "AND NOT EXISTS (SELECT 1 FROM Students_MajorClasses sc " +
                                "                 WHERE sc.student.id = s.id AND sc.classEntity.classId = :classId)",
                        Students.class)
                .setParameter("major", major)
                .setParameter("classId", classes.getClassId())
                .getResultList();
    }

    @Override
    public List<Students> listStudentsFailedSubjectAndNotPaid(MajorClasses classes) {
        if (classes == null || !isValidClassAndMajor(classes, staffsService.getStaffMajor())) return Collections.emptyList();
        String subjectId = getSubjectIdFromClass(classes);
        if (subjectId == null) return Collections.emptyList();
        Majors major = staffsService.getStaffMajor();

        return entityManager.createQuery(
                        "SELECT s FROM Students s " +
                                "WHERE s.major = :major " +
                                "AND EXISTS (SELECT 1 FROM MajorAcademicTranscripts at " +
                                "            WHERE at.student.id = s.id " +
                                "              AND at.subject.subjectId = :subjectId " +
                                "              AND at.grade = :failed) " +
                                "AND NOT EXISTS (SELECT 1 FROM PaymentHistories ph " +
                                "                 WHERE ph.student.id = s.id " +
                                "                   AND ph.subject.subjectId = :subjectId " +
                                "                   AND ph.status = :completed) " +
                                "AND NOT EXISTS (SELECT 1 FROM Students_MajorClasses sc " +
                                "                 WHERE sc.student.id = s.id " +
                                "                   AND sc.classEntity.classId = :classId)",
                        Students.class)
                .setParameter("major", major)
                .setParameter("subjectId", subjectId)
                .setParameter("classId", classes.getClassId())
                .setParameter("failed", Grades.REFER)
                .setParameter("completed", Status.COMPLETED)
                .getResultList();
    }

    @Override
    public List<Students> listStudentsFailedSubjectAndPaid(MajorClasses classes) {
        if (classes == null || !isValidClassAndMajor(classes, staffsService.getStaffMajor())) return Collections.emptyList();
        String subjectId = getSubjectIdFromClass(classes);
        if (subjectId == null) return Collections.emptyList();
        Majors major = staffsService.getStaffMajor();

        return entityManager.createQuery(
                        "SELECT s FROM Students s " +
                                "WHERE s.major = :major " +
                                "AND EXISTS (SELECT 1 FROM MajorAcademicTranscripts at " +
                                "            WHERE at.student.id = s.id " +
                                "              AND at.subject.subjectId = :subjectId " +
                                "              AND at.grade = :failed) " +
                                "AND EXISTS (SELECT 1 FROM PaymentHistories ph " +
                                "            WHERE ph.student.id = s.id " +
                                "              AND ph.subject.subjectId = :subjectId " +
                                "              AND ph.status = :completed) " +
                                "AND NOT EXISTS (SELECT 1 FROM Students_MajorClasses sc " +
                                "                WHERE sc.student.id = s.id " +
                                "                  AND sc.classEntity.classId = :classId)",
                        Students.class)
                .setParameter("major", major)
                .setParameter("subjectId", subjectId)
                .setParameter("classId", classes.getClassId())
                .setParameter("failed", Grades.REFER)
                .setParameter("completed", Status.COMPLETED)
                .getResultList();
    }

    @Override
    public List<Students> listStudentsNotTakenSubject(MajorClasses classes, boolean hasPaid) {
        if (classes == null || !isValidClassAndMajor(classes, staffsService.getStaffMajor())) return Collections.emptyList();
        String subjectId = getSubjectIdFromClass(classes);
        if (subjectId == null) return Collections.emptyList();
        Majors major = staffsService.getStaffMajor();

        String paymentClause = hasPaid
                ? "AND EXISTS (SELECT 1 FROM PaymentHistories ph WHERE ph.student.id = s.id AND ph.subject.subjectId = :subjectId AND ph.status = :completed) "
                : "AND NOT EXISTS (SELECT 1 FROM PaymentHistories ph WHERE ph.student.id = s.id AND ph.subject.subjectId = :subjectId AND ph.status = :completed) ";

        String q = "SELECT s FROM Students s " +
                "WHERE s.major = :major " +
                "AND NOT EXISTS (SELECT 1 FROM MajorAcademicTranscripts at WHERE at.student.id = s.id AND at.subject.subjectId = :subjectId) " +
                "AND NOT EXISTS (SELECT 1 FROM Students_MajorClasses sc WHERE sc.student.id = s.id AND sc.classEntity.classId = :classId) " +
                paymentClause;

        return entityManager.createQuery(q, Students.class)
                .setParameter("major", major)
                .setParameter("subjectId", subjectId)
                .setParameter("classId", classes.getClassId())
                .setParameter("completed", Status.COMPLETED)
                .getResultList();
    }

    @Override
    public List<Students> listStudentsCurrentlyTakingSubject(MajorClasses classes) {
        if (classes == null) return Collections.emptyList();
        LocalDate today = LocalDate.now();

        return entityManager.createQuery(
                        "SELECT DISTINCT s FROM Students s " +
                                "JOIN Students_MajorClasses sc ON sc.student.id = s.id " +
                                "WHERE sc.classEntity.classId = :classId " +
                                "AND EXISTS (SELECT 1 FROM MajorTimetable t " +
                                "            WHERE t.classEntity.classId = :classId " +
                                "              AND (t.date >= :today OR t.dayOfTheWeek IS NOT NULL))",
                        Students.class)
                .setParameter("classId", classes.getClassId())
                .setParameter("today", today)
                .getResultList();
    }

    @Override
    public List<Students> listStudentsCompletedPreviousSemesterWithSufficientBalance(MajorClasses classes) {
        if (classes == null || classes.getClassId() == null || staffsService.getStaffMajor() == null) return Collections.emptyList();
        String subjectId = getSubjectIdFromClass(classes);
        if (subjectId == null) return Collections.emptyList();
        Integer semester = getSemesterFromSubject(subjectId);
        if (semester == null || semester <= 1) return Collections.emptyList();
        int prev = semester - 1;
        Majors major = staffsService.getStaffMajor();

        return entityManager.createQuery(
                        "SELECT s FROM Students s " +
                                "JOIN AccountBalances ab ON ab.student.id = s.id " +
                                "WHERE s.major = :major " +
                                "AND NOT EXISTS (SELECT 1 FROM Students_MajorClasses sc2 " +
                                "                WHERE sc2.student.id = s.id AND sc2.classEntity.classId = :classId) " +
                                "AND (SELECT COUNT(DISTINCT sub.subjectId) FROM MajorSubjects sub " +
                                "     WHERE sub.major = :major AND sub.semester = :prev) " +
                                "    = " +
                                "    (SELECT COUNT(DISTINCT at.subject.subjectId) FROM MajorAcademicTranscripts at " +
                                "     WHERE at.student.id = s.id " +
                                "       AND at.subject.major = :major " +
                                "       AND at.subject.semester = :prev " +
                                "       AND at.grade <> :failed) " +
                                "AND ab.balance >= ( " +
                                "     SELECT COALESCE(SUM(ty.tuition), 0) " +
                                "     FROM TuitionByYear ty " +
                                "     WHERE ty.subject.subjectId IN (SELECT ms.subjectId FROM MajorSubjects ms WHERE ms.major = :major AND ms.semester = :prev) " +
                                "       AND ty.id.admissionYear = FUNCTION('YEAR', s.admissionYear))",
                        Students.class)
                .setParameter("major", major)
                .setParameter("prev", prev)
                .setParameter("classId", classes.getClassId())
                .setParameter("failed", Grades.REFER)
                .getResultList();
    }

    @Override
    public List<Students> listStudentsCompletedPreviousSemesterWithInsufficientBalance(MajorClasses classes) {
        if (classes == null || classes.getClassId() == null || staffsService.getStaffMajor() == null) return Collections.emptyList();
        String subjectId = getSubjectIdFromClass(classes);
        if (subjectId == null) return Collections.emptyList();
        Integer semester = getSemesterFromSubject(subjectId);
        if (semester == null || semester <= 1) return Collections.emptyList();
        int prev = semester - 1;
        Majors major = staffsService.getStaffMajor();

        return entityManager.createQuery(
                        "SELECT s FROM Students s " +
                                "LEFT JOIN AccountBalances ab ON ab.student.id = s.id " +
                                "WHERE s.major = :major " +
                                "AND NOT EXISTS (SELECT 1 FROM Students_MajorClasses sc2 " +
                                "                WHERE sc2.student.id = s.id AND sc2.classEntity.classId = :classId) " +
                                "AND (SELECT COUNT(DISTINCT sub.subjectId) FROM MajorSubjects sub " +
                                "     WHERE sub.major = :major AND sub.semester = :prev) " +
                                "    = " +
                                "    (SELECT COUNT(DISTINCT at.subject.subjectId) FROM MajorAcademicTranscripts at " +
                                "     WHERE at.student.id = s.id " +
                                "       AND at.subject.major = :major " +
                                "       AND at.subject.semester = :prev " +
                                "       AND at.grade <> :failed) " +
                                "AND COALESCE(ab.balance, 0) < ( " +
                                "     SELECT COALESCE(SUM(ty.tuition), 0) " +
                                "     FROM TuitionByYear ty " +
                                "     WHERE ty.subject.subjectId IN (SELECT ms.subjectId FROM MajorSubjects ms WHERE ms.major = :major AND ms.semester = :prev) " +
                                "       AND ty.id.admissionYear = FUNCTION('YEAR', s.admissionYear))",
                        Students.class)
                .setParameter("major", major)
                .setParameter("prev", prev)
                .setParameter("classId", classes.getClassId())
                .setParameter("failed", Grades.REFER)
                .getResultList();
    }

    private String getSubjectIdFromClass(MajorClasses classes) {
        return classes != null && classes.getSubject() != null ? classes.getSubject().getSubjectId() : null;
    }

    private Integer getSemesterFromSubject(String subjectId) {
        if (subjectId == null) return null;
        MajorSubjects subject = entityManager.createQuery(
                        "SELECT s FROM MajorSubjects s WHERE s.subjectId = :subjectId", MajorSubjects.class)
                .setParameter("subjectId", subjectId)
                .getSingleResult();
        return subject != null ? subject.getSemester() : null;
    }
}