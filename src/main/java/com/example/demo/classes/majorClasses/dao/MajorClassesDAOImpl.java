package com.example.demo.classes.majorClasses.dao;

import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.major.model.Majors;
import com.example.demo.subject.majorSubject.model.MajorSubjects;
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
    public List<MajorClasses> getClassesByMajorAndCampus(Majors major, String campusId) {
        if (major == null || campusId == null || campusId.isBlank()) {
            return List.of();
        }
        return entityManager.createQuery(
                        "SELECT c FROM MajorClasses c " +
                                "JOIN FETCH c.creator " +
                                "LEFT JOIN FETCH c.subject " +
                                "WHERE c.creator.majorManagement = :major " +
                                "AND c.creator.campus.campusId = :campusId",
                        MajorClasses.class)
                .setParameter("major", major)
                .setParameter("campusId", campusId)
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

        List<String> errors = validateClass(classObj, id);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join("; ", errors));
        }

        existingClass.setNameClass(classObj.getNameClass());
        existingClass.setSlotQuantity(classObj.getSlotQuantity());
        existingClass.setSubject(classObj.getSubject());

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
        String prefix = switch (majorId) {
            case "major001" -> "CLSGBH";
            case "major002" -> "CLSGCH";
            case "major003" -> "CLSGDH";
            case "major004" -> "CLSGKH";
            default -> "CLSGEN";
        };

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
                classObj.setSubject(subject);
            }
        }

        if (classObj.getNameClass() != null && getClassByName(classObj.getNameClass()) != null &&
                (excludeId == null || !getClassByName(classObj.getNameClass()).getClassId().equals(excludeId))) {
            errors.add("Class Name is already in use.");
        }

        return errors;
    }

    @Override
    public List<MajorClasses> searchClassesByCampus(String searchType, String keyword, int firstResult, int pageSize, Majors major, String campusId) {
        if (major == null || campusId == null || campusId.isBlank()) return List.of();

        String jpql = "SELECT c FROM MajorClasses c " +
                "WHERE c.creator.majorManagement = :major AND c.creator.campus.campusId = :campusId";

        if ("name".equals(searchType)) {
            jpql += " AND LOWER(c.nameClass) LIKE LOWER(:keyword)";
        } else {
            jpql += " AND c.classId LIKE :keyword";
        }

        return entityManager.createQuery(jpql, MajorClasses.class)
                .setParameter("major", major)
                .setParameter("campusId", campusId)
                .setParameter("keyword", "%" + keyword + "%")
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public long countSearchResultsByCampus(String searchType, String keyword, Majors major, String campusId) {
        if (major == null || campusId == null || campusId.isBlank()) return 0;

        String jpql = "SELECT COUNT(c) FROM MajorClasses c " +
                "WHERE c.creator.majorManagement = :major AND c.creator.campus.campusId = :campusId";

        if ("name".equals(searchType)) {
            jpql += " AND LOWER(c.nameClass) LIKE LOWER(:keyword)";
        } else {
            jpql += " AND c.classId LIKE :keyword";
        }

        return entityManager.createQuery(jpql, Long.class)
                .setParameter("major", major)
                .setParameter("campusId", campusId)
                .setParameter("keyword", "%" + keyword + "%")
                .getSingleResult();
    }

    @Override
    public List<MajorClasses> getPaginatedClassesByCampus(int firstResult, int pageSize, Majors major, String campusId) {
        return entityManager.createQuery(
                        "SELECT c FROM MajorClasses c " +
                                "WHERE c.creator.majorManagement = :major AND c.creator.campus.campusId = :campusId",
                        MajorClasses.class)
                .setParameter("major", major)
                .setParameter("campusId", campusId)
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public long numberOfClassesByCampus(Majors major, String campusId) {
        if (major == null || campusId == null || campusId.isBlank()) return 0;

        return entityManager.createQuery(
                        "SELECT COUNT(c) FROM MajorClasses c " +
                                "WHERE c.creator.majorManagement = :major AND c.creator.campus.campusId = :campusId",
                        Long.class)
                .setParameter("major", major)
                .setParameter("campusId", campusId)
                .getSingleResult();
    }

    private boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) return false;
        return name.matches("^[\\p{L}0-9][\\p{L}0-9 .'-]{0,49}$");
    }
}