// src/main/java/com/example/demo/attendance/specializedAttendance/dao/SpecializedAttendanceDAO.java
package com.example.demo.attendance.specializedAttendance.dao;

import com.example.demo.attendance.specializedAttendance.model.SpecializedAttendance;

import java.util.List;

public interface SpecializedAttendanceDAO {
    List<SpecializedAttendance> getAttendanceByTimetable(String timetableId);

    void save(SpecializedAttendance attendance);

    void saveAll(List<SpecializedAttendance> attendances);

    SpecializedAttendance findByTimetableAndStudent(String timetableId, String studentId);
}