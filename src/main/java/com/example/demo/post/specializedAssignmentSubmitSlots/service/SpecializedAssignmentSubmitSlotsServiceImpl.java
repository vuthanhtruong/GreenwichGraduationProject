package com.example.demo.post.specializedAssignmentSubmitSlots.service;

import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.post.specializedAssignmentSubmitSlots.dao.SpecializedAssignmentSubmitSlotsDAO;
import com.example.demo.post.specializedAssignmentSubmitSlots.model.SpecializedAssignmentSubmitSlots;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class SpecializedAssignmentSubmitSlotsServiceImpl implements SpecializedAssignmentSubmitSlotsService {
    @Override
    public List<SpecializedAssignmentSubmitSlots> getAllSpecializedAssignmentSubmitSlotsByClass(String classId) {
        return specializedAssignmentSubmitSlotsDAO.getAllSpecializedAssignmentSubmitSlotsByClass(classId);
    }

    @Override
    public List<SpecializedAssignmentSubmitSlots> getAllSpecializedAssignmentSubmitSlotsByClass(SpecializedClasses specializedClass) {
        return specializedAssignmentSubmitSlotsDAO.getAllSpecializedAssignmentSubmitSlotsByClass(specializedClass);
    }

    @Override
    public void save(SpecializedAssignmentSubmitSlots slot) {
        specializedAssignmentSubmitSlotsDAO.save(slot);
    }

    @Override
    public SpecializedAssignmentSubmitSlots findByPostId(String postId) {
        return specializedAssignmentSubmitSlotsDAO.findByPostId(postId);
    }

    @Override
    public boolean existsByPostId(String postId) {
        return specializedAssignmentSubmitSlotsDAO.existsByPostId(postId);
    }

    @Override
    public String generateUniquePostId(String classId, LocalDate date) {
        return specializedAssignmentSubmitSlotsDAO.generateUniquePostId(classId, date);
    }

    @Override
    public void deleteByPostId(String postId) {
        specializedAssignmentSubmitSlotsDAO.deleteByPostId(postId);
    }

    @Override
    public Map<String, String> validateSlot(SpecializedAssignmentSubmitSlots slot) {
        return specializedAssignmentSubmitSlotsDAO.validateSlot(slot);
    }

    private final SpecializedAssignmentSubmitSlotsDAO specializedAssignmentSubmitSlotsDAO;

    public SpecializedAssignmentSubmitSlotsServiceImpl(SpecializedAssignmentSubmitSlotsDAO specializedAssignmentSubmitSlotsDAO) {
        this.specializedAssignmentSubmitSlotsDAO = specializedAssignmentSubmitSlotsDAO;
    }

}
