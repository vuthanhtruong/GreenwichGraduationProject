package com.example.demo.studentRequiredSubjects.studentRequiredMajorSubjects.dao;

import com.example.demo.subject.majorSubject.model.MajorSubjects;
import com.example.demo.studentRequiredSubjects.studentRequiredMajorSubjects.model.StudentRequiredMajorSubjects;
import com.example.demo.user.student.model.Students;
import com.example.demo.user.staff.service.StaffsService;
import com.example.demo.subject.minorSubject.model.MinorSubjects;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional
public class StudentRequiredMajorSubjectsDAOImpl implements StudentRequiredMajorSubjectsDAO {

    @Override
    public List<String> getRequiredSubjectNotificationsForStudent(String studentId) {
        String jpql = """
        SELECT CONCAT('You are required to take major subject: ', 
                      s.majorSubject.subjectName, ' (', s.majorSubject.subjectId, ') on ', s.createdAt)
        FROM StudentRequiredMajorSubjects s
        WHERE s.student.id = :studentId
          AND s.notificationType = 'NOTIFICATION_014'
        """;

        return entityManager.createQuery(jpql, String.class)
                .setParameter("studentId", studentId)
                .getResultList();
    }

    @PersistenceContext
    private EntityManager entityManager;

    private final StaffsService staffsService;

    public StudentRequiredMajorSubjectsDAOImpl(StaffsService staffsService) {
        if (staffsService == null) {
            throw new IllegalArgumentException("StaffsService cannot be null");
        }
        this.staffsService = staffsService;
    }

    @Override
    public List<MajorSubjects> studentMajorRoadmap(Students student) {
        return entityManager.createQuery(
                        "SELECT srs.majorSubject FROM StudentRequiredMajorSubjects srs " +
                                "WHERE srs.student = :student",
                        MajorSubjects.class)
                .setParameter("student", student)
                .getResultList();
    }

    @Override
    public List<MinorSubjects> studentMinorRoadmap(Students student) {
        return entityManager.createQuery(
                        "SELECT srs.minorSubject FROM StudentRequiredMinorSubjects srs " +
                                "WHERE srs.student = :student",
                        MinorSubjects.class)
                .setParameter("student", student)
                .getResultList();
    }

    @Override
    public List<StudentRequiredMajorSubjects> getStudentRequiredMajorSubjects(MajorSubjects subject, Integer admissionYear) {
        if (subject == null || staffsService.getStaffMajor() == null) {
            return List.of();
        }

        String jpql = "SELECT srs FROM StudentRequiredMajorSubjects srs " +
                "WHERE srs.subject = :subjects " +
                "AND srs.student.specialization.major = :major And srs.student.campus.campusId=:campus";

        var query = entityManager.createQuery(jpql, StudentRequiredMajorSubjects.class)
                .setParameter("subjects", subject)
                .setParameter("major", staffsService.getStaffMajor());

        if (admissionYear != null) {
            jpql += " AND srs.student.admissionYear = :admissionYear";
            query = entityManager.createQuery(jpql, StudentRequiredMajorSubjects.class)
                    .setParameter("subjects", subject)
                    .setParameter("major", staffsService.getStaffMajor())
                    .setParameter("admissionYear", admissionYear).setParameter("campus", staffsService.getCampusOfStaff().getCampusId());
        }

        return query.getResultList();
    }

    @Override
    public List<Students> getStudentNotRequiredMajorSubjects(MajorSubjects subject, Integer admissionYear) {
        if (subject == null || staffsService.getStaffMajor() == null || admissionYear == null) {
            return List.of();
        }

        return entityManager.createQuery(
                        """
                        SELECT s FROM Students s
                        WHERE s.specialization.major = :major
                          AND s.curriculum = :curriculum
                          AND s.admissionYear = :admissionYear
                          AND s.campus.campusId=:campus
                          AND NOT EXISTS (
                            SELECT 1 FROM StudentRequiredMajorSubjects srs
                            WHERE srs.student = s
                              AND srs.subject = :subject
                          )
                        """, Students.class)
                .setParameter("major", staffsService.getStaffMajor())
                .setParameter("curriculum", subject.getCurriculum())
                .setParameter("admissionYear", admissionYear)
                .setParameter("subject", subject)
                .setParameter("campus", staffsService.getCampusOfStaff().getCampusId())
                .getResultList();
    }

    @Override
    public List<MajorSubjects> getSubjectsByCurriculumId(String curriculumId) {
        if (curriculumId == null || curriculumId.trim().isEmpty()) {
            return entityManager.createQuery(
                            "SELECT s FROM MajorSubjects s WHERE s.major = :major ORDER BY s.semester ASC",
                            MajorSubjects.class)
                    .setParameter("major", staffsService.getStaffMajor())
                    .getResultList();
        }

        return entityManager.createQuery(
                        "SELECT s FROM MajorSubjects s WHERE s.curriculum.curriculumId = :curriculumId AND s.major = :major ORDER BY s.semester ASC",
                        MajorSubjects.class)
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
                        "SELECT COUNT(srs) FROM StudentRequiredMajorSubjects srs " +
                                "WHERE srs.student.id = :studentId AND srs.subject.subjectId = :subjectId",
                        Long.class)
                .setParameter("studentId", studentId)
                .setParameter("subjectId", subjectId)
                .getSingleResult();

        return count > 0;
    }

    @Override
    public void addStudentRequiredMajorSubject(StudentRequiredMajorSubjects srm) {
        if (srm == null) {
            throw new IllegalArgumentException("StudentRequiredMajorSubjects cannot be null");
        }
        entityManager.persist(srm);
    }

    @Override
    public boolean removeStudentRequiredMajorSubject(String studentId, String subjectId) {
        if (studentId == null || subjectId == null) {
            return false;
        }

        Long count = (long) entityManager.createQuery(
                        "DELETE FROM StudentRequiredMajorSubjects srs " +
                                "WHERE srs.student.id = :studentId AND srs.subject.subjectId = :subjectId")
                .setParameter("studentId", studentId)
                .setParameter("subjectId", subjectId)
                .executeUpdate();

        return count > 0;
    }
}