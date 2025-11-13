// src/main/java/com/example/demo/attendance/minorAttendance/dao/MinorAttendanceDAO.java
package com.example.demo.attendance.minorAttendance.dao;

import com.example.demo.attendance.minorAttendance.model.MinorAttendance;

import java.util.List;

public interface MinorAttendanceDAO {
    List<MinorAttendance> getAttendanceByTimetable(String timetableId);

    void save(MinorAttendance attendance);

    void saveAll(List<MinorAttendance> attendances);

    MinorAttendance findByTimetableAndStudent(String timetableId, String studentId);
}