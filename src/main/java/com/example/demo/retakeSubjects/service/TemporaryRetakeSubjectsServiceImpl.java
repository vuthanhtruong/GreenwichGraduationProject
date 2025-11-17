package com.example.demo.retakeSubjects.service;

import com.example.demo.retakeSubjects.dao.TemporaryRetakeSubjectsDAO;
import com.example.demo.retakeSubjects.model.TemporaryRetakeSubjects;
import com.example.demo.user.student.model.Students;
import com.example.demo.subject.abstractSubject.model.Subjects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TemporaryRetakeSubjectsServiceImpl implements TemporaryRetakeSubjectsService {
    @Override
    public void deleteByStudentAndSubject(String studentId, String subjectId) {
        dao.deleteByStudentAndSubject(studentId, subjectId);
    }

    @Override
    public boolean exists(String studentId, String subjectId) {
        return dao.exists(studentId, subjectId);
    }

    private final TemporaryRetakeSubjectsDAO dao;

    @Autowired
    public TemporaryRetakeSubjectsServiceImpl(TemporaryRetakeSubjectsDAO dao) {
        this.dao = dao;
    }

    @Override
    public void addToTemporary(Students student, Subjects subject, String reason) {
        addToTemporary(student, subject, reason, null);
    }

    @Override
    public void addToTemporary(Students student, Subjects subject, String reason, String notes) {
        if (student == null || subject == null) {
            throw new IllegalArgumentException("Student và Subject không được null");
        }

        String studentId = student.getId();
        String subjectId = subject.getSubjectId();

        // Idempotent: nếu đã có rồi thì thôi, không thêm nữa
        if (dao.exists(studentId, subjectId)) {
            return;
        }

        TemporaryRetakeSubjects temp = new TemporaryRetakeSubjects(student, subject, reason, notes);
        dao.save(temp);
    }


    @Override
    public List<TemporaryRetakeSubjects> getAllPending() {
        return dao.findAllPending();
    }

    @Override
    public void markAsProcessed(String studentId, String subjectId) {
        dao.markAsProcessed(studentId, subjectId);
    }

    @Override
    public void cleanupProcessed() {
        dao.deleteProcessedRecords();
    }
}