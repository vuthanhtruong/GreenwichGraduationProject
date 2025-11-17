package com.example.demo.student_scholarship.dao;

import com.example.demo.scholarship.model.Scholarships;
import com.example.demo.scholarshipByYear.model.ScholarshipByYear;
import com.example.demo.student_scholarship.model.Students_Scholarships;

import java.util.List;
import java.util.Map;

public interface StudentScholarshipDAO {
    Map<String, Map<String, Object>> getAwardedScholarshipsByYear(Integer admissionYear);
    void assignScholarship(String studentId, String scholarshipId, Integer admissionYear);
    List<String> validateScholarshipAward(String studentId, String scholarshipId, Integer admissionYear);
    ScholarshipByYear getScholarshipByYear(String scholarshipId, Integer admissionYear);
    Long getCountStudentScholarshipByYear(Integer admissionYear, Scholarships scholarship);
    Map<String, Object> getScholarshipByStudentId(String studentId);
    Students_Scholarships getActiveScholarshipByStudentIdAndYear(String studentId, Integer admissionYear);
}
