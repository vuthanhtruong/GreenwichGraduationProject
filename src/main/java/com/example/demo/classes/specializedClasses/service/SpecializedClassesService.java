package com.example.demo.classes.specializedClasses.service;

import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.major.model.Majors;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface SpecializedClassesService {
    List<SpecializedClasses> getClassesByMajorAndCampus(Majors major, String campusId);
    List<SpecializedClasses> getClasses();
    SpecializedClasses getClassById(String id);
    SpecializedClasses getClassByName(String name);
    void addClass(SpecializedClasses c);
    SpecializedClasses editClass(String id, SpecializedClasses classObj);
    void deleteClass(String id);
    String generateUniqueClassId(String specializationId, LocalDateTime createdDate);
    Map<String, String> validateClass(SpecializedClasses classObj, String excludeId);
    List<SpecializedClasses> searchClassesByCampus(String searchType, String keyword, int firstResult, int pageSize, Majors major, String campusId);
    long countSearchResultsByCampus(String searchType, String keyword, Majors major, String campusId);
    List<SpecializedClasses> getPaginatedClassesByCampus(int firstResult, int pageSize, Majors major, String campusId);
    long numberOfClassesByCampus(Majors major, String campusId);
}
