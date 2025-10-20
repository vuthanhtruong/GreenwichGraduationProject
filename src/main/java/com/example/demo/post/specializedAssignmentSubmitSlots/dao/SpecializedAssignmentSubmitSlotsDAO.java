package com.example.demo.post.specializedAssignmentSubmitSlots.dao;

import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.post.majorAssignmentSubmitSlots.model.AssignmentSubmitSlots;
import com.example.demo.post.specializedAssignmentSubmitSlots.model.SpecializedAssignmentSubmitSlots;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface SpecializedAssignmentSubmitSlotsDAO {
    List<SpecializedAssignmentSubmitSlots> getAllSpecializedAssignmentSubmitSlotsByClass(SpecializedClasses specializedClass);
    void save(SpecializedAssignmentSubmitSlots slot);
    SpecializedAssignmentSubmitSlots findByPostId(String postId);
    boolean existsByPostId(String postId);
    String generateUniquePostId(String classId, LocalDate date);
    void deleteByPostId(String postId);
    Map<String, String> validateSlot(SpecializedAssignmentSubmitSlots slot);
}
