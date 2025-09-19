package com.example.demo.scholarshipByYear.service;

import com.example.demo.scholarshipByYear.dao.ScholarshipByYearDAO;
import com.example.demo.scholarshipByYear.model.ScholarshipByYear;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScholarshipByYearServiceImpl implements ScholarshipByYearService {

    private final ScholarshipByYearDAO scholarshipByYearDAO;

    @Autowired
    public ScholarshipByYearServiceImpl(ScholarshipByYearDAO scholarshipByYearDAO) {
        this.scholarshipByYearDAO = scholarshipByYearDAO;
    }

    @Override
    public List<Integer> getAllAdmissionYears() {
        return scholarshipByYearDAO.getAllAdmissionYears();
    }

    @Override
    public List<ScholarshipByYear> getScholarshipsByYear(Integer admissionYear) {
        return scholarshipByYearDAO.getScholarshipsByYear(admissionYear);
    }

    @Override
    public void saveOrUpdate(ScholarshipByYear scholarshipByYear) {
        scholarshipByYearDAO.saveOrUpdate(scholarshipByYear);
    }

    @Override
    public void updateScholarshipByYear(String scholarshipId, Integer admissionYear, Double amount, Double discountPercentage) {
        scholarshipByYearDAO.updateScholarshipByYear(scholarshipId, admissionYear, amount, discountPercentage);
    }

    @Override
    public Long getCountScholarshipByYear(Integer admissionYear) {
        return scholarshipByYearDAO.getCountScholarshipByYear(admissionYear);
    }

    @Override
    public ScholarshipByYear getScholarshipByYear(String scholarshipId, Integer admissionYear) {
        return scholarshipByYearDAO.getScholarshipByYear(scholarshipId, admissionYear);
    }
}