package com.example.demo.timtable.majorTimetable.service;

import com.example.demo.timtable.majorTimetable.model.Slots;

import java.util.List;

public interface SlotsService {
    List<Slots> getSlots();
    Slots getSlotById(String slotId);
}
