package com.example.demo.classes.minorClasses.dao;

import com.example.demo.classes.abstractClasses.service.ClassesService;
import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.entity.Enums.Sessions;
import com.example.demo.subject.minorSubject.model.MinorSubjects;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import com.example.demo.user.deputyStaff.service.DeputyStaffsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.IsoFields;
import java.util.*;

@Repository
@Transactional
public class MinorClassesDAOImpl implements MinorClassesDAO {

    private final DeputyStaffsService deputyStaffsService;

    // Trong MinorClassesDAOImpl.java
    @Override
    public long totalMinorClassesInMyCampus() {
        DeputyStaffs deputy = deputyStaffsService.getDeputyStaff();
        if (deputy == null || deputy.getCampus() == null) return 0L;

        String campusId = deputy.getCampus().getCampusId();

        return entityManager.createQuery(
                        "SELECT COUNT(c) FROM MinorClasses c " +
                                "WHERE c.creator.campus.campusId = :campusId", Long.class)
                .setParameter("campusId", campusId)
                .getSingleResult();
    }

    @Override
    public long totalSlotsInMyCampus() {
        DeputyStaffs deputy = deputyStaffsService.getDeputyStaff();
        if (deputy == null || deputy.getCampus() == null) return 0L;

        String campusId = deputy.getCampus().getCampusId();

        return entityManager.createQuery(
                        "SELECT COALESCE(SUM(c.slotQuantity), 0) FROM MinorClasses c " +
                                "WHERE c.creator.campus.campusId = :campusId", Long.class)
                .setParameter("campusId", campusId)
                .getSingleResult();
    }

    @Override
    public long totalOccupiedSlotsInMyCampus() {
        DeputyStaffs deputy = deputyStaffsService.getDeputyStaff();
        if (deputy == null || deputy.getCampus() == null) return 0L;

        String campusId = deputy.getCampus().getCampusId();

        return entityManager.createQuery(
                        "SELECT COUNT(smc) FROM Students_MinorClasses smc " +
                                "JOIN smc.minorClass mc " +
                                "WHERE mc.creator.campus.campusId = :campusId", Long.class)
                .setParameter("campusId", campusId)
                .getSingleResult();
    }

    @Override
    public double averageClassSizeInMyCampus() {
        DeputyStaffs deputy = deputyStaffsService.getDeputyStaff();
        if (deputy == null || deputy.getCampus() == null) return 0.0;

        String campusId = deputy.getCampus().getCampusId();

        try {
            Object[] result = entityManager.createQuery(
                            "SELECT COUNT(smc), COUNT(DISTINCT mc) " +
                                    "FROM Students_MinorClasses smc " +
                                    "JOIN smc.minorClass mc " +
                                    "WHERE mc.creator.campus.campusId = :campusId", Object[].class)
                    .setParameter("campusId", campusId)
                    .getSingleResult();

            long enrolled = result[0] != null ? ((Number) result[0]).longValue() : 0L;
            long classes = result[1] != null ? ((Number) result[1]).longValue() : 0L;

            return classes == 0 ? 0.0 : Math.round((double) enrolled / classes * 10) / 10.0;
        } catch (Exception e) {
            return 0.0;
        }
    }

    @Override
    public List<Object[]> minorClassesBySemesterInMyCampus() {
        DeputyStaffs deputy = deputyStaffsService.getDeputyStaff();
        if (deputy == null || deputy.getCampus() == null) return List.of();

        String campusId = deputy.getCampus().getCampusId();

        return entityManager.createQuery(
                        "SELECT c.session, COUNT(c), COALESCE(SUM(c.slotQuantity), 0) " +
                                "FROM MinorClasses c " +
                                "WHERE c.creator.campus.campusId = :campusId " +
                                "GROUP BY c.session ORDER BY c.session DESC", Object[].class)
                .setParameter("campusId", campusId)
                .getResultList();
    }

