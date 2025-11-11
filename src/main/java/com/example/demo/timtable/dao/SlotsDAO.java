package com.example.demo.timtable.dao;

import com.example.demo.timtable.model.Slots;

import java.util.List;

public interface SlotsDAO {
    List<Slots> getSlots();
    Slots getSlotById(String slotId);
}
