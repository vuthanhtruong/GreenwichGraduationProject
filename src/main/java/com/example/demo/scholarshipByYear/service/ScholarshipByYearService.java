package com.example.demo.scholarshipByYear.service;

import com.example.demo.scholarshipByYear.model.ScholarshipByYear;
import com.example.demo.scholarshipByYear.model.ScholarshipByYearId;

import java.util.List;

public interface ScholarshipByYearService {
    Long getCountScholarshipByYear(Integer admissionYear);
    ScholarshipByYear getScholarshipByYear(String scholarshipId, Integer admissionYear);
    ScholarshipByYear findById(ScholarshipByYearId id);
    void createScholarshipByYear(ScholarshipByYear scholarshipByYear);
    void updateScholarshipByYear(String scholarshipId, Integer admissionYear, Double amount, Double discountPercentage);
    void updateScholarshipByYear(ScholarshipByYear scholarshipByYear);
    List<ScholarshipByYear> getScholarshipsByYear(Integer admissionYear);
    List<Integer> getAllAdmissionYears();
    void saveOrUpdate(ScholarshipByYear scholarshipByYear);
    void finalizeScholarshipContracts(Integer admissionYear);
    List<ScholarshipByYear> getAllFinalizedScholarshipsByAdmissionYear(Integer admissionYear);
    ScholarshipByYear getFinalizedScholarshipByIdAndYear(String scholarshipId, Integer admissionYear);
}
