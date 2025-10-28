package com.example.demo.classes.specializedClasses.dao;

import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.subject.specializedSubject.model.SpecializedSubject;
import com.example.demo.major.model.Majors;
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

    // KHÔNG DÙNG staffsService ĐỂ LỌC CAMPUS → chỉ dùng để gán creator
    // Nếu cần gán creator → nên dùng SecurityContext (không bắt buộc ở DAO)

    @Override
    public List<SpecializedClasses> getClassesByMajorAndCampus(Majors major, String campusId) {
        if (major == null || campusId == null || campusId.isBlank()) {
            log.warn("Invalid parameters: major={} or campusId={}", major, campusId);
            return List.of();
        }
        try {
            List<SpecializedClasses> result = entityManager.createQuery(
                            "SELECT c FROM SpecializedClasses c " +
                                    "JOIN FETCH c.specializedSubject s " +
                                    "JOIN FETCH c.creator " +
                                    "WHERE s.specialization.major = :major " +
                                    "AND c.creator.campus.campusId = :campusId",
                            SpecializedClasses.class)
                    .setParameter("major", major)
                    .setParameter("campusId", campusId)
                    .getResultList();
            log.debug("Found {} specialized classes for major ID: {}, campus: {}", result.size(), major.getMajorId(), campusId);
            return result;
        } catch (Exception e) {
            log.error("Error fetching classes for major ID: {}, campus: {}", major.getMajorId(), campusId, e);
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
        if (id == null || id.isBlank()) return null;
        try {
            return entityManager.createQuery(
                            "SELECT c FROM SpecializedClasses c JOIN FETCH c.specializedSubject JOIN FETCH c.creator WHERE c.classId = :id",
                            SpecializedClasses.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (Exception e) {
            log.warn("Specialized class with ID: {} not found", id);
            return null;
        }
    }

    @Override
    public SpecializedClasses getClassByName(String name) {
        if (name == null || name.isBlank()) return null;
        try {
            return entityManager.createQuery(
                            "SELECT c FROM SpecializedClasses c JOIN FETCH c.specializedSubject JOIN FETCH c.creator WHERE c.nameClass = :name",
                            SpecializedClasses.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (Exception e) {
            log.warn("Specialized class with name: {} not found", name);
            return null;
        }
    }

    @Override
    public void addClass(SpecializedClasses c) {
        if (c == null) throw new IllegalArgumentException("Class object cannot be null");
        try {
            c.setCreatedAt(LocalDateTime.now());
            entityManager.persist(c);
            log.info("Added specialized class with ID: {}", c.getClassId());
        } catch (Exception e) {
            log.error("Error adding specialized class: {}", c.getClassId(), e);
            throw new RuntimeException("Failed to add class", e);
        }
    }

    @Override
    public SpecializedClasses editClass(String id, SpecializedClasses classObj) {
        if (classObj == null || id == null) {
            throw new IllegalArgumentException("Class object or ID cannot be null");
        }

        SpecializedClasses existing = getClassById(id);
        if (existing == null) {
            throw new IllegalArgumentException("Class with ID " + id + " not found");
        }

        List<String> errors = validateClass(classObj, id);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join("; ", errors));
        }

        existing.setNameClass(classObj.getNameClass());
        existing.setSlotQuantity(classObj.getSlotQuantity());
        existing.setSession(classObj.getSession());
        existing.setSpecializedSubject(classObj.getSpecializedSubject());

        SpecializedClasses updated = entityManager.merge(existing);
        log.info("Updated specialized class ID: {}", id);
        return updated;
    }

    @Override
    public void deleteClass(String id) {
        SpecializedClasses c = getClassById(id);
        if (c == null) {
            throw new IllegalArgumentException("Class with ID " + id + " not found");
        }
        entityManager.remove(c);
        log.info("Deleted specialized class ID: {}", id);
    }

    @Override
    public String generateUniqueClassId(String specializedSubjectId, LocalDateTime createdDate) {
        String prefix = switch (specializedSubjectId) {
            case "spec001" -> "CLSSBH";
            case "spec002" -> "CLSSCH";
            case "spec003" -> "CLSSDH";
            case "spec004" -> "CLSSKH";
            default -> "CLSSGEN";
        };

        String year = String.format("%02d", createdDate.getYear() % 100);
        String date = String.format("%02d%02d", createdDate.getMonthValue(), createdDate.getDayOfMonth());
        String classId;
        SecureRandom random = new SecureRandom();
        do {
            classId = prefix + year + date + random.nextInt(10);
        } while (getClassById(classId) != null);
        return classId;
    }

    @Override
    public List<String> validateClass(SpecializedClasses classObj, String excludeId) {
        List<String> errors = new ArrayList<>();

        if (classObj.getNameClass() == null || classObj.getNameClass().trim().isEmpty()) {
            errors.add("Class name cannot be blank.");
        } else if (!isValidName(classObj.getNameClass())) {
            errors.add("Class name is not valid.");
        }

        if (classObj.getSlotQuantity() == null || classObj.getSlotQuantity() <= 0) {
            errors.add("Total slots must be greater than 0.");
        }

        if (classObj.getSpecializedSubject() == null || classObj.getSpecializedSubject().getSubjectId() == null) {
            errors.add("Specialized subject is required.");
        } else {
            SpecializedSubject subject = entityManager.find(SpecializedSubject.class, classObj.getSpecializedSubject().getSubjectId());
            if (subject == null) {
                errors.add("Invalid specialized subject.");
            } else {
                classObj.setSpecializedSubject(subject);
            }
        }

        if (classObj.getNameClass() != null && getClassByName(classObj.getNameClass()) != null &&
                (excludeId == null || !getClassByName(classObj.getNameClass()).getClassId().equals(excludeId))) {
            errors.add("Class Name is already in use.");
        }

        return errors;
    }

    @Override
    public List<SpecializedClasses> searchClassesByCampus(String searchType, String keyword, int firstResult, int pageSize, Majors major, String campusId) {
        if (major == null || campusId == null || campusId.isBlank()) return List.of();

        String jpql = "SELECT c FROM SpecializedClasses c " +
                "JOIN FETCH c.creator JOIN FETCH c.specializedSubject s " +
                "WHERE s.specialization.major = :major AND c.creator.campus.campusId = :campusId";

        if ("name".equals(searchType)) {
            jpql += " AND LOWER(c.nameClass) LIKE LOWER(:keyword)";
        } else {
            jpql += " AND c.classId LIKE :keyword";
        }

        try {
            List<SpecializedClasses> result = entityManager.createQuery(jpql, SpecializedClasses.class)
                    .setParameter("major", major)
                    .setParameter("campusId", campusId)
                    .setParameter("keyword", "%" + keyword + "%")
                    .setFirstResult(firstResult)
                    .setMaxResults(pageSize)
                    .getResultList();
            log.debug("Search found {} classes (type={}, keyword={})", result.size(), searchType, keyword);
            return result;
        } catch (Exception e) {
            log.error("Search error", e);
            return List.of();
        }
    }

    @Override
    public long countSearchResultsByCampus(String searchType, String keyword, Majors major, String campusId) {
        if (major == null || campusId == null || campusId.isBlank()) return 0;

        String jpql = "SELECT COUNT(c) FROM SpecializedClasses c JOIN c.specializedSubject s " +
                "WHERE s.specialization.major = :major AND c.creator.campus.campusId = :campusId";

        if ("name".equals(searchType)) {
            jpql += " AND LOWER(c.nameClass) LIKE LOWER(:keyword)";
        } else {
            jpql += " AND c.classId LIKE :keyword";
        }

        try {
            Long count = entityManager.createQuery(jpql, Long.class)
                    .setParameter("major", major)
                    .setParameter("campusId", campusId)
                    .setParameter("keyword", "%" + keyword + "%")
                    .getSingleResult();
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("Count error", e);
            return 0;
        }
    }

    @Override
    public List<SpecializedClasses> getPaginatedClassesByCampus(int firstResult, int pageSize, Majors major, String campusId) {
        if (major == null || campusId == null || campusId.isBlank()) return List.of();

        try {
            return entityManager.createQuery(
                            "SELECT c FROM SpecializedClasses c " +
                                    "JOIN FETCH c.creator JOIN FETCH c.specializedSubject s " +
                                    "WHERE s.specialization.major = :major AND c.creator.campus.campusId = :campusId",
                            SpecializedClasses.class)
                    .setParameter("major", major)
                    .setParameter("campusId", campusId)
                    .setFirstResult(firstResult)
                    .setMaxResults(pageSize)
                    .getResultList();
        } catch (Exception e) {
            log.error("Pagination error", e);
            return List.of();
        }
    }

    @Override
    public long numberOfClassesByCampus(Majors major, String campusId) {
        if (major == null || campusId == null || campusId.isBlank()) return 0;

        try {
            Long count = entityManager.createQuery(
                            "SELECT COUNT(c) FROM SpecializedClasses c JOIN c.specializedSubject s " +
                                    "WHERE s.specialization.major = :major AND c.creator.campus.campusId = :campusId",
                            Long.class)
                    .setParameter("major", major)
                    .setParameter("campusId", campusId)
                    .getSingleResult();
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("Count total error", e);
            return 0;
        }
    }

    private boolean isValidName(String name) {
        return name != null && name.trim().matches("^[\\p{L}0-9][\\p{L}0-9 .'-]{0,49}$");
    }
}