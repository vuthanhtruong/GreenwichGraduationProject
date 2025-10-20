package com.example.demo.post.specializedAssignmentSubmitSlots.service;

import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.post.specializedAssignmentSubmitSlots.dao.SpecializedAssignmentSubmitSlotsDAO;
import com.example.demo.post.specializedAssignmentSubmitSlots.model.SpecializedAssignmentSubmitSlots;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecializedAssignmentSubmitSlotsServiceImpl implements SpecializedAssignmentSubmitSlotsService {
    private final SpecializedAssignmentSubmitSlotsDAO specializedAssignmentSubmitSlotsDAO;

    public SpecializedAssignmentSubmitSlotsServiceImpl(SpecializedAssignmentSubmitSlotsDAO specializedAssignmentSubmitSlotsDAO) {
        this.specializedAssignmentSubmitSlotsDAO = specializedAssignmentSubmitSlotsDAO;
    }

    @Override
    public List<SpecializedAssignmentSubmitSlots> getAllAssignmentSubmitSlotsByClass(SpecializedClasses majorClass) {
        return specializedAssignmentSubmitSlotsDAO.getAllAssignmentSubmitSlotsByClass(majorClass);
    }
}
