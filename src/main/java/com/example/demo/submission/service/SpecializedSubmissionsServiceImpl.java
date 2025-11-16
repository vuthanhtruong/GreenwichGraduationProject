package com.example.demo.submission.service;

import com.example.demo.submission.dao.SpecializedSubmissionsDAO;
import com.example.demo.submission.model.SpecializedSubmissions;
import com.example.demo.submission.model.SpecializedSubmissionsId;
import com.example.demo.user.student.model.Students;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class SpecializedSubmissionsServiceImpl implements SpecializedSubmissionsService {
    @Override
    public void deleteByStudentAndSlot(String studentId, String slotId) {
        specializedSubmissionsDAO.deleteByStudentAndSlot(studentId, slotId);
    }

    @Override
    public List<SpecializedSubmissions> getSubmissionsByAssignment(String assignmentId) {
        return specializedSubmissionsDAO.getSubmissionsByAssignment(assignmentId);
    }

    @Override
    public List<Students> getStudentsNotSubmitted(String classId, String assignmentId) {
        return specializedSubmissionsDAO.getStudentsNotSubmitted(classId, assignmentId);
    }

    @Override
    public SpecializedSubmissions findById(SpecializedSubmissionsId id) {
        return specializedSubmissionsDAO.findById(id);
    }

    @Override
    public void submit(Students student, String postId, List<MultipartFile> files) {
        specializedSubmissionsDAO.submit(student, postId, files);
    }

    private final SpecializedSubmissionsDAO specializedSubmissionsDAO;

    public SpecializedSubmissionsServiceImpl(SpecializedSubmissionsDAO specializedSubmissionsDAO) {
        this.specializedSubmissionsDAO = specializedSubmissionsDAO;
    }

    @Override
    public void save(SpecializedSubmissions submission) {
        specializedSubmissionsDAO.save(submission);
    }

    @Override
    public SpecializedSubmissions getByStudentAndSlot(String studentId, String slotId) {
        return specializedSubmissionsDAO.getByStudentAndSlot(studentId, slotId);
    }

    @Override
    public List<SpecializedSubmissions> getBySlotId(String slotId) {
        return specializedSubmissionsDAO.getBySlotId(slotId);
    }

    @Override
    public boolean exists(String studentId, String slotId) {
        return specializedSubmissionsDAO.exists(studentId, slotId);
    }
}
