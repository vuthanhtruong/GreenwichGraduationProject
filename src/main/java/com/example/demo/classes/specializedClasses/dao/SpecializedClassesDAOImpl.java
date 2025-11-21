package com.example.demo.classes.specializedClasses.dao;

import com.example.demo.classes.abstractClasses.service.ClassesService;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class SpecializedClassesDAOImpl implements SpecializedClassesDAO {

    private static final Logger log = LoggerFactory.getLogger(SpecializedClassesDAOImpl.class);

    private final ClassesService classesService;

    @PersistenceContext
    private EntityManager entityManager;

    public SpecializedClassesDAOImpl(ClassesService classesService) {
        this.classesService = classesService;
    }

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
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Class ID is required.");
        if (classObj == null) throw new IllegalArgumentException("Class data cannot be null.");

        SpecializedClasses existing = getClassById(id);
        if (existing == null) {
            throw new IllegalArgumentException("Specialized class not found: " + id);
        }

        Map<String, String> errors = validateClass(classObj, id);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + errors);
        }

        existing.setNameClass(classObj.getNameClass());
        existing.setSlotQuantity(classObj.getSlotQuantity());
        existing.setSession(classObj.getSession());
        existing.setSpecializedSubject(classObj.getSpecializedSubject());

        SpecializedClasses updated = entityManager.merge(existing);
        log.info("Updated specialized class: {} → {}", id, classObj.getNameClass());
        return updated;
    }

    @Override
    @Transactional
    public void deleteClass(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Class ID is required.");
        }

        SpecializedClasses specClass = getClassById(id);
        if (specClass == null) {
            throw new IllegalArgumentException("Class with ID " + id + " not found");
        }

        // Xóa bảng con chuyên sâu (nếu hệ thống bạn có entity Students_SpecializedClasses)
        entityManager.createQuery(
                        "DELETE FROM Students_SpecializedClasses ssc WHERE ssc.specializedClass.classId = :id")
                .setParameter("id", id)
                .executeUpdate();

        // Xóa khỏi bảng đăng ký lớp chung (nếu lớp chuyên sâu cũng nằm trong students_classes)
        entityManager.createQuery(
                        "DELETE FROM Students_Classes sc WHERE sc.classEntity.classId = :id")
                .setParameter("id", id)
                .executeUpdate();

        // Cuối cùng xóa lớp chính
        entityManager.remove(specClass);

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
    public Map<String, String> validateClass(SpecializedClasses classObj, String excludeId) {
        Map<String, String> errors = new LinkedHashMap<>();

        // 1. Name
        if (classObj.getNameClass() == null || classObj.getNameClass().trim().isEmpty()) {
            errors.put("nameClass", "Class name is required.");
        } else {
            String nameClass = classObj.getNameClass().trim();
            if (!isValidName(nameClass)) {
                errors.put("nameClass", "Class name contains invalid characters.");
            } else {
                classObj.setNameClass(nameClass);

                // CHECK TRÙNG TÊN TOÀN HỆ THỐNG
                boolean exists = (excludeId != null && !excludeId.isBlank())
                        ? classesService.existsByNameClassExcludingId(nameClass, excludeId)
                        : classesService.existsByNameClass(nameClass);

                if (exists) {
                    errors.put("nameClass", "Class name \"" + nameClass + "\" is already in use.");
                }
            }
        }

        // 2. Slot quantity
        if (classObj.getSlotQuantity() == null || classObj.getSlotQuantity() <= 0) {
            errors.put("slotQuantity", "Total slots must be greater than 0.");
        }

        // 3. Specialized Subject
        if (classObj.getSpecializedSubject() == null || classObj.getSpecializedSubject().getSubjectId() == null) {
            errors.put("specializedSubject", "Please select a specialized subject.");
        } else {
            SpecializedSubject subject = entityManager.find(SpecializedSubject.class,
                    classObj.getSpecializedSubject().getSubjectId());
            if (subject == null) {
                errors.put("specializedSubject", "Selected subject does not exist.");
            } else {
                classObj.setSpecializedSubject(subject);
            }
        }

        // 4. Session (nếu bạn vẫn muốn bắt buộc)
        if (classObj.getSession() == null) {
            errors.put("session", "Please select a semester.");
        }

        return errors;
    }

    @Override
    public List<SpecializedClasses> searchClassesByCampus(String searchType,
                                                          String keyword,
                                                          int firstResult,
                                                          int pageSize,
                                                          Majors major,
                                                          String campusId) {
        if (major == null || campusId == null || campusId.isBlank()) {
            log.warn("Search aborted: major or campusId is null/blank. major={}, campusId={}", major, campusId);
            return List.of();
        }

        // Chuẩn hoá searchType (default = "name")
        if (searchType == null || searchType.isBlank()) {
            searchType = "name";
        }

        // Chuẩn hoá keyword
        if (keyword == null) {
            keyword = "";
        }
        String likeKeyword = "%" + keyword.trim() + "%";

        String jpql =
                "SELECT c FROM SpecializedClasses c " +
                        "WHERE c.specializedSubject.specialization.major = :major " +
                        "AND c.creator.campus.campusId = :campusId ";

        if ("name".equalsIgnoreCase(searchType)) {
            jpql += "AND LOWER(c.nameClass) LIKE LOWER(:keyword) ";
        } else {
            // Mặc định nhánh còn lại là search theo classId
            jpql += "AND c.classId LIKE :keyword ";
        }

        try {
            List<SpecializedClasses> result = entityManager.createQuery(jpql, SpecializedClasses.class)
                    .setParameter("major", major)
                    .setParameter("campusId", campusId)
                    .setParameter("keyword", likeKeyword)
                    .setFirstResult(firstResult)
                    .setMaxResults(pageSize)
                    .getResultList();

            log.debug("Search found {} classes (type={}, keyword={}, majorId={}, campusId={})",
                    result.size(), searchType, keyword,
                    major.getMajorId(), campusId);

            return result;
        } catch (Exception e) {
            log.error("Search error (type={}, keyword={}, majorId={}, campusId={})",
                    searchType, keyword, major.getMajorId(), campusId, e);
            return List.of();
        }
    }


    @Override
    public long countSearchResultsByCampus(String searchType,
                                           String keyword,
                                           Majors major,
                                           String campusId) {
        if (major == null || campusId == null || campusId.isBlank()) {
            log.warn("Count search aborted: major or campusId is null/blank. major={}, campusId={}", major, campusId);
            return 0;
        }

        // Chuẩn hoá searchType (default = "name")
        if (searchType == null || searchType.isBlank()) {
            searchType = "name";
        }

        // Chuẩn hoá keyword
        if (keyword == null) {
            keyword = "";
        }
        String likeKeyword = "%" + keyword.trim() + "%";

        String jpql =
                "SELECT COUNT(c) FROM SpecializedClasses c " +
                        "WHERE c.specializedSubject.specialization.major = :major " +
                        "AND c.creator.campus.campusId = :campusId ";

        if ("name".equalsIgnoreCase(searchType)) {
            jpql += "AND LOWER(c.nameClass) LIKE LOWER(:keyword) ";
        } else {
            jpql += "AND c.classId LIKE :keyword ";
        }

        try {
            Long count = entityManager.createQuery(jpql, Long.class)
                    .setParameter("major", major)
                    .setParameter("campusId", campusId)
                    .setParameter("keyword", likeKeyword)
                    .getSingleResult();

            long result = (count != null) ? count : 0L;

            log.debug("Count search results = {} (type={}, keyword={}, majorId={}, campusId={})",
                    result, searchType, keyword, major.getMajorId(), campusId);

            return result;
        } catch (Exception e) {
            log.error("Count search error (type={}, keyword={}, majorId={}, campusId={})",
                    searchType, keyword, major.getMajorId(), campusId, e);
            return 0;
        }
    }


    @Override
    public List<SpecializedClasses> getPaginatedClassesByCampus(int firstResult, int pageSize, Majors major, String campusId) {
        if (major == null || campusId == null || campusId.isBlank()) return List.of();

        try {
            return entityManager.createQuery(
                            "SELECT c FROM SpecializedClasses c " +
                                    "WHERE c.specializedSubject.specialization.major = :major AND c.creator.campus.campusId = :campusId",
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
                            "SELECT COUNT(c) FROM SpecializedClasses c " +
                                    "WHERE c.specializedSubject.specialization.major = :major AND c.creator.campus.campusId = :campusId",
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