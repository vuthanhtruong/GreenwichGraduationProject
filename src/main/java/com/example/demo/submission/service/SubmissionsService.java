package com.example.demo.submission.service;

import com.example.demo.submission.model.Submissions;
import com.example.demo.user.student.model.Students;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SubmissionsService {
    Submissions getSubmissionByStudentId(String studentId);
    List<Submissions> getSubmissionsByClassId(String classId);
    List<Submissions> getSubmissionsByAssignment(String assignmentId);
    Submissions getSubmissionByStudentAndAssignment(String studentId, String assignmentId);
    void save(Submissions submission);
    boolean exists(String studentId, String assignmentId);
    void submitAssignment(Students student, String postId, List<MultipartFile> files);
}
