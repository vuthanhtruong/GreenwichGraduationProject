// com.example.demo.timtable.majorTimetable.service.TimetableServiceImpl
package com.example.demo.timetable.majorTimetable.service;

import com.example.demo.timetable.majorTimetable.dao.TimetableDAO;
import com.example.demo.timetable.majorTimetable.model.Timetable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TimetableServiceImpl implements TimetableService {
    @Override
    public List<Timetable> getTimetableTodayByLecturer(String lecturerId) {
        return dao.getTimetableTodayByLecturer(lecturerId);
    }

    @Override
    public List<Timetable> getMajorLecturerTimetable(String lecturerId, Integer week, Integer year) {
        return dao.getMajorLecturerTimetable(lecturerId, week, year);
    }

    @Override
    public List<Timetable> getStudentTimetable(String studentId, Integer week, Integer year) {
        return dao.getStudentTimetable(studentId, week, year);
    }

    private final TimetableDAO dao;

    public TimetableServiceImpl(TimetableDAO dao) {
        this.dao = dao;
    }

    @Override
    public List<Timetable> getTimetablesByWeekInYear(Integer week, Integer year) {
        return dao.getTimetablesByWeekInYear(week, year);
    }

}