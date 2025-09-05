package com.example.demo.campus.dao;

import com.example.demo.campus.model.Campuses;
import jakarta.persistence.TypedQuery;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface CampusesDAO {
    List<Campuses> getCampuses();
    Campuses getCampusById(String campusId);
    void addCampus(Campuses campus);
    boolean existsCampusById(String campusId);
    void deleteCampus(String campusId);
    void editCampus(Campuses campus);
    List<String> validateCampus(Campuses campus);
    String generateUniqueCampusId(LocalDate createdDate);
    void updateCampusFields(Campuses existing, Campuses updated);
    Map<String, Map<String, Long>> getCampusCounts();
    List<String> validateCampus(Campuses campus, MultipartFile avatarFile);
    List<Campuses> existsCampusByName(String campusName);

}
