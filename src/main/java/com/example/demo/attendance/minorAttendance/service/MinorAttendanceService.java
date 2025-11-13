// src/main/java/com/example/demo/attendance/minorAttendance/service/MinorAttendanceService.java
package com.example.demo.attendance.minorAttendance.service;

import com.example.demo.attendance.minorAttendance.model.MinorAttendance;

import java.util.List;

public interface MinorAttendanceService {
    List<MinorAttendance> getAttendanceByTimetable(String timetableId);

    void save(MinorAttendance attendance);

    void saveAll(List<MinorAttendance> attendances);

    MinorAttendance findByTimetableAndStudent(String timetableId, String studentId);
}