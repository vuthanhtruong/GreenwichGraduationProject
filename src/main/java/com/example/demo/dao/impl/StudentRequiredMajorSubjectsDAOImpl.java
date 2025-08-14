package com.example.demo.dao.impl;

import com.example.demo.dao.StudentRequiredMajorSubjectsDAO;
import com.example.demo.entity.MajorSubjects;
import com.example.demo.entity.AbstractClasses.StudentRequiredSubjects;
import com.example.demo.entity.StudentRequiredMajorSubjects;
import com.example.demo.entity.Students;
import com.example.demo.service.StaffsService;
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

    private boolean isValidSubjectAndMajor(MajorSubjects subjects) {
        return subjects != null && staffsService.getStaffMajor() != null;
    }

    @Override
    public List<StudentRequiredMajorSubjects> getStudentRequiredMajorSubjects(MajorSubjects subjects) {
        if (!isValidSubjectAndMajor(subjects)) {
            return List.of();
        }
        return entityManager.createQuery(
                        "SELECT srs FROM StudentRequiredMajorSubjects srs WHERE srs.subject = :subjects AND srs.student.major = :major",
                        StudentRequiredMajorSubjects.class)
                .setParameter("subjects", subjects)
                .setParameter("major", staffsService.getStaffMajor())
                .getResultList();
    }

    @Override
    public List<Students> getStudentNotRequiredMajorSubjects(MajorSubjects subjects) {
        if (!isValidSubjectAndMajor(subjects)) {
            return List.of();
        }
        return entityManager.createQuery(
                        "SELECT s FROM Students s LEFT JOIN StudentRequiredMajorSubjects srs ON s.id = srs.student.id AND srs.subject = :subjects " +
                                "WHERE s.major = :major AND srs.student.id IS NULL",
                        Students.class)
                .setParameter("subjects", subjects)
                .setParameter("major", staffsService.getStaffMajor())
                .getResultList();
    }
}