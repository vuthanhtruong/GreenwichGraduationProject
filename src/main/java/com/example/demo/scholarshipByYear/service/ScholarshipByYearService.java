package com.example.demo.scholarshipByYear.service;

import com.example.demo.scholarshipByYear.model.ScholarshipByYear;

import java.util.List;

public interface ScholarshipByYearService {
    List<Integer> getAllAdmissionYears();
    List<ScholarshipByYear> getScholarshipsByYear(Integer admissionYear);
    void saveOrUpdate(ScholarshipByYear scholarshipByYear);
    void updateScholarshipByYear(String scholarshipId, Integer admissionYear, Double amount, Double discountPercentage);
    Long getCountScholarshipByYear(Integer admissionYear);
    ScholarshipByYear getScholarshipByYear(String scholarshipId, Integer admissionYear);
}
