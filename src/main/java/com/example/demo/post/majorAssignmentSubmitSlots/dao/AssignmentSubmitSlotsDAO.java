package com.example.demo.post.majorAssignmentSubmitSlots.dao;

import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.post.majorAssignmentSubmitSlots.model.AssignmentSubmitSlots;

import java.util.List;

public interface AssignmentSubmitSlotsDAO {
    List<AssignmentSubmitSlots> getAllAssignmentSubmitSlotsByClass(MajorClasses majorClass);
}
