package com.example.demo.dao.impl;

import com.example.demo.dao.ClassesDAO;
import com.example.demo.entity.Classes;
import com.example.demo.entity.Majors;
import com.example.demo.entity.Subjects;
import com.example.demo.entity.Syllabuses;
import com.example.demo.service.StaffsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
public class ClassesDAOImpl implements ClassesDAO {
    @PersistenceContext
    private EntityManager entityManager;

    private StaffsService staffsService;

    public ClassesDAOImpl(StaffsService staffsService) {
        if (staffsService == null) {
            throw new IllegalArgumentException("StaffsService cannot be null");
        }
        this.staffsService = staffsService;
    }

    @Override
    public void SetNullWhenDeletingSubject(Subjects subject) {
        entityManager.createQuery("UPDATE Classes c SET c.subject = NULL WHERE c.subject = :subject")
                .setParameter("subject", subject)
                .executeUpdate();
    }

    @Override
    public void deleteClassBySubject(Subjects subject) {
        entityManager.createQuery("DELETE FROM Classes c WHERE c.subject = :subject")
                .setParameter("subject", subject)
                .executeUpdate();
    }

    @Override
    public List<Classes> ClassesByMajor(Majors major) {
        if (major == null) {
            return List.of();
        }
        return entityManager.createQuery("SELECT s FROM Classes s WHERE s.creator.majorManagement = :major", Classes.class)
                .setParameter("major", major)
                .getResultList();
    }

    @Override
    public List<Classes> getClasses() {
        return entityManager.createQuery("FROM Classes", Classes.class).getResultList();
    }

    @Override
    public Classes getClassById(String id) {
        return entityManager.find(Classes.class, id);
    }

    @Override
    public Classes getClassByName(String name) {
        return null;
    }

    @Override
    public void addClass(Classes c) {
        c.setCreator(staffsService.getStaff());
        c.setCreatedAt(LocalDateTime.now());
        entityManager.persist(c);
    }

    @Override
    public Classes updateClass(String id, Classes classObj) {
        if (classObj == null || id == null) {
            throw new IllegalArgumentException("Class object or ID cannot be null");
        }

        Classes existingClass = entityManager.find(Classes.class, id);
        if (existingClass == null) {
            throw new IllegalArgumentException("Class with ID " + id + " not found");
        }

        validateClass(classObj);

        existingClass.setNameClass(classObj.getNameClass());
        existingClass.setSlotQuantity(classObj.getSlotQuantity());
        existingClass.setSubject(classObj.getSubject());
        if (classObj.getCreator() != null) existingClass.setCreator(classObj.getCreator());
        if (classObj.getCreatedAt() != null) existingClass.setCreatedAt(classObj.getCreatedAt());

        return entityManager.merge(existingClass);
    }

    @Override
    public void deleteClass(String id) {
        Classes c = entityManager.find(Classes.class, id);
        if (c == null) {
            throw new IllegalArgumentException("Class with ID " + id + " not found");
        }
        entityManager.remove(c);
    }

    private void validateClass(Classes c) {
        if (c.getNameClass() == null || c.getSlotQuantity() == null || c.getSubject() == null) {
            throw new IllegalArgumentException("Name, slot quantity, and subject are required");
        }
    }
}