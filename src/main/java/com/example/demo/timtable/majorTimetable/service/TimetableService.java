// com.example.demo.timtable.majorTimetable.service.TimetableService
package com.example.demo.timtable.majorTimetable.service;

import com.example.demo.timtable.majorTimetable.model.Timetable;

import java.util.List;

public interface TimetableService {
    List<Timetable> getTimetablesByWeekInYear(Integer weekInYear, Integer year);
    List<Timetable> getStudentTimetable(String studentId, Integer week, Integer year);
}