package com.example.demo.scholarship.service;

import com.example.demo.admin.model.Admins;
import com.example.demo.admin.service.AdminsService;
import com.example.demo.entity.Enums.ActivityStatus;
import com.example.demo.scholarship.dao.ScholarshipByYearDAO;
import com.example.demo.scholarship.model.ScholarshipByYear;
import com.example.demo.scholarship.model.ScholarshipByYearId;
import com.example.demo.scholarship.model.Scholarships;
import com.example.demo.scholarship.service.ScholarshipsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScholarshipByYearServiceImpl implements ScholarshipByYearService {
    @Override
    public void saveOrUpdate(ScholarshipByYear scholarshipByYear) {
        scholarshipByYearDAO.saveOrUpdate(scholarshipByYear);
    }

    private final ScholarshipByYearDAO scholarshipByYearDAO;
    private final ScholarshipsService scholarshipsService;
    private final AdminsService adminsService;

    @Autowired
    public ScholarshipByYearServiceImpl(ScholarshipByYearDAO scholarshipByYearDAO, ScholarshipsService scholarshipsService, AdminsService adminsService) {
        this.scholarshipByYearDAO = scholarshipByYearDAO;
        this.scholarshipsService = scholarshipsService;
        this.adminsService = adminsService;
    }

    @Override
    public List<ScholarshipByYear> getScholarshipsByYear(Integer admissionYear) {
        return scholarshipByYearDAO.getScholarshipsByYear(admissionYear);
    }

    @Override
    public List<Integer> getAllAdmissionYears() {
        return scholarshipByYearDAO.getAllAdmissionYears();
    }

    @Override
    public void updateScholarshipByYear(String scholarshipId, Integer admissionYear, Double amount, Double discountPercentage) {
        scholarshipByYearDAO.updateScholarshipByYear(scholarshipId, admissionYear, amount, discountPercentage);
    }
}