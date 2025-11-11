package com.example.demo.timtable.service;

import com.example.demo.timtable.model.Slots;

import java.util.List;

public interface SlotsService {
    List<Slots> getSlots();
    Slots getSlotById(String slotId);
}
