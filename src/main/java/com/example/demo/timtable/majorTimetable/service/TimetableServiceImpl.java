// com.example.demo.timtable.majorTimetable.service.TimetableServiceImpl
package com.example.demo.timtable.majorTimetable.service;

import com.example.demo.timtable.majorTimetable.dao.TimetableDAO;
import com.example.demo.timtable.majorTimetable.model.Timetable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TimetableServiceImpl implements TimetableService {
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