// com.example.demo.timtable.majorTimetable.dao.TimetableDAO
package com.example.demo.timetable.majorTimetable.dao;

import com.example.demo.timetable.majorTimetable.model.MajorTimetable;
import com.example.demo.timetable.majorTimetable.model.Timetable;

import java.sql.Time;
import java.util.List;

public interface TimetableDAO {
    List<Timetable> getTimetablesByWeekInYear(Integer weekInYear, Integer year);
    List<Timetable> getStudentTimetable(String studentId, Integer week, Integer year);
    List<Timetable> getMajorLecturerTimetable(String lecturerId, Integer week, Integer year);
    List<Timetable> getTimetableTodayByLecturer(String lecturerId);
    List<Timetable> getMajorTimetableAndSpecializedInWeek(Integer weekOfYear, Integer year, String campusId);
}