package com.example.demo.classes.minorClasses.service;

import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.subject.minorSubject.model.MinorSubjects;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;

import java.time.LocalDateTime;
import java.util.List;

public interface MinorClassesService {
    List<MinorClasses> getClasses();
    MinorClasses getClassById(String id);
    MinorClasses getClassByName(String name);
    void addClass(MinorClasses c);
    MinorClasses editClass(String id, MinorClasses c);
    void deleteClass(String id);
    String generateUniqueClassId(LocalDateTime createdDate);
    List<String> validateClass(MinorClasses classObj, String excludeId);
    List<MinorClasses> searchClassesByCampus(String searchType, String keyword, int firstResult, int pageSize, String campusId);
    long countSearchResultsByCampus(String searchType, String keyword, String campusId);
    List<MinorClasses> getPaginatedClassesByCampus(int firstResult, int pageSize, String campusId);
    long numberOfClassesByCampus(String campusId);
    void setNullWhenDeletingSubject(MinorSubjects subject);
    void deleteClassBySubject(MinorSubjects subject);
}
