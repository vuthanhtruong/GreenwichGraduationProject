package com.example.demo.dao.impl;

import com.example.demo.dao.Lecturers_ClassesDAO;
import com.example.demo.entity.Classes;
import com.example.demo.entity.Lecturers;
import com.example.demo.entity.Lecturers_Classes;
import com.example.demo.service.StaffsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class Lecturers_ClassesDAOImpl implements Lecturers_ClassesDAO {

    @PersistenceContext
    private EntityManager entityManager;

    private StaffsService staffsService;

    public Lecturers_ClassesDAOImpl(StaffsService staffsService) {
        this.staffsService = staffsService;
    }

    @Override
    public List<Lecturers_Classes> listLecturersInClass(Classes classes) {
        return entityManager.createQuery(
                        "SELECT lc FROM Lecturers_Classes lc WHERE lc.classEntity = :class and lc.lecturer.majorManagement=:major",
                        Lecturers_Classes.class)
                .setParameter("class", classes)
                .setParameter("major",staffsService.getMajors())
                .getResultList();
    }

    @Override
    public List<Lecturers> listLecturersNotInClass(Classes classes) {
        return entityManager.createQuery(
                        "SELECT l FROM Lecturers l WHERE l.majorManagement=:major and l.id NOT IN " +
                                "(SELECT lc.lecturer.id FROM Lecturers_Classes lc WHERE lc.classEntity = :class)",
                        Lecturers.class)
                .setParameter("class", classes)
                .setParameter("major", staffsService.getMajors())
                .getResultList();
    }
}