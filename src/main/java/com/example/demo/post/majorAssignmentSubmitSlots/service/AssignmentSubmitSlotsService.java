package com.example.demo.post.majorAssignmentSubmitSlots.service;

import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.post.majorAssignmentSubmitSlots.model.AssignmentSubmitSlots;

import java.util.List;

public interface AssignmentSubmitSlotsService {
    List<AssignmentSubmitSlots> getAllAssignmentSubmitSlotsByClass(MajorClasses majorClass);
}
