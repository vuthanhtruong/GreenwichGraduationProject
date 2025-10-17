package com.example.demo.post.assignmentSubmitSlots.dao;

import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.post.assignmentSubmitSlots.model.AssignmentSubmitSlots;

import java.util.List;

public interface AssignmentSubmitSlotsDAO {
    List<AssignmentSubmitSlots> getAllAssignmentSubmitSlotsByClass(MajorClasses majorClass);
}
