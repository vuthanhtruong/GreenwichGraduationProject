package com.example.demo.classes.specializedClasses.dao;

import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.subject.specializedSubject.model.SpecializedSubject;
import com.example.demo.major.model.Majors;
import com.example.demo.user.staff.service.StaffsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class SpecializedClassesDAOImpl implements SpecializedClassesDAO {

    private static final Logger log = LoggerFactory.getLogger(SpecializedClassesDAOImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    private final StaffsService staffsService;

    public SpecializedClassesDAOImpl(StaffsService staffsService) {
        if (staffsService == null) {
            throw new IllegalArgumentException("StaffsService cannot be null");
        }
        this.staffsService = staffsService;
    }

    @Override
    public List<SpecializedClasses> ClassesByMajor(Majors major) {
        if (major == null) {
            log.warn("Received null major for ClassesByMajor query");
            return List.of();
        }
        try {
            List<SpecializedClasses> result = entityManager.createQuery(
                            "SELECT c FROM SpecializedClasses c JOIN FETCH c.specializedSubject s WHERE s.specialization.class = :major",
                            SpecializedClasses.class)
                    .setParameter("major", major)
                    .getResultList();
            log.debug("Found {} specialized classes for major ID: {}", result.size(), major.getMajorId());
            return result;
        } catch (Exception e) {
            log.error("Error fetching specialized classes for major ID: {}", major.getMajorId(), e);
            return List.of();
        }
    }

    @Override
    public List<SpecializedClasses> getClasses() {
        try {
            List<SpecializedClasses> result = entityManager.createQuery(
                            "SELECT c FROM SpecializedClasses c JOIN FETCH c.specializedSubject JOIN FETCH c.creator",
                            SpecializedClasses.class)
                    .getResultList();
            log.debug("Found {} specialized classes", result.size());
            return result;
        } catch (Exception e) {
            log.error("Error fetching all specialized classes", e);
            return List.of();
        }
    }

    @Override
    public SpecializedClasses getClassById(String id) {
        try {
            SpecializedClasses result = entityManager.createQuery(
                            "SELECT c FROM SpecializedClasses c JOIN FETCH c.specializedSubject JOIN FETCH c.creator WHERE c.classId = :id",
                            SpecializedClasses.class)
                    .setParameter("id", id)
                    .getSingleResult();
            log.debug("Found specialized class with ID: {}", id);
            return result;
        } catch (Exception e) {
            log.warn("Specialized class with ID: {} not found", id);
            return null;
        }
    }

    @Override
    public SpecializedClasses getClassByName(String name) {
        try {
            SpecializedClasses result = entityManager.createQuery(
                            "SELECT c FROM SpecializedClasses c JOIN FETCH c.specializedSubject JOIN FETCH c.creator WHERE c.nameClass = :name",
                            SpecializedClasses.class)
                    .setParameter("name", name)
                    .getSingleResult();
            log.debug("Found specialized class with name: {}", name);
            return result;
        } catch (Exception e) {
            log.warn("Specialized class with name: {} not found", name);
            return null;
        }
    }

    @Override
    public void addClass(SpecializedClasses c) {
        try {
            c.setCreator(staffsService.getStaff());
            c.setCreatedAt(LocalDateTime.now());
            entityManager.persist(c);
            log.info("Added specialized class with ID: {}", c.getClassId());
        } catch (Exception e) {
            log.error("Error adding specialized class: {}", c.getClassId(), e);
            throw new RuntimeException("Failed to add specialized class", e);
        }
    }

    @Override
    public SpecializedClasses editClass(String id, SpecializedClasses classObj) {
        if (classObj == null || id == null) {
            log.error("Class object or ID is null");
            throw new IllegalArgumentException("Class object or ID cannot be null");
        }

        SpecializedClasses existingClass = getClassById(id);
        if (existingClass == null) {
            log.error("Specialized class with ID: {} not found", id);
            throw new IllegalArgumentException("Class with ID " + id + " not found");
        }

        List<String> errors = validateClass(classObj, id);
        if (!errors.isEmpty()) {
            log.error("Validation errors for class ID {}: {}", id, String.join("; ", errors));
            throw new IllegalArgumentException(String.join("; ", errors));
        }

        existingClass.setNameClass(classObj.getNameClass());
        existingClass.setSlotQuantity(classObj.getSlotQuantity());
        existingClass.setSession(classObj.getSession());
        existingClass.setSpecializedSubject(classObj.getSpecializedSubject());
        if (classObj.getCreator() != null) {
            existingClass.setCreator(classObj.getCreator());
        }
        if (classObj.getCreatedAt() != null) {
            existingClass.setCreatedAt(classObj.getCreatedAt());
        }

        SpecializedClasses updatedClass = entityManager.merge(existingClass);
        log.info("Updated specialized class with ID: {}", id);
        return updatedClass;
    }

    @Override
    public void deleteClass(String id) {
        SpecializedClasses c = getClassById(id);
        if (c == null) {
            log.error("Specialized class with ID: {} not found", id);
            throw new IllegalArgumentException("Class with ID " + id + " not found");
        }
        entityManager.remove(c);
        log.info("Deleted specialized class with ID: {}", id);
    }

    @Override
    public String generateUniqueClassId(String specializedSubjectId, LocalDateTime createdDate) {
        String prefix;
        switch (specializedSubjectId) {
            case "spec001":
                prefix = "CLSSBH";
                break;
            case "spec002":
                prefix = "CLSSCH";
                break;
            case "spec003":
                prefix = "CLSSDH";
                break;
            case "spec004":
                prefix = "CLSSKH";
                break;
            default:
                prefix = "CLSSGEN";
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
        log.debug("Generated unique class ID: {}", classId);
        return classId;
    }

    @Override
    public List<String> validateClass(SpecializedClasses classObj, String excludeId) {
        List<String> errors = new ArrayList<>();

        if (classObj.getNameClass() == null || classObj.getNameClass().trim().isEmpty()) {
            errors.add("Class name cannot be blank.");
        } else if (!isValidName(classObj.getNameClass())) {
            errors.add("Class name is not valid. Only letters, numbers, spaces, and standard punctuation are allowed.");
        }

        if (classObj.getSlotQuantity() == null || classObj.getSlotQuantity() <= 0) {
            errors.add("Total slots must be greater than 0.");
        }

        if (classObj.getSpecializedSubject() == null || classObj.getSpecializedSubject().getSubjectId() == null) {
            errors.add("Specialized subject is required.");
        } else {
            SpecializedSubject subject = entityManager.find(SpecializedSubject.class, classObj.getSpecializedSubject().getSubjectId());
            if (subject == null) {
                errors.add("Invalid specialized subject selected.");
            } else {
                classObj.setSpecializedSubject(subject); // Ensure managed entity
            }
        }

        if (classObj.getNameClass() != null && getClassByName(classObj.getNameClass()) != null &&
                (excludeId == null || !getClassByName(classObj.getNameClass()).getClassId().equals(excludeId))) {
            errors.add("Class Name is already in use.");
        }

        return errors;
    }

    @Override
    public List<SpecializedClasses> searchClasses(String searchType, String keyword, int firstResult, int pageSize, Majors major) {
        String queryString = "SELECT c FROM SpecializedClasses c JOIN FETCH c.creator JOIN FETCH c.specializedSubject s WHERE s.specialization.class = :major";
        if ("name".equals(searchType)) {
            queryString += " AND LOWER(c.nameClass) LIKE LOWER(:keyword)";
        } else {
            queryString += " AND c.classId LIKE :keyword";
        }
        try {
            List<SpecializedClasses> result = entityManager.createQuery(queryString, SpecializedClasses.class)
                    .setParameter("major", major)
                    .setParameter("keyword", "%" + keyword + "%")
                    .setFirstResult(firstResult)
                    .setMaxResults(pageSize)
                    .getResultList();
            log.debug("Found {} specialized classes for searchType: {}, keyword: {}", result.size(), searchType, keyword);
            return result;
        } catch (Exception e) {
            log.error("Error searching specialized classes: {}", e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public long countSearchResults(String searchType, String keyword, Majors major) {
        String queryString = "SELECT COUNT(c) FROM SpecializedClasses c JOIN c.specializedSubject s WHERE s.specialization.major = :major";
        if ("name".equals(searchType)) {
            queryString += " AND LOWER(c.nameClass) LIKE LOWER(:keyword)";
        } else {
            queryString += " AND c.classId LIKE :keyword";
        }
        try {
            Long result = entityManager.createQuery(queryString, Long.class)
                    .setParameter("major", major)
                    .setParameter("keyword", "%" + keyword + "%")
                    .getSingleResult();
            log.debug("Counted {} search results for searchType: {}, keyword: {}", result, searchType, keyword);
            return result;
        } catch (Exception e) {
            log.error("Error counting search results: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public List<SpecializedClasses> getPaginatedClasses(int firstResult, int pageSize, Majors major) {
        try {
            List<SpecializedClasses> result = entityManager.createQuery(
                            "SELECT c FROM SpecializedClasses c JOIN FETCH c.creator JOIN FETCH c.specializedSubject s WHERE s.specialization.major = :major",
                            SpecializedClasses.class)
                    .setParameter("major", major)
                    .setFirstResult(firstResult)
                    .setMaxResults(pageSize)
                    .getResultList();
            log.debug("Found {} paginated specialized classes for major ID: {}", result.size(), major.getMajorId());
            return result;
        } catch (Exception e) {
            log.error("Error fetching paginated specialized classes: {}", e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public long numberOfClasses(Majors major) {
        try {
            Long result = entityManager.createQuery(
                            "SELECT COUNT(c) FROM SpecializedClasses c JOIN c.specializedSubject s WHERE s.specialization.major = :major",
                            Long.class)
                    .setParameter("major", major)
                    .getSingleResult();
            log.debug("Counted {} specialized classes for major ID: {}", result, major.getMajorId());
            return result;
        } catch (Exception e) {
            log.error("Error counting specialized classes: {}", e.getMessage(), e);
            return 0;
        }
    }

    private boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        String nameRegex = "^[\\p{L}0-9][\\p{L}0-9 .'-]{0,49}$";
        return name.matches(nameRegex);
    }
}