    @Override
    public List<Object[]> top5LargestClassesInMyCampus() {
        DeputyStaffs deputy = deputyStaffsService.getDeputyStaff();
        if (deputy == null || deputy.getCampus() == null) return List.of();

        String campusId = deputy.getCampus().getCampusId();

        return entityManager.createQuery(
                        "SELECT c.nameClass, c.slotQuantity, COUNT(smc.student) " +
                                "FROM MinorClasses c LEFT JOIN Students_MinorClasses smc ON smc.minorClass = c " +
                                "WHERE c.creator.campus.campusId = :campusId " +
                                "GROUP BY c.classId, c.nameClass, c.slotQuantity " +
                                "ORDER BY COUNT(smc.student.id) DESC", Object[].class)
                .setMaxResults(5)
                .setParameter("campusId", campusId)
                .getResultList();
    }

    @Override
    public List<Object[]> minorClassesBySubjectInMyCampus() {
        DeputyStaffs deputy = deputyStaffsService.getDeputyStaff();
        if (deputy == null || deputy.getCampus() == null) return List.of();

        String campusId = deputy.getCampus().getCampusId();

        return entityManager.createQuery(
                        "SELECT COALESCE(s.subjectName, 'No Subject'), COUNT(c), COALESCE(SUM(c.slotQuantity), 0) " +
                                "FROM MinorClasses c LEFT JOIN c.minorSubject s " +
                                "WHERE c.creator.campus.campusId = :campusId " +
                                "GROUP BY s.subjectId, s.subjectName " +
                                "ORDER BY COUNT(c) DESC", Object[].class)
                .setParameter("campusId", campusId)
                .getResultList();
    }

    @Override
    public long unscheduledMinorClassesCount() {
        DeputyStaffs deputy = deputyStaffsService.getDeputyStaff();
        if (deputy == null || deputy.getCampus() == null) return 0L;

        String campusId = deputy.getCampus().getCampusId();
        LocalDate today = LocalDate.now();
        int week = today.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        int year = today.getYear();

        return entityManager.createQuery(
                        "SELECT COUNT(c) FROM MinorClasses c " +
                                "WHERE c.creator.campus.campusId = :campusId " +
                                "AND c.classId NOT IN (" +
                                "   SELECT t.minorClass.classId FROM MinorTimetable t " +
                                "   WHERE t.weekOfYear = :week AND t.year = :year" +
                                ")", Long.class)
                .setParameter("campusId", campusId)
                .setParameter("week", week)
                .setParameter("year", year)
                .getSingleResult();
    }

    private final ClassesService classesService;

    @PersistenceContext
    private EntityManager entityManager;

    public MinorClassesDAOImpl(DeputyStaffsService deputyStaffsService, ClassesService classesService) {
        this.deputyStaffsService = deputyStaffsService;
        this.classesService = classesService;
    }

    @Override
    public List<MinorClasses> getClasses() {
        return entityManager.createQuery("SELECT c FROM MinorClasses c", MinorClasses.class).getResultList();
    }

    @Override
    public MinorClasses getClassById(String id) {
        return entityManager.find(MinorClasses.class, id);
    }

    @Override
    public MinorClasses getClassByName(String name) {
        if (name == null || name.isBlank()) return null;
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
        if (c == null) throw new IllegalArgumentException("Class object cannot be null");
        c.setCreatedAt(LocalDateTime.now());
        entityManager.persist(c);
    }

    @Override
    public MinorClasses editClass(String id, MinorClasses classObj) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Class ID is required.");
        if (classObj == null) throw new IllegalArgumentException("Class data cannot be null.");

        MinorClasses existing = entityManager.find(MinorClasses.class, id);
        if (existing == null) {
            throw new IllegalArgumentException("Class not found with ID: " + id);
        }

