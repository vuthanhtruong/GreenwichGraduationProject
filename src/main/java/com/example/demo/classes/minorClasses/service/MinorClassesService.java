package com.example.demo.classes.minorClasses.service;

import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.subject.minorSubject.model.MinorSubjects;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface MinorClassesService {
    // ==================== DASHBOARD DEPUTY STAFF ====================
    long totalMinorClassesInMyCampus();

    long totalSlotsInMyCampus();

    long totalOccupiedSlotsInMyCampus();

    double averageClassSizeInMyCampus();

    List<Object[]> minorClassesBySemesterInMyCampus(); // [session, count, totalSlots]

    List<Object[]> top5LargestClassesInMyCampus(); // [nameClass, slotQuantity, enrolledCount]

    List<Object[]> minorClassesBySubjectInMyCampus(); // [subjectName, classCount, totalSlots]

    long unscheduledMinorClassesCount(); // Lớp chưa xếp lịch tuần này

    // ==================== CRUD & UTILS ====================
    List<MinorClasses> getClasses();

    MinorClasses getClassById(String id);

    MinorClasses getClassByName(String name);

    void addClass(MinorClasses c);

    MinorClasses editClass(String id, MinorClasses classObj);

    void deleteClass(String id);

    String generateUniqueClassId(LocalDateTime createdDate);

    Map<String, String> validateClass(MinorClasses classObj, String excludeId);

    // ==================== SEARCH & PAGINATION ====================
    List<MinorClasses> searchClassesByCampus(String searchType, String keyword, int firstResult, int pageSize, String campusId);

    long countSearchResultsByCampus(String searchType, String keyword, String campusId);

    List<MinorClasses> getPaginatedClassesByCampus(int firstResult, int pageSize, String campusId);

    long numberOfClassesByCampus(String campusId);

    // ==================== SUBJECT CLEANUP ====================
    void setNullWhenDeletingSubject(MinorSubjects subject);

    void deleteClassBySubject(MinorSubjects subject);
}
