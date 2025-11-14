package com.example.demo.classes.majorClasses.service;

import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.major.model.Majors;
import com.example.demo.subject.majorSubject.model.MajorSubjects;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface MajorClassesService {
    void SetNullWhenDeletingSubject(MajorSubjects subject);
    void deleteClassBySubject(MajorSubjects subject);
    List<MajorClasses> getClassesByMajorAndCampus(Majors major, String campusId);
    List<MajorClasses> getClasses();
    MajorClasses getClassById(String id);
    MajorClasses getClassByName(String name);
    void addClass(MajorClasses c);
    MajorClasses editClass(String id, MajorClasses classObj);
    void deleteClass(String id);
    String generateUniqueClassId(String majorId, LocalDateTime createdDate);
    Map<String, String> validateClass(MajorClasses classObj, String excludeId);
    List<MajorClasses> searchClassesByCampus(String searchType, String keyword, int firstResult, int pageSize, Majors major, String campusId);
    long countSearchResultsByCampus(String searchType, String keyword, Majors major, String campusId);
    List<MajorClasses> getPaginatedClassesByCampus(int firstResult, int pageSize, Majors major, String campusId);
    long numberOfClassesByCampus(Majors major, String campusId);
}
