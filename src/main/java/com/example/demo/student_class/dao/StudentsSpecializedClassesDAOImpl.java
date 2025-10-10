package com.example.demo.student_class.dao;

import com.example.demo.specializedClasses.model.SpecializedClasses;
import com.example.demo.staff.service.StaffsService;
import com.example.demo.student.model.Students;
import com.example.demo.student_class.model.Students_SpecializedClasses;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class StudentsSpecializedClassesDAOImpl implements StudentsSpecializedClassesDAO {

    @PersistenceContext
    private EntityManager entityManager;

    private final StaffsService staffsService;

    public StudentsSpecializedClassesDAOImpl(StaffsService staffsService) {
        this.staffsService = staffsService;
    }

    @Override
    public List<Students> getStudentsNotInClassAndSubject(String classId, String subjectId) {
        return entityManager.createQuery(
                        "SELECT s FROM Students s " +
                                "WHERE s.specialization.major = :major AND s.campus = :campus " +
                                "AND s.id IN (" +
                                "    SELECT srs.student.id FROM StudentRequiredSpecializedSubjects srs " +
                                "    WHERE srs.specializedSubject.subjectId = :subjectId" +
                                ") " +
                                "AND s.id NOT IN (" +
                                "    SELECT ssc.id.studentId FROM Students_SpecializedClasses ssc " +
                                "    WHERE ssc.id.classId = :classId " +
                                "    OR ssc.specializedClass.specialization.id IN (" +
                                "        SELECT ss.specialization.id FROM SpecializedSubject ss WHERE ss.subjectId = :subjectId" +
                                "    )" +
                                ")",
                        Students.class)
                .setParameter("major", staffsService.getStaffMajor())
                .setParameter("campus", staffsService.getCampusOfStaff())
                .setParameter("classId", classId)
                .setParameter("subjectId", subjectId)
                .getResultList();
    }

    @Override
    public void addStudentToClass(Students_SpecializedClasses studentsSpecializedClasses) {
        if (studentsSpecializedClasses == null) {
            throw new IllegalArgumentException("Student-Class assignment cannot be null");
        }
        entityManager.persist(studentsSpecializedClasses);
    }

    @Override
    public void removeStudentFromClass(String studentId, String classId) {
        Students_SpecializedClasses studentsSpecializedClasses = entityManager.createQuery(
                        "SELECT ssc FROM Students_SpecializedClasses ssc WHERE ssc.id.studentId = :studentId AND ssc.id.classId = :classId",
                        Students_SpecializedClasses.class)
                .setParameter("studentId", studentId)
                .setParameter("classId", classId)
                .getSingleResult();
        if (studentsSpecializedClasses != null) {
            entityManager.remove(studentsSpecializedClasses);
        }
    }

    @Override
    public List<Students_SpecializedClasses> getStudentsInClass(String classId) {
        return entityManager.createQuery(
                        "SELECT ssc FROM Students_SpecializedClasses ssc WHERE ssc.id.classId = :classId",
                        Students_SpecializedClasses.class)
                .setParameter("classId", classId)
                .getResultList();
    }

    @Override
    public List<Students> getStudentsByClass(SpecializedClasses specializedClass) {
        return entityManager.createQuery(
                        "SELECT ssc.student FROM Students_SpecializedClasses ssc WHERE ssc.specializedClass = :specializedClass",
                        Students.class)
                .setParameter("specializedClass", specializedClass)
                .getResultList();
    }

    @Override
    public boolean existsByStudentAndClass(String studentId, String classId) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(ssc) FROM Students_SpecializedClasses ssc WHERE ssc.id.studentId = :studentId AND ssc.id.classId = :classId",
                        Long.class)
                .setParameter("studentId", studentId)
                .setParameter("classId", classId)
                .getSingleResult();
        return count > 0;
    }
}