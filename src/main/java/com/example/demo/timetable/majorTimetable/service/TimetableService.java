// com.example.demo.timtable.majorTimetable.service.TimetableService
package com.example.demo.timetable.majorTimetable.service;

import com.example.demo.timetable.majorTimetable.model.Timetable;

import java.util.List;

public interface TimetableService {
    List<Timetable> getTimetablesByWeekInYear(Integer weekInYear, Integer year);
    List<Timetable> getStudentTimetable(String studentId, Integer week, Integer year);
    List<Timetable> getMajorLecturerTimetable(String lecturerId, Integer week, Integer year);
    List<Timetable> getTimetableTodayByLecturer(String lecturerId);
    List<Timetable> getMajorTimetableAndSpecializedInWeek(Integer weekOfYear, Integer year, String campusId);
}