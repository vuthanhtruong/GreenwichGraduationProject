package com.example.demo.retakeSubjects.dao;

import com.example.demo.retakeSubjects.model.TemporaryRetakeSubjects;
import com.example.demo.studentRequiredMajorSubjects.model.StudentRetakeSubjectsId;

import java.util.List;

public interface TemporaryRetakeSubjectsDAO {

    void save(TemporaryRetakeSubjects entity);

    TemporaryRetakeSubjects findById(StudentRetakeSubjectsId id);

    // ← ĐÂY LÀ METHOD BẠN ĐANG THIẾU!
    boolean exists(String studentId, String subjectId);

    List<TemporaryRetakeSubjects> findAllPending();

    List<TemporaryRetakeSubjects> findByStudentId(String studentId);

    void markAsProcessed(String studentId, String subjectId);

    void deleteProcessedRecords();
    void deleteByStudentAndSubject(String studentId, String subjectId);
}