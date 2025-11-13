package com.example.demo.attendance.majorAttendance.service;

import com.example.demo.attendance.majorAttendance.model.MajorAttendance;

import java.util.List;

public interface MajorAttendanceService {
    List<MajorAttendance> getAttendanceByTimetable(String timetableId);

    void save(MajorAttendance attendance);

    void saveAll(List<MajorAttendance> attendances);

    MajorAttendance findByTimetableAndStudent(String timetableId, String studentId);
}
