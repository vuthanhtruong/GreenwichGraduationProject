package com.example.demo.student_scholarship.service;

import com.example.demo.student_scholarship.dao.StudentScholarshipDAO;
import com.example.demo.scholarshipByYear.model.ScholarshipByYear;
import com.example.demo.student_scholarship.model.Students_Scholarships;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class StudentScholarshipServiceImpl implements StudentScholarshipService {
    @Override
    public Map<String, Map<String, Object>> getAwardedScholarshipsByYear(Integer admissionYear) {
        return studentScholarshipDAO.getAwardedScholarshipsByYear(admissionYear);
    }

    private final StudentScholarshipDAO studentScholarshipDAO;

    public StudentScholarshipServiceImpl(StudentScholarshipDAO studentScholarshipDAO) {
        this.studentScholarshipDAO = studentScholarshipDAO;
    }


    @Override
    public void assignScholarship(String studentId, String scholarshipId, Integer admissionYear) {
        studentScholarshipDAO.assignScholarship(studentId, scholarshipId, admissionYear);
    }

    @Override
    public List<String> validateScholarshipAward(String studentId, String scholarshipId, Integer admissionYear) {
        return studentScholarshipDAO.validateScholarshipAward(studentId, scholarshipId, admissionYear);
    }

    @Override
    public ScholarshipByYear getScholarshipByYear(String id, Integer admissionYear) {
        return studentScholarshipDAO.getScholarshipByYear(id, admissionYear);
    }
}
