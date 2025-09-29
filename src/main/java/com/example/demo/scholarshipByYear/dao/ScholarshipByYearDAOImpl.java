package com.example.demo.scholarshipByYear.dao;

import com.example.demo.admin.model.Admins;
import com.example.demo.admin.service.AdminsService;
import com.example.demo.entity.Enums.ActivityStatus;
import com.example.demo.scholarshipByYear.model.ScholarshipByYear;
import com.example.demo.scholarshipByYear.model.ScholarshipByYearId;
import com.example.demo.scholarship.model.Scholarships;
import com.example.demo.scholarship.service.ScholarshipsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
public class ScholarshipByYearDAOImpl implements ScholarshipByYearDAO {

    private final ScholarshipsService scholarshipsService;
    private final AdminsService adminsService;

    public ScholarshipByYearDAOImpl(ScholarshipsService scholarshipsService, AdminsService adminsService) {
        this.scholarshipsService = scholarshipsService;
        this.adminsService = adminsService;
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Long getCountScholarshipByYear(Integer admissionYear) {
        if (admissionYear == null) {
            return 0L;
        }
        return entityManager.createQuery(
                        "SELECT COUNT(sby) FROM ScholarshipByYear sby WHERE sby.id.admissionYear = :admissionYear",
                        Long.class)
                .setParameter("admissionYear", admissionYear)
                .getSingleResult();
    }

    @Override
    public ScholarshipByYear getScholarshipByYear(String scholarshipId, Integer admissionYear) {
        if (scholarshipId == null || admissionYear == null) {
            return null;
        }
        return entityManager.createQuery(
                        "SELECT sby FROM ScholarshipByYear sby " +
                                "WHERE sby.id.scholarshipId = :scholarshipId AND sby.id.admissionYear = :admissionYear",
                        ScholarshipByYear.class)
                .setParameter("scholarshipId", scholarshipId)
                .setParameter("admissionYear", admissionYear)
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public void updateScholarshipByYear(String scholarshipId, Integer admissionYear, Double amount, Double discountPercentage) {
        if (amount == null && discountPercentage == null) {
            return;
        }

        Scholarships scholarship = scholarshipsService.getScholarshipById(scholarshipId);
        if (scholarship == null) {
            throw new IllegalArgumentException("Scholarship with ID " + scholarshipId + " not found");
        }

        ScholarshipByYear scholarshipByYear = getScholarshipByYear(scholarshipId, admissionYear);
        if (scholarshipByYear == null) {
            scholarshipByYear = new ScholarshipByYear();
            scholarshipByYear.setId(new ScholarshipByYearId(scholarshipId, admissionYear));
            scholarshipByYear.setScholarship(scholarship);
        }

        scholarshipByYear.setAmount(amount != null ? amount : 0.0);
        scholarshipByYear.setDiscountPercentage(discountPercentage);
        scholarshipByYear.setStatus(ActivityStatus.ACTIVATED);
        Admins creator = adminsService.getAdmin();
        scholarshipByYear.setCreator(creator);
        scholarshipByYear.setCreatedAt(LocalDateTime.now());
        saveOrUpdate(scholarshipByYear);
    }

    @Override
    public List<ScholarshipByYear> getScholarshipsByYear(Integer admissionYear) {
        if (admissionYear == null) {
            return List.of();
        }
        return entityManager.createQuery(
                        "SELECT sby FROM ScholarshipByYear sby LEFT JOIN FETCH sby.scholarship LEFT JOIN FETCH sby.creator WHERE sby.id.admissionYear = :admissionYear",
                        ScholarshipByYear.class)
                .setParameter("admissionYear", admissionYear)
                .getResultList();
    }

    @Override
    public List<Integer> getAllAdmissionYears() {
        return entityManager.createQuery(
                        "SELECT DISTINCT sby.id.admissionYear FROM ScholarshipByYear sby ORDER BY sby.id.admissionYear DESC",
                        Integer.class)
                .getResultList();
    }

    @Override
    public void saveOrUpdate(ScholarshipByYear scholarshipByYear) {
        entityManager.merge(scholarshipByYear);
    }

    @Override
    public void finalizeScholarshipContracts(Integer admissionYear) {
        if (admissionYear == null) {
            throw new IllegalArgumentException("Admission year cannot be null");
        }
        entityManager.createQuery(
                        "UPDATE ScholarshipByYear sby SET sby.status = :status " +
                                "WHERE sby.id.admissionYear = :admissionYear " +
                                "AND (sby.amount IS NOT NULL OR sby.discountPercentage IS NOT NULL)")
                .setParameter("status", ActivityStatus.ACTIVATED)
                .setParameter("admissionYear", admissionYear)
                .executeUpdate();
    }
}