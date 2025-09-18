package com.example.demo.scholarshipByYear.dao;

import com.example.demo.scholarshipByYear.model.ScholarshipByYear;
import java.util.List;

public interface ScholarshipByYearDAO {
    List<ScholarshipByYear> getScholarshipsByYear(Integer admissionYear);
    List<Integer> getAllAdmissionYears();
    void saveOrUpdate(ScholarshipByYear scholarshipByYear);
    void updateScholarshipByYear(String scholarshipId, Integer admissionYear, Double amount, Double discountPercentage);
}