        Map<String, String> errors = validateClass(classObj, id);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + errors);
        }

        existing.setNameClass(classObj.getNameClass());
        existing.setSlotQuantity(classObj.getSlotQuantity());
        existing.setSession(classObj.getSession());
        existing.setMinorSubject(classObj.getMinorSubject());
        existing.setSession(classObj.getSession());
        return entityManager.merge(existing);
    }

    @Override
    public void deleteClass(String id) {
        MinorClasses c = getClassById(id);
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
            classId = prefix + year + date + random.nextInt(10);
        } while (getClassById(classId) != null);
        return classId;
    }

    @Override
    public Map<String, String> validateClass(MinorClasses classObj, String excludeId) {
        Map<String, String> errors = new LinkedHashMap<>();

        // 1. Class name
        if (classObj.getNameClass() == null || classObj.getNameClass().trim().isEmpty()) {
            errors.put("nameClass", "Class name is required.");
        } else {
            String nameClass = classObj.getNameClass().trim();
            if (!isValidName(nameClass)) {
                errors.put("nameClass", "Class name contains invalid characters. Only letters, numbers, spaces, and . - ' are allowed.");
            } else {
                classObj.setNameClass(nameClass);

                // CHECK TRÙNG TÊN TOÀN HỆ THỐNG (Major + Minor + ...)
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

        // 3. Subject
        if (classObj.getMinorSubject() == null || classObj.getMinorSubject().getSubjectId() == null || classObj.getMinorSubject().getSubjectId().trim().isEmpty()) {
            errors.put("minorSubject", "Please select a subject.");
        } else {
            MinorSubjects subject = entityManager.find(MinorSubjects.class, classObj.getMinorSubject().getSubjectId());
            if (subject == null) {
                errors.put("minorSubject", "Selected subject does not exist.");
            } else {
                classObj.setMinorSubject(subject);
            }
        }

        // 4. Session (học kỳ) - bắt buộc
        if (classObj.getSession() == null) {
            errors.put("session", "Please select a semester.");
        }

        return errors;
    }

    @Override
    public List<MinorClasses> searchClassesByCampus(String searchType, String keyword, int firstResult, int pageSize, String campusId) {
        if (campusId == null || campusId.isBlank()) return List.of();

        String jpql = "SELECT c FROM MinorClasses c JOIN FETCH c.creator LEFT JOIN FETCH c.minorSubject " +
                "WHERE c.creator.campus.campusId = :campusId";

        if ("name".equals(searchType)) {
            jpql += " AND LOWER(c.nameClass) LIKE LOWER(:keyword)";
        } else {
            jpql += " AND c.classId LIKE :keyword";
        }

        return entityManager.createQuery(jpql, MinorClasses.class)
                .setParameter("campusId", campusId)
                .setParameter("keyword", "%" + keyword + "%")
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public long countSearchResultsByCampus(String searchType, String keyword, String campusId) {
        if (campusId == null || campusId.isBlank()) return 0;

        String jpql = "SELECT COUNT(c) FROM MinorClasses c WHERE c.creator.campus.campusId = :campusId";

        if ("name".equals(searchType)) {
            jpql += " AND LOWER(c.nameClass) LIKE LOWER(:keyword)";
        } else {
            jpql += " AND c.classId LIKE :keyword";
        }

        return entityManager.createQuery(jpql, Long.class)
                .setParameter("campusId", campusId)
                .setParameter("keyword", "%" + keyword + "%")
                .getSingleResult();
    }

    @Override
    public List<MinorClasses> getPaginatedClassesByCampus(int firstResult, int pageSize, String campusId) {
        if (campusId == null || campusId.isBlank()) return List.of();

        return entityManager.createQuery(
                        "SELECT c FROM MinorClasses c JOIN FETCH c.creator LEFT JOIN FETCH c.minorSubject " +
                                "WHERE c.creator.campus.campusId = :campusId",
                        MinorClasses.class)
                .setParameter("campusId", campusId)
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public long numberOfClassesByCampus(String campusId) {
        if (campusId == null || campusId.isBlank()) return 0;

        return entityManager.createQuery(
                        "SELECT COUNT(c) FROM MinorClasses c WHERE c.creator.campus.campusId = :campusId",
                        Long.class)
                .setParameter("campusId", campusId)
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
        return name != null && name.trim().matches("^[\\p{L}0-9][\\p{L}0-9 .'-]{0,49}$");
    }
}