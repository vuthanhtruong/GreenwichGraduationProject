package com.example.demo.classes.majorClasses.service;

import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.major.model.Majors;
import com.example.demo.subject.majorSubject.model.MajorSubjects;

import java.time.LocalDateTime;
import java.util.List;

public interface MajorClassesService {
    void SetNullWhenDeletingSubject(MajorSubjects subject);
    void deleteClassBySubject(MajorSubjects subject);
    List<MajorClasses> ClassesByMajor(Majors major);
    List<MajorClasses> getClasses();
    MajorClasses getClassById(String id);
    MajorClasses getClassByName(String name);
    void addClass(MajorClasses c);
    MajorClasses editClass(String id, MajorClasses classObj);
    void deleteClass(String id);
    String generateUniqueClassId(String majorId, LocalDateTime createdDate);
    List<String> validateClass(MajorClasses classObj, String excludeId);
    List<MajorClasses> searchClasses(String searchType, String keyword, int firstResult, int pageSize, Majors major);
    long countSearchResults(String searchType, String keyword, Majors major);
    List<MajorClasses> getPaginatedClasses(int firstResult, int pageSize, Majors major);
    long numberOfClasses(Majors major);
}
