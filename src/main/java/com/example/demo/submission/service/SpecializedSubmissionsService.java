package com.example.demo.submission.service;

import com.example.demo.submission.model.SpecializedSubmissions;
import com.example.demo.submission.model.SpecializedSubmissionsId;
import com.example.demo.user.student.model.Students;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SpecializedSubmissionsService {
    void save(SpecializedSubmissions submission);
    SpecializedSubmissions getByStudentAndSlot(String studentId, String slotId);
    List<SpecializedSubmissions> getBySlotId(String slotId);
    boolean exists(String studentId, String slotId);
    void submit(Students student, String postId, List<MultipartFile> files);
    SpecializedSubmissions findById(SpecializedSubmissionsId id);
    List<SpecializedSubmissions> getSubmissionsByAssignment(String assignmentId);
    List<Students> getStudentsNotSubmitted(String classId, String assignmentId);
}
