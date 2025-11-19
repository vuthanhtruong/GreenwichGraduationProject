package com.example.demo.classes.majorClasses.dao;

import com.example.demo.classes.abstractClasses.service.ClassesService;
import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.major.model.Majors;
import com.example.demo.subject.majorSubject.model.MajorSubjects;
import com.example.demo.user.staff.model.Staffs;
import com.example.demo.user.staff.service.StaffsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class MajorClassesDAOImpl implements MajorClassesDAO {

    private final StaffsService staffsService;

    // ==================== DASHBOARD LỚP HỌC CHÍNH NGÀNH - CHUẨN NHẤT CHO STAFF ====================

    @Override
    public long totalMajorClassesInMyMajor() {
        Staffs staff = staffsService.getStaff();
        if (staff == null || staff.getMajorManagement() == null || staff.getCampus() == null) return 0L;

        Majors major = staff.getMajorManagement();
        String campusId = staff.getCampus().getCampusId();

        return entityManager.createQuery(
                        "SELECT COUNT(c) FROM MajorClasses c " +
                                "WHERE c.creator.majorManagement = :major " +
                                "AND c.creator.campus.campusId = :campusId", Long.class)
                .setParameter("major", major)
                .setParameter("campusId", campusId)
                .getSingleResult();
    }

    @Override
    public long totalSlotsInMyMajor() {
        Staffs staff = staffsService.getStaff();
        if (staff == null || staff.getMajorManagement() == null || staff.getCampus() == null) return 0L;

        Majors major = staff.getMajorManagement();
        String campusId = staff.getCampus().getCampusId();

        return entityManager.createQuery(
                        "SELECT COALESCE(SUM(c.slotQuantity), 0) FROM MajorClasses c " +
                                "WHERE c.creator.majorManagement = :major " +
                                "AND c.creator.campus.campusId = :campusId", Long.class)
                .setParameter("major", major)
                .setParameter("campusId", campusId)
                .getSingleResult();
    }

    @Override
    public long totalOccupiedSlotsInMyMajor() {
        Staffs staff = staffsService.getStaff();
        if (staff == null || staff.getMajorManagement() == null || staff.getCampus() == null) return 0L;

        Majors major = staff.getMajorManagement();
        String campusId = staff.getCampus().getCampusId();

        return entityManager.createQuery(
                        "SELECT COUNT(smc) FROM Students_MajorClasses smc " +
                                "JOIN smc.majorClass mc " +
                                "WHERE mc.creator.majorManagement = :major " +
                                "AND mc.creator.campus.campusId = :campusId", Long.class)
                .setParameter("major", major)
                .setParameter("campusId", campusId)
                .getSingleResult();
    }

    @Override
    public double averageClassSizeInMyMajor() {
        Staffs staff = staffsService.getStaff();
        if (staff == null || staff.getMajorManagement() == null || staff.getCampus() == null) return 0.0;

        Majors major = staff.getMajorManagement();
        String campusId = staff.getCampus().getCampusId();

        try {
            Object[] result = entityManager.createQuery(
                            "SELECT COUNT(smc), COUNT(DISTINCT mc) " +
                                    "FROM Students_MajorClasses smc " +
                                    "JOIN smc.majorClass mc " +
                                    "WHERE mc.creator.majorManagement = :major " +
                                    "AND mc.creator.campus.campusId = :campusId", Object[].class)
                    .setParameter("major", major)
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
    public List<Object[]> majorClassesBySemesterInMyMajor() {
        Staffs staff = staffsService.getStaff();
        if (staff == null || staff.getMajorManagement() == null || staff.getCampus() == null) return List.of();

        Majors major = staff.getMajorManagement();
        String campusId = staff.getCampus().getCampusId();

        return entityManager.createQuery(
                        "SELECT c.session, COUNT(c), COALESCE(SUM(c.slotQuantity), 0) " +
                                "FROM MajorClasses c " +
                                "WHERE c.creator.majorManagement = :major " +
                                "AND c.creator.campus.campusId = :campusId " +
                                "GROUP BY c.session ORDER BY c.session DESC", Object[].class)
                .setParameter("major", major)
                .setParameter("campusId", campusId)
                .getResultList();
    }

    @Override
    public List<Object[]> top5LargestClassesInMyMajor() {
        Staffs staff = staffsService.getStaff();
        if (staff == null || staff.getMajorManagement() == null || staff.getCampus() == null) return List.of();

        Majors major = staff.getMajorManagement();
        String campusId = staff.getCampus().getCampusId();

        return entityManager.createQuery(
                        "SELECT c.nameClass, c.slotQuantity, COUNT(smc.student) " +
                                "FROM MajorClasses c LEFT JOIN Students_MajorClasses smc ON smc.majorClass = c " +
                                "WHERE c.creator.majorManagement = :major " +
                                "AND c.creator.campus.campusId = :campusId " +
                                "GROUP BY c.classId, c.nameClass, c.slotQuantity " +
                                "ORDER BY COUNT(smc.student.id) DESC", Object[].class)
                .setMaxResults(5)
                .setParameter("major", major)
                .setParameter("campusId", campusId)
                .getResultList();
    }

    @Override
    public List<Object[]> majorClassesBySubjectInMyMajor() {
        Staffs staff = staffsService.getStaff();
        if (staff == null || staff.getMajorManagement() == null || staff.getCampus() == null) return List.of();

        Majors major = staff.getMajorManagement();
        String campusId = staff.getCampus().getCampusId();

        return entityManager.createQuery(
                        "SELECT COALESCE(s.subjectName, 'No Subject'), COUNT(c), COALESCE(SUM(c.slotQuantity), 0) " +
                                "FROM MajorClasses c LEFT JOIN c.subject s " +
                                "WHERE c.creator.majorManagement = :major " +
                                "AND c.creator.campus.campusId = :campusId " +
                                "GROUP BY s.subjectId, s.subjectName " +
                                "ORDER BY COUNT(c) DESC", Object[].class)
                .setParameter("major", major)
                .setParameter("campusId", campusId)
                .getResultList();
    }

    private final ClassesService classesService;

    @PersistenceContext
    private EntityManager entityManager;

    public MajorClassesDAOImpl(StaffsService staffsService, ClassesService classesService) {
        this.staffsService = staffsService;
        this.classesService = classesService;
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
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Class ID is required.");
        }
        if (classObj == null) {
            throw new IllegalArgumentException("Class data cannot be null.");
        }

        MajorClasses existing = entityManager.find(MajorClasses.class, id);
        if (existing == null) {
            throw new IllegalArgumentException("Class not found with ID: " + id);
        }

        Map<String, String> errors = validateClass(classObj, id);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + errors);
        }

        existing.setNameClass(classObj.getNameClass());
        existing.setSlotQuantity(classObj.getSlotQuantity());
        existing.setSubject(classObj.getSubject());
        existing.setSession(classObj.getSession());
        return entityManager.merge(existing);
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
    public Map<String, String> validateClass(MajorClasses classObj, String excludeId) {
        Map<String, String> errors = new LinkedHashMap<>();

        // 1. Class name - required + format
        if (classObj.getNameClass() == null || classObj.getNameClass().trim().isEmpty()) {
            errors.put("nameClass", "Class name is required.");
        } else {
            String nameClass = classObj.getNameClass().trim();
            if (!isValidName(nameClass)) {
                errors.put("nameClass", "Class name contains invalid characters. Only letters, numbers, spaces, and . - ' are allowed.");
            } else {
                classObj.setNameClass(nameClass); // normalize

                // 2. CHECK DUPLICATE NAME GLOBALLY (across all class types)
                boolean exists = (excludeId != null && !excludeId.isBlank())
                        ? classesService.existsByNameClassExcludingId(nameClass, excludeId)
                        : classesService.existsByNameClass(nameClass);

                if (exists) {
                    errors.put("nameClass", "Class name \"" + nameClass + "\" is already in use.");
                }
            }
        }

        // 3. Slot quantity
        if (classObj.getSlotQuantity() == null || classObj.getSlotQuantity() <= 0) {
            errors.put("slotQuantity", "Total slots must be greater than 0.");
        }

        // 4. Subject
        if (classObj.getSubject() == null || classObj.getSubject().getSubjectId() == null || classObj.getSubject().getSubjectId().trim().isEmpty()) {
            errors.put("subject", "Please select a subject.");
        } else {
            MajorSubjects subject = entityManager.find(MajorSubjects.class, classObj.getSubject().getSubjectId());
            if (subject == null) {
                errors.put("subject", "Selected subject does not exist.");
            } else {
                classObj.setSubject(subject); // attach managed entity
            }
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