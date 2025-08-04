package com.example.demo.dao.impl;

import com.example.demo.dao.Students_ClassesDAO;
import com.example.demo.entity.*;
import com.example.demo.service.StaffsService;
import com.example.demo.service.StudentsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
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

    @Override
    public void addStudentsToClass(Classes classes, List<String> studentIds) {
        if (classes == null || studentIds == null) {
            throw new IllegalArgumentException("Classes or studentIds cannot be null");
        }
        for (String studentId : studentIds) {
            if (studentId == null || studentId.trim().isEmpty()) {
                continue; // Skip empty or null studentId
            }
            Students student = studentsService.getStudentById(studentId);
            if (student == null) {
                continue; // Skip if student not found
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
        if (classes == null) {
            return List.of();
        }
        Majors major = staffsService.getStaffMajor();
        if (major == null) {
            return List.of();
        }
        return entityManager.createQuery(
                        "SELECT sc FROM Students_Classes sc WHERE sc.classEntity = :class AND sc.student.major = :major",
                        Students_Classes.class)
                .setParameter("class", classes)
                .setParameter("major", major)
                .getResultList();
    }

    @Override
    public List<Students> listStudentsNotInClass(Classes classes) {
        if (classes == null) {
            return List.of();
        }
        Majors major = staffsService.getStaffMajor();
        if (major == null) {
            return List.of();
        }
        return entityManager.createQuery(
                        "SELECT s FROM Students s WHERE s.major = :major AND s.id NOT IN " +
                                "(SELECT sc.student.id FROM Students_Classes sc WHERE sc.classEntity = :class)",
                        Students.class)
                .setParameter("class", classes)
                .setParameter("major", major)
                .getResultList();
    }

    @Override
    public List<Students> listStudentsFailedSubjectAndNotPaid(Classes classes) {
        if (classes == null || classes.getSubject() == null) {
            return List.of();
        }
        String subjectId = classes.getSubject().getSubjectId();
        Majors major = staffsService.getStaffMajor();
        if (major == null) {
            return List.of();
        }
        return entityManager.createQuery(
                        "SELECT s FROM Students s WHERE s.major = :major AND s.id IN " +
                                "(SELECT at.student.id FROM AcademicTranscripts at WHERE at.subject.subjectId = :subjectId " +
                                "AND at.grade = :failedGrade) " +
                                "AND s.id NOT IN (SELECT ph.student.id FROM PaymentHistory ph " +
                                "WHERE ph.subject.subjectId = :subjectId AND ph.status = 'SUCCESS') " +
                                "AND s.id NOT IN (SELECT sc.student.id FROM Students_Classes sc " +
                                "WHERE sc.classEntity.subject.subjectId = :subjectId)",
                        Students.class)
                .setParameter("subjectId", subjectId)
                .setParameter("major", major)
                .setParameter("failedGrade", Grades.NOT_PASS)
                .getResultList();
    }

    @Override
    public List<Students> listStudentsFailedSubjectAndPaid(Classes classes) {
        if (classes == null || classes.getSubject() == null) {
            return List.of();
        }
        String subjectId = classes.getSubject().getSubjectId();
        Majors major = staffsService.getStaffMajor();
        if (major == null) {
            return List.of();
        }
        return entityManager.createQuery(
                        "SELECT s FROM Students s WHERE s.major = :major AND s.id IN " +
                                "(SELECT at.student.id FROM AcademicTranscripts at WHERE at.subject.subjectId = :subjectId " +
                                "AND at.grade = :failedGrade) " +
                                "AND s.id IN (SELECT ph.student.id FROM PaymentHistory ph " +
                                "WHERE ph.subject.subjectId = :subjectId AND ph.status = 'SUCCESS') " +
                                "AND s.id NOT IN (SELECT sc.student.id FROM Students_Classes sc " +
                                "WHERE sc.classEntity.subject.subjectId = :subjectId)",
                        Students.class)
                .setParameter("subjectId", subjectId)
                .setParameter("major", major)
                .setParameter("failedGrade", Grades.NOT_PASS)
                .getResultList();
    }

    @Override
    public List<Students> listStudentsNotTakenSubject(Classes classes, boolean hasPaid) {
        if (classes == null || classes.getSubject() == null) {
            return List.of();
        }
        String subjectId = classes.getSubject().getSubjectId();
        Majors major = staffsService.getStaffMajor();
        if (major == null) {
            return List.of();
        }
        String paymentCondition = hasPaid ?
                "AND s.id IN (SELECT ph.student.id FROM PaymentHistory ph WHERE ph.subject.subjectId = :subjectId AND ph.status = 'SUCCESS')" :
                "AND s.id NOT IN (SELECT ph.student.id FROM PaymentHistory ph WHERE ph.subject.subjectId = :subjectId AND ph.status = 'SUCCESS')";
        return entityManager.createQuery(
                        "SELECT s FROM Students s WHERE s.major = :major AND s.id NOT IN " +
                                "(SELECT at.student.id FROM AcademicTranscripts at WHERE at.subject.subjectId = :subjectId) " +
                                paymentCondition + " " +
                                "AND s.id NOT IN (SELECT sc.student.id FROM Students_Classes sc " +
                                "WHERE sc.classEntity.subject.subjectId = :subjectId)",
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
                        "SELECT s FROM Students s WHERE s.id IN " +
                                "(SELECT sc.student.id FROM Students_Classes sc WHERE sc.classEntity = :class) " +
                                "AND EXISTS (SELECT t FROM Timetable t WHERE t.classEntity = :class " +
                                "AND (t.date >= :currentDate OR t.dayOfTheWeek IS NOT NULL))",
                        Students.class)
                .setParameter("class", classes)
                .setParameter("currentDate", currentDate)
                .getResultList();
    }

    @Override
    public List<Students> listStudentsCompletedPreviousSemester(Classes classes) {
        if (classes == null || classes.getSubject() == null || classes.getSubject().getSemester() == null) {
            return List.of();
        }
        Semester prevSemester = getPreviousSemester(classes.getSubject().getSemester());
        if (prevSemester == null) {
            return List.of();
        }
        String subjectId = classes.getSubject().getSubjectId();
        Majors major = staffsService.getStaffMajor();
        if (major == null) {
            return List.of();
        }
        return entityManager.createQuery(
                        "SELECT s FROM Students s WHERE s.major = :major AND s.id IN " +
                                "(SELECT sc.student.id FROM Students_Classes sc WHERE sc.classEntity.subject.semester = :prevSemester " +
                                "GROUP BY sc.student.id HAVING COUNT(DISTINCT sc.classEntity.subject.subjectId) = " +
                                "(SELECT COUNT(DISTINCT sub.subjectId) FROM Subjects sub WHERE sub.semester = :prevSemester)) " +
                                "AND s.id NOT IN (SELECT sc2.student.id FROM Students_Classes sc2 " +
                                "WHERE sc2.classEntity.subject.subjectId = :subjectId)",
                        Students.class)
                .setParameter("prevSemester", prevSemester)
                .setParameter("subjectId", subjectId)
                .setParameter("major", major)
                .getResultList();
    }

    private Semester getPreviousSemester(Semester currentSemester) {
        if (currentSemester == null) {
            return null;
        }
        int currentOrdinal = currentSemester.ordinal();
        if (currentOrdinal == 0) {
            return null; // SEMESTER_1 has no previous semester
        }
        return Semester.values()[currentOrdinal - 1];
    }
}