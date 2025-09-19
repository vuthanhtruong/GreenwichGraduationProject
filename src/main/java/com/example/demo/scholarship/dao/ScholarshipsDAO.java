package com.example.demo.scholarship.dao;

import com.example.demo.scholarship.model.Scholarships;
import java.util.List;
import java.util.Map;

public interface ScholarshipsDAO {
    boolean existsScholarshipById(String scholarshipId);
    Scholarships getScholarshipById(String id);
    List<Scholarships> getAllScholarships();
    List<Scholarships> getPaginatedScholarships(int firstResult, int pageSize);
    long numberOfScholarships();
    List<Scholarships> searchScholarships(String searchType, String keyword, int firstResult, int pageSize);
    long countSearchResults(String searchType, String keyword);
    String generateUniqueScholarshipId();
    Scholarships addScholarship(Scholarships scholarship);
    Map<String, String> validateScholarship(Scholarships scholarship);
    List<Scholarships> getScScholarshipsByName(String scholarshipName);
}