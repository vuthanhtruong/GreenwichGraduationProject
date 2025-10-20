package com.example.demo.post.specializedAssignmentSubmitSlots.dao;

import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.post.majorAssignmentSubmitSlots.model.AssignmentSubmitSlots;
import com.example.demo.post.specializedAssignmentSubmitSlots.model.SpecializedAssignmentSubmitSlots;

import java.util.List;

public interface SpecializedAssignmentSubmitSlotsDAO {
    List<SpecializedAssignmentSubmitSlots> getAllAssignmentSubmitSlotsByClass(SpecializedClasses majorClass);
}
