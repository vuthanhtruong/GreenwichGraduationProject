// com.example.demo.timtable.majorTimetable.dao.TimetableDAO
package com.example.demo.timtable.majorTimetable.dao;

import com.example.demo.timtable.majorTimetable.model.Timetable;

import java.util.ArrayList;
import java.util.List;

public interface TimetableDAO {
    List<Timetable> getTimetablesByWeekInYear(Integer weekInYear, Integer year);
    List<Timetable> getStudentTimetable(String studentId, Integer week, Integer year);
    List<Timetable> getMajorLecturerTimetable(String lecturerId, Integer week, Integer year);
}