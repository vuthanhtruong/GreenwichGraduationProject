// src/main/java/com/example/demo/attendance/majorAttendance/service/MajorAttendanceServiceImpl.java
package com.example.demo.attendance.majorAttendance.service;

import com.example.demo.attendance.majorAttendance.dao.MajorAttendanceDAO;
import com.example.demo.attendance.majorAttendance.model.MajorAttendance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MajorAttendanceServiceImpl implements MajorAttendanceService {
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

    @Override
    public void save(MajorAttendance attendance) {
        dao.save(attendance);
    }

    private final MajorAttendanceDAO dao;

    public MajorAttendanceServiceImpl(MajorAttendanceDAO dao) {
        this.dao = dao;
    }

    @Override
    public List<MajorAttendance> getAttendanceByTimetable(String timetableId) {
        return dao.getAttendanceByTimetable(timetableId);
    }

    @Override
    public void saveAll(List<MajorAttendance> attendances) {
        dao.saveAll(attendances);
    }

    @Override
    public MajorAttendance findByTimetableAndStudent(String timetableId, String studentId) {
        return dao.findByTimetableAndStudent(timetableId, studentId);
    }
}