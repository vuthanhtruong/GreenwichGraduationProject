package com.example.demo.dao.impl;

import com.example.demo.dao.Students_ClassesDAO;
import com.example.demo.entity.*;
import com.example.demo.service.StaffsService;
import com.example.demo.service.StudentsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    private boolean isValidClassAndMajor(Classes classes, Majors major) {
        return classes != null && classes.getSubject() != null && major != null;
    }

    @Override
    public void addStudentsToClass(Classes classes, List<String> studentIds) {
        if (classes == null || studentIds == null) {
            throw new IllegalArgumentException("Classes or studentIds cannot be null");
        }
        for (String studentId : studentIds) {
            if (studentId == null || studentId.trim().isEmpty()) {
                continue;
            }
            Students student = studentsService.getStudentById(studentId);
            if (student == null) {
                continue;
            }
            Students_Classes studentClass = new Students_Classes();
            StudentsClassesId id = new StudentsClassesId(studentId, classes.getClassId());
            studentClass.setId(id);
            studentClass.setClassEntity(classes);
            studentClass.setStudent(student);
            studentClass.setCreatedAt(LocalDateTime.now());
            studentClass.setAddedBy(staffsService.getStaff());
            entityManager.persist(studentClass);
        }
    }

    @Override
    public List<Students_Classes> listStudentsInClass(Classes classes) {
        if (classes == null || staffsService.getStaffMajor() == null) {
            return List.of();
        }
        Majors major = staffsService.getStaffMajor();
        return entityManager.createQuery(
                        "SELECT sc FROM Students_Classes sc WHERE sc.classEntity = :class AND sc.student.major = :major",
                        Students_Classes.class)
                .setParameter("class", classes)
                .setParameter("major", major)
                .getResultList();
    }

    @Override
    public List<Students> listStudentsNotInClass(Classes classes) {
        if (classes == null || staffsService.getStaffMajor() == null) {
            return List.of();
        }
        Majors major = staffsService.getStaffMajor();
        return entityManager.createQuery(
                        "SELECT s FROM Students s LEFT JOIN Students_Classes sc ON s.id = sc.student.id AND sc.classEntity = :class " +
                                "WHERE s.major = :major AND sc.student.id IS NULL",
                        Students.class)
                .setParameter("class", classes)
                .setParameter("major", major)
                .getResultList();
    }

    @Override
    public List<Students> listStudentsFailedSubjectAndNotPaid(Classes classes) {
        if (!isValidClassAndMajor(classes, staffsService.getStaffMajor())) {
            return List.of();
        }
        String subjectId = classes.getSubject().getSubjectId();
        Majors major = staffsService.getStaffMajor();
        return entityManager.createQuery(
                        "SELECT s FROM Students s JOIN AcademicTranscripts at ON s.id = at.student.id " +
                                "LEFT JOIN MajorPaymentHistory ph ON s.id = ph.student.id AND ph.subject.subjectId = :subjectId AND ph.status = 'COMPLETED' " +
                                "LEFT JOIN Students_Classes sc ON s.id = sc.student.id AND sc.classEntity.subject.subjectId = :subjectId " +
                                "WHERE s.major = :major AND at.subject.subjectId = :subjectId AND at.grade = :failedGrade " +
                                "AND ph.student.id IS NULL AND sc.student.id IS NULL",
                        Students.class)
                .setParameter("subjectId", subjectId)
                .setParameter("major", major)
                .setParameter("failedGrade", Grades.REFER)
                .getResultList();
    }

    @Override
    public List<Students> listStudentsFailedSubjectAndPaid(Classes classes) {
        if (!isValidClassAndMajor(classes, staffsService.getStaffMajor())) {
            return List.of();
        }
        String subjectId = classes.getSubject().getSubjectId();
        Majors major = staffsService.getStaffMajor();
        return entityManager.createQuery(
                        "SELECT s FROM Students s JOIN AcademicTranscripts at ON s.id = at.student.id " +
                                "JOIN MajorPaymentHistory ph ON s.id = ph.student.id AND ph.subject.subjectId = :subjectId AND ph.status = 'COMPLETED' " +
                                "LEFT JOIN Students_Classes sc ON s.id = sc.student.id AND sc.classEntity.subject.subjectId = :subjectId " +
                                "WHERE s.major = :major AND at.subject.subjectId = :subjectId AND at.grade = :failedGrade " +
                                "AND sc.student.id IS NULL",
                        Students.class)
                .setParameter("subjectId", subjectId)
                .setParameter("major", major)
                .setParameter("failedGrade", Grades.REFER)
                .getResultList();
    }

    @Override
    public List<Students> listStudentsNotTakenSubject(Classes classes, boolean hasPaid) {
        if (!isValidClassAndMajor(classes, staffsService.getStaffMajor())) {
            return List.of();
        }
        String subjectId = classes.getSubject().getSubjectId();
        Majors major = staffsService.getStaffMajor();
        String paymentCondition = hasPaid ?
                "AND EXISTS (SELECT ph FROM MajorPaymentHistory ph WHERE ph.student.id = s.id AND ph.subject.subjectId = :subjectId AND ph.status = 'COMPLETED')" :
                "AND NOT EXISTS (SELECT ph FROM MajorPaymentHistory ph WHERE ph.student.id = s.id AND ph.subject.subjectId = :subjectId AND ph.status = 'COMPLETED')";
        return entityManager.createQuery(
                        "SELECT s FROM Students s LEFT JOIN AcademicTranscripts at ON s.id = at.student.id AND at.subject.subjectId = :subjectId " +
                                "LEFT JOIN Students_Classes sc ON s.id = sc.student.id AND sc.classEntity.subject.subjectId = :subjectId " +
                                "WHERE s.major = :major AND at.student.id IS NULL AND sc.student.id IS NULL " + paymentCondition,
                        Students.class)
                .setParameter("subjectId", subjectId)
                .setParameter("major", major)
                .getResultList();
    }

    @Override
    public List<Students> listStudentsCurrentlyTakingSubject(Classes classes) {
        if (classes == null) {
            return List.of();
        }
        LocalDate currentDate = LocalDate.now();
        return entityManager.createQuery(
                        "SELECT s FROM Students s JOIN Students_Classes sc ON s.id = sc.student.id " +
                                "WHERE sc.classEntity = :class AND EXISTS (SELECT t FROM MajorTimetable t WHERE t.classEntity = :class " +
                                "AND (t.date >= :currentDate OR t.dayOfTheWeek IS NOT NULL))",
                        Students.class)
                .setParameter("class", classes)
                .setParameter("currentDate", currentDate)
                .getResultList();
    }

    @Cacheable(value = "totalTuition", key = "#currentSemester + '-' + #major.id")
    public Double calculateTotalTuition(Integer currentSemester, Majors major) {
        return entityManager.createQuery(
                        "SELECT COALESCE(SUM(s.tuition), 0) FROM Subjects s WHERE s.semester = :currentSemester AND s.major = :major",
                        Double.class)
                .setParameter("currentSemester", currentSemester)
                .setParameter("major", major)
                .getSingleResult();
    }

    @Override
    public List<Students> listStudentsCompletedPreviousSemesterWithSufficientBalance(Classes classes) {
        if (classes == null || classes.getSubject() == null || classes.getSubject().getSemester() == null || staffsService.getStaffMajor() == null) {
            return List.of();
        }
        Integer prevSemester = getPreviousSemester(classes.getSubject().getSemester());
        if (prevSemester == null) {
            return List.of();
        }
        String subjectId = classes.getSubject().getSubjectId();
        Majors major = staffsService.getStaffMajor();
        Double totalTuition = calculateTotalTuition(classes.getSubject().getSemester(), major);

        return entityManager.createQuery(
                        "SELECT s FROM Students s JOIN Students_Classes sc ON s.id = sc.student.id " +
                                "JOIN AccountBalances ab ON s.id = ab.student.id " +
                                "WHERE s.major = :major AND sc.classEntity.subject.semester = :prevSemester " +
                                "AND s.id NOT IN (SELECT sc2.student.id FROM Students_Classes sc2 WHERE sc2.classEntity.subject.subjectId = :subjectId) " +
                                "AND ab.balance >= :totalTuition " +
                                "GROUP BY s.id HAVING COUNT(DISTINCT sc.classEntity.subject.subjectId) = " +
                                "(SELECT COUNT(DISTINCT sub.subjectId) FROM Subjects sub WHERE sub.semester = :prevSemester)",
                        Students.class)
                .setParameter("prevSemester", prevSemester)
                .setParameter("subjectId", subjectId)
                .setParameter("major", major)
                .setParameter("totalTuition", totalTuition)
                .getResultList();
    }

    @Override
    public List<Students> listStudentsCompletedPreviousSemesterWithInsufficientBalance(Classes classes) {
        if (classes == null || classes.getSubject() == null || classes.getSubject().getSemester() == null || staffsService.getStaffMajor() == null) {
            return List.of();
        }
        Integer prevSemester = getPreviousSemester(classes.getSubject().getSemester());
        if (prevSemester == null) {
            return List.of();
        }
        String subjectId = classes.getSubject().getSubjectId();
        Majors major = staffsService.getStaffMajor();
        Double totalTuition = calculateTotalTuition(classes.getSubject().getSemester(), major);

        return entityManager.createQuery(
                        "SELECT s FROM Students s JOIN Students_Classes sc ON s.id = sc.student.id " +
                                "LEFT JOIN AccountBalances ab ON s.id = ab.student.id " +
                                "WHERE s.major = :major AND sc.classEntity.subject.semester = :prevSemester " +
                                "AND s.id NOT IN (SELECT sc2.student.id FROM Students_Classes sc2 WHERE sc2.classEntity.subject.subjectId = :subjectId) " +
                                "AND (ab.balance IS NULL OR ab.balance < :totalTuition) " +
                                "GROUP BY s.id HAVING COUNT(DISTINCT sc.classEntity.subject.subjectId) = " +
                                "(SELECT COUNT(DISTINCT sub.subjectId) FROM Subjects sub WHERE sub.semester = :prevSemester)",
                        Students.class)
                .setParameter("prevSemester", prevSemester)
                .setParameter("subjectId", subjectId)
                .setParameter("major", major)
                .setParameter("totalTuition", totalTuition)
                .getResultList();
    }

    private Integer getPreviousSemester(Integer currentSemester) {
        if (currentSemester == null) {
            return null;
        }
        return currentSemester > 1 ? currentSemester - 1 : null;
    }
}