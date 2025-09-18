package com.example.demo.scholarshipByYear.service;

import com.example.demo.scholarshipByYear.model.ScholarshipByYear;

import java.util.List;

public interface ScholarshipByYearService {
    List<ScholarshipByYear> getScholarshipsByYear(Integer admissionYear);
    List<Integer> getAllAdmissionYears();
    void saveOrUpdate(ScholarshipByYear scholarshipByYear);
    void updateScholarshipByYear(String scholarshipId, Integer admissionYear, Double amount, Double discountPercentage);
}
