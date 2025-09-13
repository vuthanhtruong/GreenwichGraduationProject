package com.example.demo.scholarship.service;

import com.example.demo.scholarship.dao.ScholarshipsDAO;
import com.example.demo.scholarship.model.Scholarships;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScholarshipsServiceImpl implements ScholarshipsService {
    @Override
    public String generateUniqueScholarshipId() {
        return scholarshipsDAO.generateUniqueScholarshipId();
    }

    @Override
    public Scholarships addScholarship(Scholarships scholarship) {
        return scholarshipsDAO.addScholarship(scholarship);
    }

    @Override
    public List<String> validateScholarship(Scholarships scholarship) {
        return scholarshipsDAO.validateScholarship(scholarship);
    }

    private final ScholarshipsDAO scholarshipsDAO;
    public ScholarshipsServiceImpl(ScholarshipsDAO scholarshipsDAO) {
        this.scholarshipsDAO = scholarshipsDAO;
    }

    public boolean existsScholarshipById(String scholarshipId) {
        return scholarshipsDAO.existsScholarshipById(scholarshipId);
    }

    public Scholarships getScholarshipById(String id) {
        return scholarshipsDAO.getScholarshipById(id);
    }

    public List<Scholarships> getAllScholarships() {
        return scholarshipsDAO.getAllScholarships();
    }

    public List<Scholarships> getPaginatedScholarships(int firstResult, int pageSize) {
        return scholarshipsDAO.getPaginatedScholarships(firstResult, pageSize);
    }

    public long numberOfScholarships() {
        return scholarshipsDAO.numberOfScholarships();
    }

    public List<Scholarships> searchScholarships(String searchType, String keyword, int firstResult, int pageSize) {
        return scholarshipsDAO.searchScholarships(searchType, keyword, firstResult, pageSize);
    }

    public long countSearchResults(String searchType, String keyword) {
        return scholarshipsDAO.countSearchResults(searchType, keyword);
    }
}