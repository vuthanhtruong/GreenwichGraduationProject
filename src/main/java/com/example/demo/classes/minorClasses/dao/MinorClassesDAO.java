package com.example.demo.classes.minorClasses.dao;

import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.subject.minorSubject.model.MinorSubjects;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;

import java.time.LocalDateTime;
import java.util.List;

public interface MinorClassesDAO {
    List<MinorClasses> getClasses();
    MinorClasses getClassById(String id);
    MinorClasses getClassByName(String name);
    void addClass(MinorClasses c);
    MinorClasses editClass(String id, MinorClasses c);
    void deleteClass(String id);
    String generateUniqueClassId(LocalDateTime createdDate);
    List<String> validateClass(MinorClasses classObj, String excludeId);
    List<MinorClasses> searchClasses(String searchType, String keyword, int firstResult, int pageSize );
    long countSearchResults(String searchType, String keyword);
    List<MinorClasses> getPaginatedClasses(int firstResult, int pageSize );
    long numberOfClasses();
    void setNullWhenDeletingSubject(MinorSubjects subject);
    void deleteClassBySubject(MinorSubjects subject);
}