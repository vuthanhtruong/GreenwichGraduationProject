// src/main/java/com/example/demo/attendance/specializedAttendance/service/SpecializedAttendanceServiceImpl.java
package com.example.demo.attendance.specializedAttendance.service;

import com.example.demo.attendance.specializedAttendance.dao.SpecializedAttendanceDAO;
import com.example.demo.attendance.specializedAttendance.model.SpecializedAttendance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SpecializedAttendanceServiceImpl implements SpecializedAttendanceService {
    @Override
    public long countAttendanceSessionsThisWeek(String campusId, Integer week, Integer year) {
        return dao.countAttendanceSessionsThisWeek(campusId, week, year);
    }

    @Override
    public double getAverageAttendanceRateThisWeek(String campusId, Integer week, Integer year) {
        return dao.getAverageAttendanceRateThisWeek(campusId, week, year);
    }

    @Override
    public List<Object[]> getTop5ClassesLowestAttendanceThisWeek(String campusId, Integer week, Integer year) {
        return dao.getTop5ClassesLowestAttendanceThisWeek(campusId, week, year);
    }

    @Override
    public long countStudentsWithManyAbsencesThisWeek(String campusId, Integer week, Integer year) {
        return dao.countStudentsWithManyAbsencesThisWeek(campusId, week, year);
    }

    private final SpecializedAttendanceDAO dao;

    public SpecializedAttendanceServiceImpl(SpecializedAttendanceDAO dao) {
        this.dao = dao;
    }

    @Override
    public List<SpecializedAttendance> getAttendanceByTimetable(String timetableId) {
        return dao.getAttendanceByTimetable(timetableId);
    }

    @Override
    public void save(SpecializedAttendance attendance) {
        dao.save(attendance);
    }

    @Override
    public void saveAll(List<SpecializedAttendance> attendances) {
        dao.saveAll(attendances);
    }

    @Override
    public SpecializedAttendance findByTimetableAndStudent(String timetableId, String studentId) {
        return dao.findByTimetableAndStudent(timetableId, studentId);
    }
}