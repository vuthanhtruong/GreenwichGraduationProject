package com.example.demo.dao.impl;


import com.example.demo.dao.ClassesDAO;
import com.example.demo.entity.Classes;
import com.example.demo.entity.Majors;
import com.example.demo.entity.Subjects;
import com.example.demo.entity.Syllabuses;
import com.example.demo.service.EmailServiceForLectureService;
import com.example.demo.service.EmailServiceForStudentService;
import com.example.demo.service.StaffsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Repository;

import javax.security.auth.Subject;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
public class ClassesDAOImpl implements ClassesDAO {
    @Override
    public void SetNullWhenDeletingSubject(Subjects subject) {
        List<Classes> ClassesList=entityManager.createQuery("select s from Classes s where s.subject=:subject",Classes.class).
                setParameter("subject",subject).getResultList();
        for (Classes Classes : ClassesList) {
            Classes.setSubject(null);
            entityManager.merge(Classes);
        }
    }

    @Override
    public void deleteClassBySubject(Subjects subject) {
        List<Classes> ClassesList=entityManager.createQuery("select s from Classes s where s.subject=:subject",Classes.class).
                setParameter("subject",subject).getResultList();
        for (Classes Classes : ClassesList) {
            entityManager.remove(Classes);
        }
    }

    private StaffsService staffsService;

    public ClassesDAOImpl(StaffsService staffsService) {
      this.staffsService = staffsService;
    }

    @Override
    public List<Classes> ClassesByMajor(Majors major) {
        List<Classes> classes = entityManager.createQuery("SELECT s FROM Classes s WHERE s.creator.majorManagement = :major", Classes.class)
                .setParameter("major", staffsService.getMajors())
                .getResultList();
        return classes;
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Classes> getClasses() {
        return entityManager.createQuery("from Classes ", Classes.class).getResultList();
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
        c.setCreator(staffsService.getStaffs());
        c.setCreatedAt(LocalDateTime.now());
        entityManager.persist(c);
    }

    @Override
    public Classes updateClass(String id, Classes classObj) {
        if (classObj == null) {
            throw new IllegalArgumentException("Class object cannot be null");
        }

        Classes existingClass = entityManager.find(Classes.class, id);
        if (existingClass == null) {
            throw new IllegalArgumentException("Class with ID " + id + " not found");
        }

        // Validate required fields
        if (classObj.getNameClass() == null) {
            throw new IllegalArgumentException("Class name cannot be null");
        }
        if (classObj.getSlotQuantity() == null) {
            throw new IllegalArgumentException("Slot quantity cannot be null");
        }
        if (classObj.getSubject() == null) {
            throw new IllegalArgumentException("Subject cannot be null");
        }
        // Update fields (only if non-null)
        existingClass.setNameClass(classObj.getNameClass()); // Required field
        existingClass.setSlotQuantity(classObj.getSlotQuantity()); // Required field
        existingClass.setSubject(classObj.getSubject()); // Required field
        if (classObj.getCreator() != null) {
            existingClass.setCreator(classObj.getCreator());
        }
        if (classObj.getCreatedAt() != null) {
            existingClass.setCreatedAt(classObj.getCreatedAt());
        }

        return entityManager.merge(existingClass);
    }

    @Override
    public void deleteClass(String id) {
        entityManager.remove(entityManager.find(Classes.class, id));
    }
}
