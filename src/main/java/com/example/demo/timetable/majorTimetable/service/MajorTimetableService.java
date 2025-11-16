package com.example.demo.timetable.majorTimetable.service;

import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.entity.Enums.DaysOfWeek;
import com.example.demo.room.model.Rooms;
import com.example.demo.timetable.majorTimetable.model.MajorTimetable;
import com.example.demo.timetable.majorTimetable.model.Slots;

import java.util.List;

public interface MajorTimetableService {
    MajorTimetable getTimetableByClassSlotDayWeek(
            String classId, String campusId, String slotId, DaysOfWeek dayOfWeek, Integer weekOfYear, Integer year);

    List<Rooms> getAvailableRoomsForSlot(
            String classId, String campusId, Slots slots, DaysOfWeek daysOfWeek, Integer weekNumberInYear, Integer year);

    String[][] SlotOfTheDayThatCanBeSuccessfullyArranged(
            String classId, String campusId, Slots slots, DaysOfWeek daysOfWeek, Integer WeekNumberInYear, Integer year);

    void SaveMajorTimetable(MajorTimetable timetable, String campusId);

    List<MajorTimetable> getMajorTimetablesByWeekInYear(Integer weekInYear, Integer year, String campusId);

    void delete(MajorTimetable timetable);
    MajorTimetable getById(String timetableId);
    int countBookedSlotsInWeek(String classId, Integer week, Integer year, String campusId);
    int countTotalBookedSlots(String classId); // BỎ campusId
    List<MajorTimetable> getAllSchedulesByClass(String classId);
    List<MajorTimetable> getMajorTimetableByStudent(String studentId, Integer week, Integer year);
    List<MajorTimetable> getMajorTimetablesByLecturer(String lecturerId, Integer week, Integer year);
    MajorTimetable getMajorTimetableById(String timetableId);
    List<MajorClasses> getMajorClassesByMajorTimetable(Integer week, Integer year, String campusId);
    List<MajorTimetable> getMajorTimetableTodayByLecturer(String lecturerId);
}
