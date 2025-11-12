// src/main/java/com/example/demo/timtable/minorTimetable/service/MinorTimetableServiceImpl.java
package com.example.demo.timtable.minorTimtable.service;

import com.example.demo.entity.Enums.DaysOfWeek;
import com.example.demo.room.model.Rooms;
import com.example.demo.timtable.majorTimetable.model.Slots;
import com.example.demo.timtable.minorTimtable.dao.MinorTimetableDAO;
import com.example.demo.timtable.minorTimtable.model.MinorTimetable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MinorTimetableServiceImpl implements MinorTimetableService {

    private final MinorTimetableDAO dao;

    public MinorTimetableServiceImpl(MinorTimetableDAO dao) {
        this.dao = dao;
    }

    @Override public List<MinorTimetable> getAllSchedulesByClass(String classId) { return dao.getAllSchedulesByClass(classId); }
    @Override public int countTotalBookedSlots(String classId) { return dao.countTotalBookedSlots(classId); }
    @Override public int countBookedSlotsInWeek(String classId, Integer week, Integer year, String campusId) { return dao.countBookedSlotsInWeek(classId, week, year, campusId); }
    @Override public MinorTimetable getById(String timetableId) { return dao.getById(timetableId); }
    @Override public void delete(MinorTimetable timetable) { dao.delete(timetable); }
    @Override public List<MinorTimetable> getMinorTimetablesByWeekInYear(Integer week, Integer year, String campusId) { return dao.getMinorTimetablesByWeekInYear(week, year, campusId); }
    @Override public MinorTimetable getTimetableByClassSlotDayWeek(String classId, String campusId, String slotId, DaysOfWeek dayOfWeek, Integer weekOfYear, Integer year) { return dao.getTimetableByClassSlotDayWeek(classId, campusId, slotId, dayOfWeek, weekOfYear, year); }
    @Override public List<Rooms> getAvailableRoomsForSlot(String classId, String campusId, Slots slot, DaysOfWeek day, Integer week, Integer year) { return dao.getAvailableRoomsForSlot(classId, campusId, slot, day, week, year); }
    @Override public String[][] SlotOfTheDayThatCanBeSuccessfullyArranged(String classId, String campusId, Slots slots, DaysOfWeek daysOfWeek, Integer weekNumberInYear, Integer year) { return dao.SlotOfTheDayThatCanBeSuccessfullyArranged(classId, campusId, slots, daysOfWeek, weekNumberInYear, year); }
    @Override public void saveMinorTimetable(MinorTimetable timetable, String campusId) { dao.saveMinorTimetable(timetable, campusId); }
    @Override public List<MinorTimetable> getMinorTimetableByStudent(String studentId, Integer week, Integer year) { return dao.getMinorTimetableByStudent(studentId, week, year); }
}