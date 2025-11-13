package com.example.demo.timetable.majorTimetable.dao;

import com.example.demo.timetable.majorTimetable.model.Slots;

import java.util.List;

public interface SlotsDAO {
    List<Slots> getSlots();
    Slots getSlotById(String slotId);
}
