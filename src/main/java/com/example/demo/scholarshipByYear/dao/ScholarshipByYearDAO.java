package com.example.demo.scholarshipByYear.dao;

import com.example.demo.scholarshipByYear.model.ScholarshipByYear;
import com.example.demo.scholarshipByYear.model.ScholarshipByYearId;
import com.example.demo.student_scholarship.model.Students_Scholarships;

import java.util.List;

public interface ScholarshipByYearDAO {
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
    ScholarshipByYear getByScholarshipIdAndYear(String scholarshipId, Integer admissionYear);

    void save(ScholarshipByYear scholarshipByYear);
}