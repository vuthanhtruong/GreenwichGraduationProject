package com.example.demo.post.majorAssignmentSubmitSlots.service;

import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.post.majorAssignmentSubmitSlots.dao.AssignmentSubmitSlotsDAO;
import com.example.demo.post.majorAssignmentSubmitSlots.model.AssignmentSubmitSlots;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssignmentSubmitSlotsServiceImpl implements AssignmentSubmitSlotsService {
    private AssignmentSubmitSlotsDAO assignmentSubmitSlotsDAO;
    @Override
    public List<AssignmentSubmitSlots> getAllAssignmentSubmitSlotsByClass(MajorClasses majorClass) {
        return assignmentSubmitSlotsDAO.getAllAssignmentSubmitSlotsByClass(majorClass);
    }
}
