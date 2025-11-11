package com.example.demo.timtable.majorTimetable.dao;

import com.example.demo.timtable.majorTimetable.model.Slots;

import java.util.List;

public interface SlotsDAO {
    List<Slots> getSlots();
    Slots getSlotById(String slotId);
}
