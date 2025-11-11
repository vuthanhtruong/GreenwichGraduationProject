package com.example.demo.timtable.majorTimetable.dao;

import com.example.demo.timtable.majorTimetable.model.Timetable;

import java.util.List;

public interface TimetableDAO {
    List<Timetable> getTimetablesByWeekInYear(Integer weekInYear, Integer Year);
}
