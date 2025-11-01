package com.example.demo.submission.service;

import com.example.demo.submission.dao.SubmissionsDAO;
import com.example.demo.submission.model.Submissions;
import com.example.demo.user.student.model.Students;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class SubmissionsServiceImpl implements SubmissionsService {
    @Override
    public void submitAssignment(Students student, String postId, List<MultipartFile> files) {
        submissionsDAO.submitAssignment(student, postId, files);
    }

    private final SubmissionsDAO submissionsDAO;

    public SubmissionsServiceImpl(SubmissionsDAO submissionsDAO) {
        this.submissionsDAO = submissionsDAO;
    }

    @Override
    public Submissions getSubmissionByStudentId(String studentId) {
        return submissionsDAO.getSubmissionByStudentId(studentId);
    }

    @Override
    public List<Submissions> getSubmissionsByClassId(String classId) {
        return submissionsDAO.getSubmissionsByClassId(classId);
    }

    @Override
    public List<Submissions> getSubmissionsByAssignment(String assignmentId) {
        return submissionsDAO.getSubmissionsByAssignment(assignmentId);
    }

    @Override
    public Submissions getSubmissionByStudentAndAssignment(String studentId, String assignmentId) {
        return submissionsDAO.getSubmissionByStudentAndAssignment(studentId, assignmentId);
    }

    @Override
    public void save(Submissions submission) {
        submissionsDAO.save(submission);
    }

    @Override
    public boolean exists(String studentId, String assignmentId) {
        return submissionsDAO.exists(studentId, assignmentId);
    }
}
