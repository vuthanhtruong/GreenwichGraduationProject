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
    public MajorTimetable getTimetableByClassSlotDayDate(String classId, String slotId, DaysOfWeek dayOfWeek, LocalDate date) {
        return majorTimetableDAO.getTimetableByClassSlotDayDate(classId, slotId, dayOfWeek, date);
    }

    @Override
    public List<Rooms> getAvailableRoomsForSlot(String classId, Slots slots, DaysOfWeek daysOfWeek, Integer WeekNumberInYear) {
        return majorTimetableDAO.getAvailableRoomsForSlot(classId, slots, daysOfWeek, WeekNumberInYear);
    }

    @Override
    public void SaveMajorTimetable(MajorTimetable timetable) {
        majorTimetableDAO.SaveMajorTimetable(timetable);
    }

    private final MajorTimetableDAO majorTimetableDAO;

    public MajorTimetableServiceImpl(MajorTimetableDAO majorTimetableDAO) {
        this.majorTimetableDAO = majorTimetableDAO;
    }

    @Override
    public String[][] SlotOfTheDayThatCanBeSuccessfullyArranged(String classId, Slots slots, DaysOfWeek daysOfWeek, Integer WeekNumberInYear) {
        return majorTimetableDAO.SlotOfTheDayThatCanBeSuccessfullyArranged(classId, slots, daysOfWeek, WeekNumberInYear);
    }

}
