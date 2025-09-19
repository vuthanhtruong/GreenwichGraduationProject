package com.example.demo.scholarship.dao;

import com.example.demo.admin.model.Admins;
import com.example.demo.admin.service.AdminsService;
import com.example.demo.scholarship.model.Scholarships;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Transactional
public class ScholarshipsDAOImpl implements ScholarshipsDAO {
    @Override
    public List<Scholarships> getScScholarshipsByName(String scholarshipName) {
        return entityManager.createQuery("from Scholarships s where s.typeName=:scholarshipName").setParameter("scholarshipName", scholarshipName).getResultList();
    }

    @PersistenceContext
    private EntityManager entityManager;
    private final AdminsService adminsService;

    public ScholarshipsDAOImpl(AdminsService adminsService) {
        this.adminsService = adminsService;
    }

    @Override
    public boolean existsScholarshipById(String scholarshipId) {
        return entityManager.find(Scholarships.class, scholarshipId) != null;
    }

    @Override
    public Scholarships getScholarshipById(String id) {
        if (id == null) {
            return null;
        }
        try {
            return entityManager.find(Scholarships.class, id);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public List<Scholarships> getAllScholarships() {
        return entityManager.createQuery("SELECT s FROM Scholarships s LEFT JOIN FETCH s.creator", Scholarships.class)
                .getResultList()
                .stream()
                .sorted(Comparator.comparing(Scholarships::getTypeName, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(Scholarships::getAwardDate, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(Scholarships::getScholarshipId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Scholarships> getPaginatedScholarships(int firstResult, int pageSize) {
        return entityManager.createQuery("SELECT s FROM Scholarships s LEFT JOIN FETCH s.creator", Scholarships.class)
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public long numberOfScholarships() {
        return entityManager.createQuery("SELECT COUNT(s) FROM Scholarships s", Long.class)
                .getSingleResult();
    }

    @Override
    public List<Scholarships> searchScholarships(String searchType, String keyword, int firstResult, int pageSize) {
        if (keyword == null || keyword.trim().isEmpty() || searchType == null) {
            return List.of();
        }
        String queryString;
        if ("name".equals(searchType)) {
            queryString = "SELECT s FROM Scholarships s WHERE LOWER(s.typeName) LIKE LOWER(:keyword)";
        } else if ("id".equals(searchType)) {
            queryString = "SELECT s FROM Scholarships s WHERE s.scholarshipId LIKE :keyword";
        } else {
            return List.of();
        }
        return entityManager.createQuery(queryString, Scholarships.class)
                .setParameter("keyword", "%" + keyword.trim() + "%")
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public long countSearchResults(String searchType, String keyword) {
        if (keyword == null || keyword.trim().isEmpty() || searchType == null) {
            return 0;
        }
        String queryString;
        if ("name".equals(searchType)) {
            queryString = "SELECT COUNT(s) FROM Scholarships s WHERE LOWER(s.typeName) LIKE LOWER(:keyword)";
        } else if ("id".equals(searchType)) {
            queryString = "SELECT COUNT(s) FROM Scholarships s WHERE s.scholarshipId LIKE :keyword";
        } else {
            return 0;
        }
        return entityManager.createQuery(queryString, Long.class)
                .setParameter("keyword", "%" + keyword.trim() + "%")
                .getSingleResult();
    }

    @Override
    public Map<String, String> validateScholarship(Scholarships scholarship) {
        Map<String, String> errors = new HashMap<>();

        // Check for duplicate typeName
        if (!getScScholarshipsByName(scholarship.getTypeName()).isEmpty() || getScScholarshipsByName(scholarship.getTypeName()).size() != 0) {
            errors.put("typeName", "Scholarship type name is already exist.");
        }

        // Validate typeName
        if (scholarship.getTypeName() == null || scholarship.getTypeName().trim().isEmpty()) {
            errors.put("typeName", "Scholarship type name is required.");
        } else if (!scholarship.getTypeName().matches("^[\\p{L}\\s\\-']{2,100}$")) {
            errors.put("typeName", "Scholarship type name is invalid. Only letters, spaces, hyphens, and apostrophes are allowed (2-100 characters).");
        }

        return errors;
    }


    @Override
    public String generateUniqueScholarshipId() {
        String prefix = "SCH";
        String year = String.format("%02d", LocalDate.now().getYear() % 100);
        String date = String.format("%02d%02d", LocalDate.now().getMonthValue(), LocalDate.now().getDayOfMonth());
        String scholarshipId;
        SecureRandom random = new SecureRandom();
        do {
            String randomDigit = String.format("%03d", random.nextInt(1000));
            scholarshipId = prefix + year + date + randomDigit;
        } while (existsScholarshipById(scholarshipId));
        return scholarshipId;
    }

    @Override
    public Scholarships addScholarship(Scholarships scholarship) {
        Admins admin = adminsService.getAdmin();
        scholarship.setCreator(admin);
        scholarship.setCreatedAt(LocalDateTime.now());
        scholarship.setAwardDate(LocalDate.now());
        return entityManager.merge(scholarship);
    }
}