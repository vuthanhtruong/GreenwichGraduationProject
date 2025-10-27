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

import java.util.List;

@Repository
@Transactional
public class StudentRequiredMajorSubjectsDAOImpl implements StudentRequiredMajorSubjectsDAO {
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
    public List<StudentRequiredMajorSubjects> getStudentRequiredMajorSubjects(MajorSubjects subjects) {
        if (subjects == null || staffsService.getStaffMajor() == null) {
            return List.of();
        }

        return entityManager.createQuery(
                        "SELECT srs FROM StudentRequiredMajorSubjects srs " +
                                "WHERE srs.subject = :subjects AND srs.student.specialization.major = :major",
                        StudentRequiredMajorSubjects.class)
                .setParameter("subjects", subjects)
                .setParameter("major", staffsService.getStaffMajor())
                .getResultList();
    }

    @Override
    public List<Students> getStudentNotRequiredMajorSubjects(MajorSubjects subjects) {
        if (subjects == null || staffsService.getStaffMajor() == null) {
            return List.of();
        }

        return entityManager.createQuery(
                        "SELECT s FROM Students s LEFT JOIN StudentRequiredMajorSubjects srs " +
                                "ON s.id = srs.student.id AND srs.subject = :subjects " +
                                "WHERE s.specialization.major = :major AND srs.student.id IS NULL",
                        Students.class)
                .setParameter("subjects", subjects)
                .setParameter("major", staffsService.getStaffMajor())
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