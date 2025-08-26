package com.example.demo.campus.service;

import com.example.demo.campus.model.Campuses;

import java.time.LocalDate;
import java.util.List;

public interface CampusesService {
    List<Campuses> getCampuses();
    Campuses getCampusById(String campusId);
    void addCampus(Campuses campus);
    boolean existsCampusById(String campusId);
    void deleteCampus(String campusId);
    void editCampus(Campuses campus);
    List<String> validateCampus(Campuses campus);
    String generateUniqueCampusId(LocalDate createdDate);
}
