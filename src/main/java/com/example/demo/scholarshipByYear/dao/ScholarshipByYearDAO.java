package com.example.demo.scholarshipByYear.dao;

import com.example.demo.scholarshipByYear.model.ScholarshipByYear;
import java.util.List;

public interface ScholarshipByYearDAO {
    Long getCountScholarshipByYear(Integer admissionYear);
    ScholarshipByYear getScholarshipByYear(String scholarshipId, Integer admissionYear);
    void updateScholarshipByYear(String scholarshipId, Integer admissionYear, Double amount, Double discountPercentage);
    List<ScholarshipByYear> getScholarshipsByYear(Integer admissionYear);
    List<Integer> getAllAdmissionYears();
    void saveOrUpdate(ScholarshipByYear scholarshipByYear);
    void finalizeScholarshipContracts(Integer admissionYear);
}