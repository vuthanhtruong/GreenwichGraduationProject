package com.example.demo.timtable.service;

import com.example.demo.timtable.dao.SlotsDAO;
import com.example.demo.timtable.model.Slots;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SlotsServiceImpl implements SlotsService {
    @Override
    public Slots getSlotById(String slotId) {
        return slotsDAO.getSlotById(slotId);
    }

    @Override
    public List<Slots> getSlots() {
        return slotsDAO.getSlots();
    }
    private final SlotsDAO slotsDAO;
    public SlotsServiceImpl(SlotsDAO slotsDAO, SlotsDAO slotsDAO1) {
        this.slotsDAO = slotsDAO1;
    }
}
