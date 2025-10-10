package com.example.demo.specializedClasses.service;

import com.example.demo.specializedClasses.model.SpecializedClasses;
import com.example.demo.major.model.Majors;

import java.time.LocalDateTime;
import java.util.List;

public interface SpecializedClassesService {
    List<SpecializedClasses> ClassesByMajor(Majors major);
    List<SpecializedClasses> getClasses();
    SpecializedClasses getClassById(String id);
    SpecializedClasses getClassByName(String name);
    void addClass(SpecializedClasses c);
    SpecializedClasses editClass(String id, SpecializedClasses classObj);
    void deleteClass(String id);
    String generateUniqueClassId(String specializationId, LocalDateTime createdDate);
    List<String> validateClass(SpecializedClasses classObj, String excludeId);
    List<SpecializedClasses> searchClasses(String searchType, String keyword, int firstResult, int pageSize, Majors major);
    long countSearchResults(String searchType, String keyword, Majors major);
    List<SpecializedClasses> getPaginatedClasses(int firstResult, int pageSize, Majors major);
    long numberOfClasses(Majors major);
}
