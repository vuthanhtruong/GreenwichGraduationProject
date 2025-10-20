package com.example.demo.post.specializedAssignmentSubmitSlots.service;

import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.post.specializedAssignmentSubmitSlots.model.SpecializedAssignmentSubmitSlots;

import java.util.List;

public interface SpecializedAssignmentSubmitSlotsService {
    List<SpecializedAssignmentSubmitSlots> getAllAssignmentSubmitSlotsByClass(SpecializedClasses majorClass);
}
