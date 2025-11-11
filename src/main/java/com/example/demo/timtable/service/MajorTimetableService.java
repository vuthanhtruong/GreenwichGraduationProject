package com.example.demo.timtable.service;

import com.example.demo.entity.Enums.DaysOfWeek;
import com.example.demo.room.model.Rooms;
import com.example.demo.timtable.model.MajorTimetable;
import com.example.demo.timtable.model.Slots;

import java.time.LocalDate;
import java.util.List;

public interface MajorTimetableService {
    MajorTimetable getTimetableByClassSlotDayDate(String classId, String slotId, DaysOfWeek dayOfWeek, LocalDate date);
    List<Rooms> getAvailableRoomsForSlot(String classId, Slots slots, DaysOfWeek daysOfWeek, Integer WeekNumberInYear);
    String[][] SlotOfTheDayThatCanBeSuccessfullyArranged(String classId, Slots slots, DaysOfWeek daysOfWeek, Integer WeekNumberInYear);
    void SaveMajorTimetable(MajorTimetable timetable);
}
