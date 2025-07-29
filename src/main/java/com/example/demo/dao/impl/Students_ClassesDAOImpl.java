package com.example.demo.dao.impl;

import com.example.demo.dao.Students_ClassesDAO;
import com.example.demo.entity.*;
import com.example.demo.service.StaffsService;
import com.example.demo.service.StudentsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
public class Students_ClassesDAOImpl implements Students_ClassesDAO {
    @Override
    public void addStudentsToClass(Classes classes, List<String> studentIds) {
        for (String studentId : studentIds) {
            Students student = studentsService.getStudentById(studentId);
            Students_Classes studentClass = new Students_Classes();
            StudentsClassesId id = new StudentsClassesId(studentId, classes.getClassId());
            studentClass.setId(id);
            studentClass.setClassEntity(classes);
            studentClass.setStudent(student);
            studentClass.setCreatedAt(LocalDateTime.now());
            studentClass.setAddedBy(staffsService.getStaffs());
            entityManager.persist(studentClass);
        }
    }

    @PersistenceContext
    private EntityManager entityManager;

    private StaffsService staffsService;
    private StudentsService  studentsService;

    public Students_ClassesDAOImpl(StaffsService staffsService) {
        this.staffsService = staffsService;
    }

    @Override
    public List<Students_Classes> listStudentsInClass(Classes classes) {
        return entityManager.createQuery(
                        "SELECT sc FROM Students_Classes sc WHERE sc.classEntity = :class AND sc.student.major = :major",
                        Students_Classes.class)
                .setParameter("class", classes)
                .setParameter("major", staffsService.getMajors())
                .getResultList();
    }

    @Override
    public List<Students> listStudentsNotInClass(Classes classes) {
        return entityManager.createQuery(
                        "SELECT s FROM Students s WHERE s.major = :major AND s.id NOT IN " +
                                "(SELECT sc.student.id FROM Students_Classes sc WHERE sc.classEntity = :class)",
                        Students.class)
                .setParameter("class", classes)
                .setParameter("major", staffsService.getMajors())
                .getResultList();
    }

    @Override
    public List<Students> listStudentsFailedSubjectAndNotPaid(Classes classes) {
        return entityManager.createQuery(
                        "SELECT s FROM Students s WHERE s.major = :major AND s.id IN " +
                                "(SELECT at.id.numberId FROM AcademicTranscript at WHERE at.id.subjectId = :subjectId " +
                                "AND (at.grade = 'F' OR at.status = 'FAILED')) " +
                                "AND s.id NOT IN (SELECT ph.student.id FROM PaymentHistory ph " +
                                "WHERE ph.subject.id = :subjectId AND ph.status = 'SUCCESS') " +
                                "AND s.id NOT IN (SELECT sc.student.id FROM Students_Classes sc " +
                                "WHERE sc.classEntity.subject.id = :subjectId)",
                        Students.class)
                .setParameter("subjectId", classes.getSubject().getSubjectId())
                .setParameter("major", staffsService.getMajors())
                .getResultList();
    }

    @Override
    public List<Students> listStudentsFailedSubjectAndPaid(Classes classes) {
        return entityManager.createQuery(
                        "SELECT s FROM Students s WHERE s.major = :major AND s.id IN " +
                                "(SELECT at.id.numberId FROM AcademicTranscript at WHERE at.id.subjectId = :subjectId " +
                                "AND (at.grade = 'F' OR at.status = 'FAILED')) " +
                                "AND s.id IN (SELECT ph.student.id FROM PaymentHistory ph " +
                                "WHERE ph.subject.id = :subjectId AND ph.status = 'SUCCESS') " +
                                "AND s.id NOT IN (SELECT sc.student.id FROM Students_Classes sc " +
                                "WHERE sc.classEntity.subject.id = :subjectId)",
                        Students.class)
                .setParameter("subjectId", classes.getSubject().getSubjectId())
                .setParameter("major", staffsService.getMajors())
                .getResultList();
    }

    @Override
    public List<Students> listStudentsNotTakenSubject(Classes classes, boolean hasPaid) {
        String paymentCondition = hasPaid ?
                "AND s.id IN (SELECT ph.student.id FROM PaymentHistory ph WHERE ph.subject.id = :subjectId AND ph.status = 'SUCCESS')" :
                "AND s.id NOT IN (SELECT ph.student.id FROM PaymentHistory ph WHERE ph.subject.id = :subjectId AND ph.status = 'SUCCESS')";
        return entityManager.createQuery(
                        "SELECT s FROM Students s WHERE s.major = :major AND s.id NOT IN " +
                                "(SELECT at.id.numberId FROM AcademicTranscript at WHERE at.id.subjectId = :subjectId) " +
                                paymentCondition + " " +
                                "AND s.id NOT IN (SELECT sc.student.id FROM Students_Classes sc " +
                                "WHERE sc.classEntity.subject.id = :subjectId)",
                        Students.class)
                .setParameter("subjectId", classes.getSubject().getSubjectId())
                .setParameter("major", staffsService.getMajors())
                .getResultList();
    }

    @Override
    public List<Students> listStudentsCurrentlyTakingSubject(Classes classes) {
        return entityManager.createQuery(
                        "SELECT s FROM Students s WHERE s.id IN " +
                                "(SELECT sc.student.id FROM Students_Classes sc WHERE sc.classEntity = :class) " +
                                "AND EXISTS (SELECT t FROM Timetable t WHERE t.classEntity = :class " +
                                "AND (t.date >= :currentDate OR t.dayOfTheWeek IS NOT NULL))",
                        Students.class)
                .setParameter("class", classes)
                .setParameter("currentDate", java.time.LocalDate.now())
                .getResultList();
    }

    @Override
    public List<Students> listStudentsCompletedPreviousSemester(Classes classes) {
        return entityManager.createQuery(
                        "SELECT s FROM Students s WHERE s.major = :major AND s.id IN " +
                                "(SELECT sc.student.id FROM Students_Classes sc WHERE sc.classEntity.subject.semester = :prevSemester " +
                                "GROUP BY sc.student.id HAVING COUNT(DISTINCT sc.classEntity.subject.id) = " +
                                "(SELECT COUNT(DISTINCT sub.id) FROM Subjects sub WHERE sub.semester = :prevSemester)) " +
                                "AND s.id NOT IN (SELECT sc2.student.id FROM Students_Classes sc2 " +
                                "WHERE sc2.classEntity.subject.id = :subjectId)",
                        Students.class)
                .setParameter("prevSemester", getPreviousSemester(classes.getSubject().getSemester()))
                .setParameter("subjectId", classes.getSubject().getSubjectId())
                .setParameter("major", staffsService.getMajors())
                .getResultList();
    }

    private Semester getPreviousSemester(Semester currentSemester) {
        if (currentSemester == null) {
            return null;
        }
        int currentOrdinal = currentSemester.ordinal();
        if (currentOrdinal == 0) {
            return null; // SEMESTER_1 không có kỳ trước
        }
        return Semester.values()[currentOrdinal - 1];
    }
}