package com.example.demo.scholarship.service;

import com.example.demo.scholarship.dao.StudentScholarshipDAO;
import com.example.demo.scholarship.model.ScholarshipByYear;
import com.example.demo.scholarship.model.Students_Scholarships;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentScholarshipServiceImpl implements StudentScholarshipService {
    private final StudentScholarshipDAO studentScholarshipDAO;

    public StudentScholarshipServiceImpl(StudentScholarshipDAO studentScholarshipDAO) {
        this.studentScholarshipDAO = studentScholarshipDAO;
    }

    @Override
    public List<Students_Scholarships> getAwardedScholarshipsByYear(Integer admissionYear) {
        return studentScholarshipDAO.getAwardedScholarshipsByYear(admissionYear);
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
