package com.example.demo.timtable.dao;

import com.example.demo.timtable.model.Timetable;

import java.util.List;

public interface TimetableDAO {
    List<Timetable> getTimetablesByWeekInYear(Integer weekInYear, Integer Year);
}
