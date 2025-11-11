package com.example.demo.timtable.service;

import com.example.demo.entity.Enums.DaysOfWeek;
import com.example.demo.room.model.Rooms;
import com.example.demo.timtable.dao.MajorTimetableDAO;
import com.example.demo.timtable.model.MajorTimetable;
import com.example.demo.timtable.model.Slots;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MajorTimetableServiceImpl implements MajorTimetableService {
    @Override
    public MajorTimetable getById(String timetableId) {
        return majorTimetableDAO.getById(timetableId);
    }

    @Override
    public void delete(MajorTimetable timetable) {
        majorTimetableDAO.delete(timetable);
    }

    private final MajorTimetableDAO majorTimetableDAO;

    public MajorTimetableServiceImpl(MajorTimetableDAO majorTimetableDAO) {
        this.majorTimetableDAO = majorTimetableDAO;
    }

    @Override
    public List<MajorTimetable> getMajorTimetablesByWeekInYear(Integer weekInYear, Integer year, String campusId) {
        return majorTimetableDAO.getMajorTimetablesByWeekInYear(weekInYear, year, campusId);
    }

    @Override
    public MajorTimetable getTimetableByClassSlotDayWeek(String classId, String campusId, String slotId, DaysOfWeek dayOfWeek, Integer weekOfYear, Integer year) {
        return majorTimetableDAO.getTimetableByClassSlotDayWeek(classId, campusId, slotId, dayOfWeek, weekOfYear, year);
    }

    @Override
    public List<Rooms> getAvailableRoomsForSlot(String classId, String campusId, Slots slots, DaysOfWeek daysOfWeek, Integer weekNumberInYear, Integer year) {
        return majorTimetableDAO.getAvailableRoomsForSlot(classId, campusId, slots, daysOfWeek, weekNumberInYear, year);
    }

    @Override
    public String[][] SlotOfTheDayThatCanBeSuccessfullyArranged(String classId, String campusId, Slots slots, DaysOfWeek daysOfWeek, Integer weekNumberInYear, Integer year) {
        return majorTimetableDAO.SlotOfTheDayThatCanBeSuccessfullyArranged(classId, campusId, slots, daysOfWeek, weekNumberInYear, year);
    }

    @Override
    public void SaveMajorTimetable(MajorTimetable timetable, String campusId) {
        majorTimetableDAO.SaveMajorTimetable(timetable, campusId);
    }
}