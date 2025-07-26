package com.example.demo.dao.impl;

import com.example.demo.dao.Students_ClassesDAO;
import com.example.demo.entity.Classes;
import com.example.demo.entity.Students;
import com.example.demo.entity.Students_Classes;
import com.example.demo.service.StaffsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class Students_ClassesDAOImpl implements Students_ClassesDAO {

    @PersistenceContext
    private EntityManager entityManager;

    private StaffsService staffsService;

    public Students_ClassesDAOImpl(StaffsService staffsService) {
        this.staffsService = staffsService;
    }

    @Override
    public List<Students_Classes> listStudentsInClass(Classes classes) {
        return entityManager.createQuery(
                        "SELECT sc FROM Students_Classes sc WHERE sc.classEntity = :class and sc.student.major=:major",
                        Students_Classes.class)
                .setParameter("class", classes)
                .setParameter("major",staffsService.getMajors())
                .getResultList();
    }

    @Override
    public List<Students> listStudentsNotInClass(Classes classes) {
        return entityManager.createQuery(
                        "SELECT s FROM Students s WHERE s.major=:major and s.id NOT IN " +
                                "(SELECT sc.student.id FROM Students_Classes sc WHERE sc.classEntity = :class)",
                        Students.class)
                .setParameter("class", classes)
                .setParameter("major", staffsService.getMajors())
                .getResultList();
    }
}