package com.example.demo.post.majorAssignmentSubmitSlots.service;

import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.post.majorAssignmentSubmitSlots.dao.AssignmentSubmitSlotsDAO;
import com.example.demo.post.majorAssignmentSubmitSlots.model.AssignmentSubmitSlots;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class AssignmentSubmitSlotsServiceImpl implements AssignmentSubmitSlotsService {
    @Override
    public Map<String, String> validateSlot(AssignmentSubmitSlots slot) {
        return assignmentSubmitSlotsDAO.validateSlot(slot);
    }

    @Override
    public String generateUniquePostId(String classId, LocalDate date) {
        return assignmentSubmitSlotsDAO.generateUniquePostId(classId, date);
    }

    @Override
    public void deleteByPostId(String postId) {
        assignmentSubmitSlotsDAO.deleteByPostId(postId);
    }

    @Override
    public AssignmentSubmitSlots findByPostId(String postId) {
        return assignmentSubmitSlotsDAO.findByPostId(postId);
    }

    @Override
    public boolean existsByPostId(String postId) {
        return assignmentSubmitSlotsDAO.existsByPostId(postId);
    }

    @Override
    public void save(AssignmentSubmitSlots slot) {
        assignmentSubmitSlotsDAO.save(slot);
    }

    private AssignmentSubmitSlotsDAO assignmentSubmitSlotsDAO;
    @Override
    public List<AssignmentSubmitSlots> getAllAssignmentSubmitSlotsByClass(MajorClasses majorClass) {
        return assignmentSubmitSlotsDAO.getAllAssignmentSubmitSlotsByClass(majorClass);
    }
}
