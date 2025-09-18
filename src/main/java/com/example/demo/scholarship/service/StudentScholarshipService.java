package com.example.demo.scholarship.service;

import com.example.demo.scholarship.model.ScholarshipByYear;
import com.example.demo.scholarship.model.Students_Scholarships;

import java.util.List;

public interface StudentScholarshipService {
    List<Students_Scholarships> getAwardedScholarshipsByYear(Integer admissionYear);
    void assignScholarship(String studentId, String scholarshipId, Integer admissionYear);
    List<String> validateScholarshipAward(String studentId, String scholarshipId, Integer admissionYear);
    ScholarshipByYear getScholarshipByYear(String id, Integer admissionYear);
}
