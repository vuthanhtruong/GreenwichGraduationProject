// src/main/java/com/example/demo/attendance/specializedAttendance/service/SpecializedAttendanceService.java
package com.example.demo.attendance.specializedAttendance.service;

import com.example.demo.attendance.specializedAttendance.model.SpecializedAttendance;

import java.util.List;

public interface SpecializedAttendanceService {
    List<SpecializedAttendance> getAttendanceByTimetable(String timetableId);

    void save(SpecializedAttendance attendance);

    void saveAll(List<SpecializedAttendance> attendances);

    SpecializedAttendance findByTimetableAndStudent(String timetableId, String studentId);

    long countAttendanceSessionsThisWeek(String campusId, Integer week, Integer year);
    double getAverageAttendanceRateThisWeek(String campusId, Integer week, Integer year);
    List<Object[]> getTop5ClassesLowestAttendanceThisWeek(String campusId, Integer week, Integer year);
    long countStudentsWithManyAbsencesThisWeek(String campusId, Integer week, Integer year);
}