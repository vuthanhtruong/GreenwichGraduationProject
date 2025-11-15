package com.example.demo.studentRequiredSubjects.studentRequiredSpecializedSubjects.dao;

import com.example.demo.subject.specializedSubject.model.SpecializedSubject;
import com.example.demo.studentRequiredSubjects.studentRequiredSpecializedSubjects.model.StudentRequiredSpecializedSubjects;
import com.example.demo.user.student.model.Students;
import com.example.demo.user.staff.service.StaffsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional
public class StudentRequiredSpecializedSubjectsDAOImpl implements StudentRequiredSpecializedSubjectsDAO {

    @Override
    public List<String> getRequiredSubjectNotificationsForStudent(String studentId) {
        String jpql = """
        SELECT CONCAT('You are required to take specialized subject: ', 
                      s.specializedSubject.subjectName, ' (', s.specializedSubject.subjectId, ') on ', s.createdAt)
        FROM StudentRequiredSpecializedSubjects s
        WHERE s.student.id = :studentId
          AND s.notificationType = 'NOTIFICATION_016'
        """;

        return entityManager.createQuery(jpql, String.class)
                .setParameter("studentId", studentId)
                .getResultList();
    }

    @Override
    public boolean isStudentAlreadyRequiredForSpecializedSubject(String studentId, String subjectId) {
        if (studentId == null || subjectId == null) {
            return false;
        }
        Long count = entityManager.createQuery(
                        "SELECT COUNT(srs) FROM StudentRequiredSpecializedSubjects srs " +
                                "WHERE srs.student.id = :studentId AND srs.specializedSubject.subjectId = :subjectId AND srs.student.campus=:campus",
                        Long.class)
                .setParameter("studentId", studentId)
                .setParameter("subjectId", subjectId)
                .setParameter("campus",staffsService.getCampusOfStaff())
                .getSingleResult();
        return count > 0;
    }

    @PersistenceContext
    private EntityManager entityManager;

    private final StaffsService staffsService;

    public StudentRequiredSpecializedSubjectsDAOImpl(StaffsService staffsService) {
        if (staffsService == null) {
            throw new IllegalArgumentException("StaffsService cannot be null");
        }
        this.staffsService = staffsService;
    }

    @Override
    public List<SpecializedSubject> studentSpecializedRoadmap(Students student) {
        return entityManager.createQuery(
                        "SELECT srs.specializedSubject FROM StudentRequiredSpecializedSubjects srs " +
                                "WHERE srs.student = :student",
                        SpecializedSubject.class)
                .setParameter("student", student)
                .getResultList();
    }

    @Override
    public List<StudentRequiredSpecializedSubjects> getStudentRequiredSpecializedSubjects(SpecializedSubject subject, Integer admissionYear) {
        if (subject == null || staffsService.getStaffMajor() == null) {
            return List.of();
        }

        String jpql = "SELECT srs FROM StudentRequiredSpecializedSubjects srs " +
                "WHERE srs.specializedSubject = :subject " +
                "AND srs.student.specialization.major = :major " +
                "AND srs.student.campus = :campus";

        // Chỉ thêm điều kiện admissionYear nếu không null
        if (admissionYear != null) {
            jpql += " AND srs.student.admissionYear = :admissionYear";
        }

        var query = entityManager.createQuery(jpql, StudentRequiredSpecializedSubjects.class)
                .setParameter("subject", subject)
                .setParameter("major", staffsService.getStaffMajor())
                .setParameter("campus", staffsService.getCampusOfStaff());

        if (admissionYear != null) {
            query.setParameter("admissionYear", admissionYear);
        }

        return query.getResultList();
    }

    @Override
    public List<Students> getStudentNotRequiredSpecializedSubjects(SpecializedSubject subject, Integer admissionYear) {
        if (subject == null || staffsService.getStaffMajor() == null) {
            return List.of();
        }

        return entityManager.createQuery(
                        "SELECT s FROM Students s " +
                                "WHERE s.specialization.major = :major " +
                                "AND s.curriculum=:curriculum AND s.campus = :campus " +
                                "AND NOT EXISTS (" +
                                "   SELECT 1 FROM StudentRequiredSpecializedSubjects srs " +
                                "   WHERE srs.student = s " +
                                "   AND srs.specializedSubject = :subject and s.admissionYear=:admissionYear" +
                                ")",
                        Students.class)
                .setParameter("subject", subject)
                .setParameter("major", staffsService.getStaffMajor())
                .setParameter("curriculum",subject.getCurriculum())
                .setParameter("campus", staffsService.getCampusOfStaff())
                .setParameter("admissionYear",admissionYear)
                .getResultList();
    }


    @Override
    public List<SpecializedSubject> getSubjectsByCurriculumId(String curriculumId) {
        if (curriculumId == null || curriculumId.trim().isEmpty()) {
            return entityManager.createQuery(
                            "SELECT s FROM SpecializedSubject s WHERE s.specialization.major = :major ORDER BY s.semester ASC",
                            SpecializedSubject.class)
                    .setParameter("major", staffsService.getStaffMajor())
                    .getResultList();
        }

        return entityManager.createQuery(
                        "SELECT s FROM SpecializedSubject s WHERE s.curriculum.curriculumId = :curriculumId AND s.specialization.major = :major ORDER BY s.semester ASC",
                        SpecializedSubject.class)
                .setParameter("curriculumId", curriculumId)
                .setParameter("major", staffsService.getStaffMajor())
                .getResultList();
    }

    @Override
    public boolean isStudentAlreadyRequiredForSubject(String studentId, String subjectId) {
        if (studentId == null || subjectId == null) {
            return false;
        }

        Long count = entityManager.createQuery(
                        "SELECT COUNT(srs) FROM StudentRequiredSpecializedSubjects srs " +
                                "WHERE srs.student.id = :studentId AND srs.specializedSubject.subjectId = :subjectId AND srs.student.campus=:campus",
                        Long.class)
                .setParameter("studentId", studentId)
                .setParameter("subjectId", subjectId)
                .setParameter("campus",staffsService.getCampusOfStaff())
                .getSingleResult();

        return count > 0;
    }

    @Override
    public void addStudentRequiredSpecializedSubject(StudentRequiredSpecializedSubjects srs) {
        if (srs == null) {
            throw new IllegalArgumentException("StudentRequiredSpecializedSubjects cannot be null");
        }
        entityManager.persist(srs);
    }

    @Override
    public boolean removeStudentRequiredSpecializedSubject(String studentId, String subjectId) {
        if (studentId == null || subjectId == null) {
            return false;
        }

        Long count = (long) entityManager.createQuery(
                        "DELETE FROM StudentRequiredSpecializedSubjects srs " +
                                "WHERE srs.student.id = :studentId AND srs.specializedSubject.subjectId = :subjectId AND srs.student.campus=:campus")
                .setParameter("studentId", studentId)
                .setParameter("subjectId", subjectId)
                .setParameter("campus",staffsService.getCampusOfStaff())
                .executeUpdate();

        return count > 0;
    }
}