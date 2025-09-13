package com.example.demo.scholarship.dao;

import com.example.demo.admin.model.Admins;
import com.example.demo.admin.service.AdminsService;
import com.example.demo.entity.Enums.ActivityStatus;
import com.example.demo.scholarship.model.ScholarshipByYear;
import com.example.demo.scholarship.model.ScholarshipByYearId;
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

    @Override
    public void updateScholarshipByYear(String scholarshipId, Integer admissionYear, Double amount, Double discountPercentage) {
        if (amount == null && discountPercentage == null) {
            return; // Không cập nhật nếu cả hai trường đều null
        }

        Scholarships scholarship = scholarshipsService.getScholarshipById(scholarshipId);
        if (scholarship == null) {
            throw new IllegalArgumentException("Scholarship with ID " + scholarshipId + " not found");
        }

        ScholarshipByYear scholarshipByYear = new ScholarshipByYear();
        scholarshipByYear.setId(new ScholarshipByYearId(scholarshipId, admissionYear));
        scholarshipByYear.setScholarship(scholarship);
        scholarshipByYear.setAmount(amount != null ? amount : 0.0);
        scholarshipByYear.setDiscountPercentage(discountPercentage);
        scholarshipByYear.setStatus(ActivityStatus.ACTIVATED); // Sửa thành ACTIVE để đồng bộ với enum

        // Giả định người tạo là admin đang đăng nhập
        Admins creator = adminsService.getAdmin();
        scholarshipByYear.setCreator(creator);
        scholarshipByYear.setCreatedAt(LocalDateTime.now());
        saveOrUpdate(scholarshipByYear);
    }

    @PersistenceContext
    private EntityManager entityManager;

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
        return entityManager.createQuery("SELECT DISTINCT sby.id.admissionYear FROM ScholarshipByYear sby ORDER BY sby.id.admissionYear DESC", Integer.class)
                .getResultList();
    }

    @Override
    public void saveOrUpdate(ScholarshipByYear scholarshipByYear) {
        entityManager.merge(scholarshipByYear);
    }
}