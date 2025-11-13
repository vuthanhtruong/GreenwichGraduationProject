// src/main/java/com/example/demo/attendance/minorAttendance/service/MinorAttendanceServiceImpl.java
package com.example.demo.attendance.minorAttendance.service;

import com.example.demo.attendance.minorAttendance.dao.MinorAttendanceDAO;
import com.example.demo.attendance.minorAttendance.model.MinorAttendance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MinorAttendanceServiceImpl implements MinorAttendanceService {

    private final MinorAttendanceDAO dao;

    public MinorAttendanceServiceImpl(MinorAttendanceDAO dao) {
        this.dao = dao;
    }

    @Override
    public List<MinorAttendance> getAttendanceByTimetable(String timetableId) {
        return dao.getAttendanceByTimetable(timetableId);
    }

    @Override
    public void save(MinorAttendance attendance) {
        dao.save(attendance);
    }

    @Override
    public void saveAll(List<MinorAttendance> attendances) {
        dao.saveAll(attendances);
    }

    @Override
    public MinorAttendance findByTimetableAndStudent(String timetableId, String studentId) {
        return dao.findByTimetableAndStudent(timetableId, studentId);
    }
}