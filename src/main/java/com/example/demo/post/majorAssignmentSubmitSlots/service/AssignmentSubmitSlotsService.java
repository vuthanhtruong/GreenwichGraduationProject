package com.example.demo.post.majorAssignmentSubmitSlots.service;

import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.post.majorAssignmentSubmitSlots.model.AssignmentSubmitSlots;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AssignmentSubmitSlotsService {
    List<AssignmentSubmitSlots> getAllAssignmentSubmitSlotsByClass(MajorClasses majorClass);
    String generateUniquePostId(String classId, LocalDate date);
    void deleteByPostId(String postId);
    AssignmentSubmitSlots findByPostId(String postId);
    boolean existsByPostId(String postId);
    void save(AssignmentSubmitSlots slot);
    Map<String, String> validateSlot(AssignmentSubmitSlots slot);
    List<AssignmentSubmitSlots> getAssignmentSubmitSlotsByClass(String majorClass);
}
