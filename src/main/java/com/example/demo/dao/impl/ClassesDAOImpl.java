package com.example.demo.dao.impl;

import com.example.demo.dao.ClassesDAO;
import com.example.demo.entity.MajorClasses;
import com.example.demo.entity.Majors;
import com.example.demo.entity.MajorSubjects;
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
    public void SetNullWhenDeletingSubject(MajorSubjects subject) {
        entityManager.createQuery("UPDATE MajorClasses c SET c.subject = NULL WHERE c.subject = :subject")
                .setParameter("subject", subject)
                .executeUpdate();
    }

    @Override
    public void deleteClassBySubject(MajorSubjects subject) {
        entityManager.createQuery("DELETE FROM MajorClasses c WHERE c.subject = :subject")
                .setParameter("subject", subject)
                .executeUpdate();
    }

    @Override
    public List<MajorClasses> ClassesByMajor(Majors major) {
        if (major == null) {
            return List.of();
        }
        return entityManager.createQuery("SELECT s FROM MajorClasses s WHERE s.creator.majorManagement = :major", MajorClasses.class)
                .setParameter("major", major)
                .getResultList();
    }

    @Override
    public List<MajorClasses> getClasses() {
        return entityManager.createQuery("FROM MajorClasses", MajorClasses.class).getResultList();
    }

    @Override
    public MajorClasses getClassById(String id) {
        return entityManager.find(MajorClasses.class, id);
    }

    @Override
    public MajorClasses getClassByName(String name) {
        return null;
    }

    @Override
    public void addClass(MajorClasses c) {
        c.setCreator(staffsService.getStaff());
        c.setCreatedAt(LocalDateTime.now());
        entityManager.persist(c);
    }

    @Override
    public MajorClasses updateClass(String id, MajorClasses classObj) {
        if (classObj == null || id == null) {
            throw new IllegalArgumentException("Class object or ID cannot be null");
        }

        MajorClasses existingClass = entityManager.find(MajorClasses.class, id);
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
        MajorClasses c = entityManager.find(MajorClasses.class, id);
        if (c == null) {
            throw new IllegalArgumentException("Class with ID " + id + " not found");
        }
        entityManager.remove(c);
    }

    private void validateClass(MajorClasses c) {
        if (c.getNameClass() == null || c.getSlotQuantity() == null || c.getSubject() == null) {
            throw new IllegalArgumentException("Name, slot quantity, and subject are required");
        }
    }
}