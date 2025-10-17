package com.example.demo.students_Classes.abstractStudent_Class.dao;

import com.example.demo.students_Classes.abstractStudent_Class.model.Students_Classes;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class StudentsClassesDAOImpl implements StudentsClassesDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Students_Classes> getClassByStudent(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID must not be null or empty");
        }
        return entityManager.createQuery(
                        "SELECT sc FROM Students_Classes sc JOIN FETCH sc.classEntity WHERE sc.id.studentId = :studentId",
                        Students_Classes.class)
                .setParameter("studentId", studentId)
                .getResultList();
    }
}