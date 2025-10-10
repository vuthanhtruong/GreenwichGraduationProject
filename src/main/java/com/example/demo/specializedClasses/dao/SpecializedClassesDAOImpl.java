package com.example.demo.specializedClasses.dao;

import com.example.demo.specializedClasses.model.SpecializedClasses;
import com.example.demo.Specialization.model.Specialization;
import com.example.demo.major.model.Majors;
import com.example.demo.staff.service.StaffsService;
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
public class SpecializedClassesDAOImpl implements SpecializedClassesDAO {

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
            return List.of();
        }
        return entityManager.createQuery("SELECT c FROM SpecializedClasses c JOIN c.specialization s WHERE s.major = :major", SpecializedClasses.class)
                .setParameter("major", major)
                .getResultList();
    }

    @Override
    public List<SpecializedClasses> getClasses() {
        return entityManager.createQuery("FROM SpecializedClasses", SpecializedClasses.class).getResultList();
    }

    @Override
    public SpecializedClasses getClassById(String id) {
        return entityManager.find(SpecializedClasses.class, id);
    }

    @Override
    public SpecializedClasses getClassByName(String name) {
        try {
            return entityManager.createQuery("SELECT c FROM SpecializedClasses c WHERE c.nameClass = :name", SpecializedClasses.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void addClass(SpecializedClasses c) {
        c.setCreator(staffsService.getStaff());
        c.setCreatedAt(LocalDateTime.now());
        entityManager.persist(c);
    }

    @Override
    public SpecializedClasses editClass(String id, SpecializedClasses classObj) {
        if (classObj == null || id == null) {
            throw new IllegalArgumentException("Class object or ID cannot be null");
        }

        SpecializedClasses existingClass = entityManager.find(SpecializedClasses.class, id);
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
        existingClass.setSpecialization(classObj.getSpecialization());
        if (classObj.getCreator() != null) existingClass.setCreator(classObj.getCreator());
        if (classObj.getCreatedAt() != null) existingClass.setCreatedAt(classObj.getCreatedAt());

        return entityManager.merge(existingClass);
    }

    @Override
    public void deleteClass(String id) {
        SpecializedClasses c = entityManager.find(SpecializedClasses.class, id);
        if (c == null) {
            throw new IllegalArgumentException("Class with ID " + id + " not found");
        }
        entityManager.remove(c);
    }

    @Override
    public String generateUniqueClassId(String specializationId, LocalDateTime createdDate) {
        String prefix;
        switch (specializationId) {
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

        if (classObj.getSpecialization() == null || classObj.getSpecialization().getSpecializationId() == null) {
            errors.add("Specialization is required.");
        } else {
            Specialization specialization = entityManager.find(Specialization.class, classObj.getSpecialization().getSpecializationId());
            if (specialization == null) {
                errors.add("Invalid specialization selected.");
            } else {
                classObj.setSpecialization(specialization); // Ensure managed entity
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
        String queryString = "SELECT c FROM SpecializedClasses c JOIN FETCH c.creator JOIN c.specialization s WHERE s.major = :major";
        if ("name".equals(searchType)) {
            queryString += " AND LOWER(c.nameClass) LIKE LOWER(:keyword)";
        } else {
            queryString += " AND c.classId LIKE :keyword";
        }
        return entityManager.createQuery(queryString, SpecializedClasses.class)
                .setParameter("major", major)
                .setParameter("keyword", "%" + keyword + "%")
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public long countSearchResults(String searchType, String keyword, Majors major) {
        String queryString = "SELECT COUNT(c) FROM SpecializedClasses c JOIN c.specialization s WHERE s.major = :major";
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
    public List<SpecializedClasses> getPaginatedClasses(int firstResult, int pageSize, Majors major) {
        return entityManager.createQuery("SELECT c FROM SpecializedClasses c JOIN FETCH c.creator JOIN c.specialization s WHERE s.major = :major", SpecializedClasses.class)
                .setParameter("major", major)
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public long numberOfClasses(Majors major) {
        return entityManager.createQuery("SELECT COUNT(c) FROM SpecializedClasses c JOIN c.specialization s WHERE s.major = :major", Long.class)
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