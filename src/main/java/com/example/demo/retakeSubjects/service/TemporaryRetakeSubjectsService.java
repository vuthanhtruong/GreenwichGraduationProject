package com.example.demo.retakeSubjects.service;

import com.example.demo.retakeSubjects.model.TemporaryRetakeSubjects;
import com.example.demo.user.student.model.Students;
import com.example.demo.subject.abstractSubject.model.Subjects;

import java.util.List;

public interface TemporaryRetakeSubjectsService {

    void addToTemporary(Students student, Subjects subject, String reason);

    void addToTemporary(Students student, Subjects subject, String reason, String notes);
    List<TemporaryRetakeSubjects> getAllPending();

    void markAsProcessed(String studentId, String subjectId);

    void cleanupProcessed();
    boolean exists(String studentId, String subjectId);
    void deleteByStudentAndSubject(String studentId, String subjectId);
}