package com.example.demo.classes.majorClasses.dao;

import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.major.model.Majors;
import com.example.demo.subject.majorSubject.model.MajorSubjects;
import com.example.demo.user.staff.service.StaffsService;
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
public class MajorClassesDAOImpl implements MajorClassesDAO {
    @PersistenceContext
    private EntityManager entityManager;

    private final StaffsService staffsService;

    public MajorClassesDAOImpl(StaffsService staffsService) {
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
        try {
            return entityManager.createQuery("SELECT c FROM MajorClasses c WHERE c.nameClass = :name", MajorClasses.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void addClass(MajorClasses c) {
        c.setCreator(staffsService.getStaff());
        c.setCreatedAt(LocalDateTime.now());
        entityManager.persist(c);
    }

    @Override
    public MajorClasses editClass(String id, MajorClasses classObj) {
        if (classObj == null || id == null) {
            throw new IllegalArgumentException("Class object or ID cannot be null");
        }

        MajorClasses existingClass = entityManager.find(MajorClasses.class, id);
        if (existingClass == null) {
            throw new IllegalArgumentException("Class with ID " + id + " not found");
        }

        // Validate and throw exception if validation fails
        List<String> errors = validateClass(classObj, id);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join("; ", errors));
        }

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

    @Override
    public String generateUniqueClassId(String majorId, LocalDateTime createdDate) {
        String prefix;
        switch (majorId) {
            case "major001":
                prefix = "CLSGBH";
                break;
            case "major002":
                prefix = "CLSGCH";
                break;
            case "major003":
                prefix = "CLSGDH";
                break;
            case "major004":
                prefix = "CLSGKH";
                break;
            default:
                prefix = "CLSGEN";
                break;
        }

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
    public List<String> validateClass(MajorClasses classObj, String excludeId) {
        List<String> errors = new ArrayList<>();

        if (classObj.getNameClass() == null || classObj.getNameClass().trim().isEmpty()) {
            errors.add("Class name cannot be blank.");
        } else if (!isValidName(classObj.getNameClass())) {
            errors.add("Class name is not valid. Only letters, numbers, spaces, and standard punctuation are allowed.");
        }

        if (classObj.getSlotQuantity() == null || classObj.getSlotQuantity() <= 0) {
            errors.add("Total slots must be greater than 0.");
        }

        if (classObj.getSubject() == null || classObj.getSubject().getSubjectId() == null) {
            errors.add("Subject is required.");
        } else {
            MajorSubjects subject = entityManager.find(MajorSubjects.class, classObj.getSubject().getSubjectId());
            if (subject == null) {
                errors.add("Invalid subject selected.");
            } else {
                classObj.setSubject(subject); // Ensure the subject is a managed entity
            }
        }

        if (classObj.getNameClass() != null && getClassByName(classObj.getNameClass()) != null &&
                (excludeId == null || !getClassByName(classObj.getNameClass()).getClassId().equals(excludeId))) {
            errors.add("Class Name is already in use.");
        }

        return errors;
    }

    @Override
    public List<MajorClasses> searchClasses(String searchType, String keyword, int firstResult, int pageSize, Majors major) {
        String queryString = "SELECT c FROM MajorClasses c JOIN FETCH c.creator LEFT JOIN FETCH c.subject WHERE c.creator.majorManagement = :major";
        if ("name".equals(searchType)) {
            queryString += " AND LOWER(c.nameClass) LIKE LOWER(:keyword)";
        } else {
            queryString += " AND c.classId LIKE :keyword";
        }
        return entityManager.createQuery(queryString, MajorClasses.class)
                .setParameter("major", major)
                .setParameter("keyword", "%" + keyword + "%")
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public long countSearchResults(String searchType, String keyword, Majors major) {
        String queryString = "SELECT COUNT(c) FROM MajorClasses c WHERE c.creator.majorManagement = :major";
        if ("name".equals(searchType)) {
            queryString += " AND LOWER(c.nameClass) LIKE LOWER(:keyword)";
        } else {
            queryString += " AND c.classId LIKE :keyword";
        }
        return entityManager.createQuery(queryString, Long.class)
                .setParameter("major", major)
                .setParameter("keyword", "%" + keyword + "%")
                .getSingleResult();
    }

    @Override
    public List<MajorClasses> getPaginatedClasses(int firstResult, int pageSize, Majors major) {
        return entityManager.createQuery("SELECT c FROM MajorClasses c JOIN FETCH c.creator LEFT JOIN FETCH c.subject WHERE c.creator.majorManagement = :major", MajorClasses.class)
                .setParameter("major", major)
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public long numberOfClasses(Majors major) {
        return entityManager.createQuery("SELECT COUNT(c) FROM MajorClasses c WHERE c.creator.majorManagement = :major", Long.class)
                .setParameter("major", major)
                .getSingleResult();
    }

    private boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        String nameRegex = "^[\\p{L}0-9][\\p{L}0-9 .'-]{0,49}$";
        return name.matches(nameRegex);
    }
}