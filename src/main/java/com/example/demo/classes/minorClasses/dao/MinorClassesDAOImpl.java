package com.example.demo.classes.minorClasses.dao;

import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.subject.minorSubject.model.MinorSubjects;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import com.example.demo.user.deputyStaff.service.DeputyStaffsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class MinorClassesDAOImpl implements MinorClassesDAO {
    @PersistenceContext
    private EntityManager entityManager;

    private final DeputyStaffsService deputyStaffsService;

    public MinorClassesDAOImpl(DeputyStaffsService deputyStaffsService) {
        if (deputyStaffsService == null) {
            throw new IllegalArgumentException("DeputyStaffsService cannot be null");
        }
        this.deputyStaffsService = deputyStaffsService;
    }

    @Override
    public List<MinorClasses> getClasses() {
        return entityManager.createQuery("SELECT c FROM MinorClasses c ", MinorClasses.class)
                .getResultList();
    }

    @Override
    public MinorClasses getClassById(String id) {
        return entityManager.find(MinorClasses.class, id);
    }

    @Override
    public MinorClasses getClassByName(String name) {
        try {
            return entityManager.createQuery("SELECT c FROM MinorClasses c WHERE c.nameClass = :name", MinorClasses.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void addClass(MinorClasses c) {
        c.setCreator(deputyStaffsService.getDeputyStaff());
        c.setCreatedAt(LocalDateTime.now());
        entityManager.persist(c);
    }

    @Override
    public MinorClasses editClass(String id, MinorClasses classObj) {
        if (classObj == null || id == null) {
            throw new IllegalArgumentException("Class object or ID cannot be null");
        }

        MinorClasses existingClass = entityManager.find(MinorClasses.class, id);
        if (existingClass == null) {
            throw new IllegalArgumentException("Class with ID " + id + " not found");
        }

        List<String> errors = validateClass(classObj, id);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join("; ", errors));
        }

        existingClass.setNameClass(classObj.getNameClass());
        existingClass.setSlotQuantity(classObj.getSlotQuantity());
        existingClass.setSession(classObj.getSession());
        existingClass.setMinorSubject(classObj.getMinorSubject());
        if (classObj.getCreator() != null) existingClass.setCreator(classObj.getCreator());
        if (classObj.getCreatedAt() != null) existingClass.setCreatedAt(classObj.getCreatedAt());

        return entityManager.merge(existingClass);
    }

    @Override
    public void deleteClass(String id) {
        MinorClasses c = entityManager.find(MinorClasses.class, id);
        if (c == null) {
            throw new IllegalArgumentException("Class with ID " + id + " not found");
        }
        entityManager.remove(c);
    }

    @Override
    public String generateUniqueClassId(LocalDateTime createdDate) {
        String prefix = "CLSMIN";
        String year = String.format("%02d", createdDate.getYear() % 100);
        String date = String.format("%02d%02d", createdDate.getMonthValue(), createdDate.getDayOfMonth());

        String classId;
        SecureRandom random = new SecureRandom();
        do {
            String randomDigit = String.valueOf(random.nextInt(10));
            classId = prefix + year + date + randomDigit;
        } while (getClassById(classId) != null);
        return classId;
    }

    @Override
    public List<String> validateClass(MinorClasses classObj, String excludeId) {
        List<String> errors = new ArrayList<>();

        if (classObj.getNameClass() == null || classObj.getNameClass().trim().isEmpty()) {
            errors.add("Class name cannot be blank.");
        } else if (!isValidName(classObj.getNameClass())) {
            errors.add("Class name is not valid. Only letters, numbers, spaces, and standard punctuation are allowed.");
        }

        if (classObj.getSlotQuantity() == null || classObj.getSlotQuantity() <= 0) {
            errors.add("Total slots must be greater than 0.");
        }

        if (classObj.getMinorSubject() == null || classObj.getMinorSubject().getSubjectId() == null) {
            errors.add("Subject is required.");
        } else {
            MinorSubjects subject = entityManager.find(MinorSubjects.class, classObj.getMinorSubject().getSubjectId());
            if (subject == null) {
                errors.add("Invalid subject selected.");
            } else {
                classObj.setMinorSubject(subject);
            }
        }

        if (classObj.getSession() == null) {
            errors.add("Session is required.");
        }

        if (classObj.getNameClass() != null && getClassByName(classObj.getNameClass()) != null &&
                (excludeId == null || !getClassByName(classObj.getNameClass()).getClassId().equals(excludeId))) {
            errors.add("Class name is already in use.");
        }

        return errors;
    }

    @Override
    public List<MinorClasses> searchClasses(String searchType, String keyword, int firstResult, int pageSize ) {
        String queryString = "SELECT c FROM MinorClasses c JOIN FETCH c.creator LEFT JOIN FETCH c.minorSubject ";
        if ("name".equals(searchType)) {
            queryString += " AND LOWER(c.nameClass) LIKE LOWER(:keyword)";
        } else {
            queryString += " AND c.classId LIKE :keyword";
        }
        return entityManager.createQuery(queryString, MinorClasses.class)

                .setParameter("keyword", "%" + keyword + "%")
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public long countSearchResults(String searchType, String keyword ) {
        String queryString = "SELECT COUNT(c) FROM MinorClasses c ";
        if ("name".equals(searchType)) {
            queryString += " AND LOWER(c.nameClass) LIKE LOWER(:keyword)";
        } else {
            queryString += " AND c.classId LIKE :keyword";
        }
        return entityManager.createQuery(queryString, Long.class)
                .setParameter("keyword", "%" + keyword + "%")
                .getSingleResult();
    }

    @Override
    public List<MinorClasses> getPaginatedClasses(int firstResult, int pageSize) {
        return entityManager.createQuery("SELECT c FROM MinorClasses c JOIN FETCH c.creator LEFT JOIN FETCH c.minorSubject ", MinorClasses.class)
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public long numberOfClasses() {
        return entityManager.createQuery("SELECT COUNT(c) FROM MinorClasses c ", Long.class)
                .getSingleResult();
    }

    @Override
    public void setNullWhenDeletingSubject(MinorSubjects subject) {
        entityManager.createQuery("UPDATE MinorClasses c SET c.minorSubject = NULL WHERE c.minorSubject = :subject")
                .setParameter("subject", subject)
                .executeUpdate();
    }

    @Override
    public void deleteClassBySubject(MinorSubjects subject) {
        entityManager.createQuery("DELETE FROM MinorClasses c WHERE c.minorSubject = :subject")
                .setParameter("subject", subject)
                .executeUpdate();
    }

    private boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        String nameRegex = "^[\\p{L}0-9][\\p{L}0-9 .'-]{0,49}$";
        return name.matches(nameRegex);
    }
}