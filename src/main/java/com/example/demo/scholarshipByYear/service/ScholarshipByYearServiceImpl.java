package com.example.demo.scholarshipByYear.service;

import com.example.demo.scholarshipByYear.dao.ScholarshipByYearDAO;
import com.example.demo.scholarshipByYear.model.ScholarshipByYear;
import com.example.demo.scholarshipByYear.model.ScholarshipByYearId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScholarshipByYearServiceImpl implements ScholarshipByYearService {
    @Override
    public ScholarshipByYear getFinalizedScholarshipByIdAndYear(String scholarshipId, Integer admissionYear) {
        return scholarshipByYearDAO.getFinalizedScholarshipByIdAndYear(scholarshipId, admissionYear);
    }

    @Override
    public List<ScholarshipByYear> getAllFinalizedScholarshipsByAdmissionYear(Integer admissionYear) {
        return scholarshipByYearDAO.getAllFinalizedScholarshipsByAdmissionYear(admissionYear);
    }

    @Override
    public void updateScholarshipByYear(ScholarshipByYear scholarshipByYear) {
        scholarshipByYearDAO.updateScholarshipByYear(scholarshipByYear);
    }

    @Override
    public ScholarshipByYear findById(ScholarshipByYearId id) {
        return scholarshipByYearDAO.findById(id);
    }

    @Override
    public void createScholarshipByYear(ScholarshipByYear scholarshipByYear) {
        scholarshipByYearDAO.createScholarshipByYear(scholarshipByYear);
    }

    @Override
    public void finalizeScholarshipContracts(Integer admissionYear) {
        scholarshipByYearDAO.finalizeScholarshipContracts(admissionYear);
    }

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