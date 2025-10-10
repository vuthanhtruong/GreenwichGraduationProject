package com.example.demo.student_class.dao;

import com.example.demo.classes.model.MajorClasses;
import com.example.demo.staff.service.StaffsService;
import com.example.demo.student.model.Students;
import com.example.demo.student_class.model.Students_MajorClasses;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class StudentsMajorClassesDAOImpl implements StudentsMajorClassesDAO {
    private final StaffsService staffsService;

    public StudentsMajorClassesDAOImpl(StaffsService staffsService) {
        this.staffsService = staffsService;
    }

    @Override
    public List<Students> getStudentsNotInClassAndSubject(String classId, String subjectId) {
        return entityManager.createQuery(
                        "SELECT s FROM Students s " +
                                "WHERE s.specialization.major = :major "+"AND s.campus=:campus " +
                                "AND s.id IN (" +
                                "    SELECT srs.student.id FROM StudentRequiredMajorSubjects srs " +
                                "    WHERE srs.subject.subjectId = :subjectId" +
                                ") " +
                                "AND s.id NOT IN (" +
                                "    SELECT smc.id.studentId FROM Students_MajorClasses smc " +
                                "    WHERE smc.id.classId = :classId " +
                                "    OR smc.majorClass.subject.subjectId = :subjectId" +
                                ")",
                        Students.class)
                .setParameter("major", staffsService.getStaffMajor())
                .setParameter("campus", staffsService.getCampusOfStaff())
                .setParameter("classId", classId)
                .setParameter("subjectId", subjectId)
                .getResultList();
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void addStudentToClass(Students_MajorClasses studentsMajorClasses) {
        entityManager.persist(studentsMajorClasses);
    }

    @Override
    public void removeStudentFromClass(String studentId, String classId) {
        Students_MajorClasses studentsMajorClasses = entityManager.createQuery(
                        "SELECT smc FROM Students_MajorClasses smc WHERE smc.id.studentId = :studentId AND smc.id.classId = :classId",
                        Students_MajorClasses.class)
                .setParameter("studentId", studentId)
                .setParameter("classId", classId)
                .getSingleResult();
        if (studentsMajorClasses != null) {
            entityManager.remove(studentsMajorClasses);
        }
    }

    @Override
    public List<Students_MajorClasses> getStudentsInClass(String classId) {
        return entityManager.createQuery(
                        "SELECT smc FROM Students_MajorClasses smc WHERE smc.id.classId = :classId",
                        Students_MajorClasses.class)
                .setParameter("classId", classId)
                .getResultList();
    }

    @Override
    public List<Students> getStudentsByClass(MajorClasses majorClass) {
        return entityManager.createQuery(
                        "SELECT smc.student FROM Students_MajorClasses smc WHERE smc.majorClass = :majorClass",
                        Students.class)
                .setParameter("majorClass", majorClass)
                .getResultList();
    }

    @Override
    public boolean existsByStudentAndClass(String studentId, String classId) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(smc) FROM Students_MajorClasses smc WHERE smc.id.studentId = :studentId AND smc.id.classId = :classId",
                        Long.class)
                .setParameter("studentId", studentId)
                .setParameter("classId", classId)
                .getSingleResult();
        return count > 0;
    }
}