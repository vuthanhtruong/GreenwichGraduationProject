package com.example.demo.timetable.majorTimetable.service;

import com.example.demo.timetable.majorTimetable.model.Slots;

import java.util.List;

public interface SlotsService {
    List<Slots> getSlots();
    Slots getSlotById(String slotId);
}
