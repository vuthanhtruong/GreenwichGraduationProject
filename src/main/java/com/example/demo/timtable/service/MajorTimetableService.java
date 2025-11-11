package com.example.demo.timtable.service;

import com.example.demo.entity.Enums.DaysOfWeek;
import com.example.demo.room.model.Rooms;
import com.example.demo.timtable.model.MajorTimetable;
import com.example.demo.timtable.model.Slots;

import java.time.LocalDate;
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
}
