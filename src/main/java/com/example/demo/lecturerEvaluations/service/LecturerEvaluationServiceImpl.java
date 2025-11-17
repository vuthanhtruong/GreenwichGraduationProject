// src/main/java/com/example/demo/lecturerEvaluations/service/LecturerEvaluationServiceImpl.java
package com.example.demo.lecturerEvaluations.service;

import com.example.demo.classes.abstractClasses.model.Classes;
import com.example.demo.lecturerEvaluations.dao.LecturerEvaluationDAO;
import com.example.demo.lecturerEvaluations.model.LecturerEvaluations;
import com.example.demo.lecturerEvaluations.model.MajorLecturerEvaluations;
import com.example.demo.lecturerEvaluations.model.MinorLecturerEvaluations;
import com.example.demo.user.majorLecturer.model.MajorLecturers;
import com.example.demo.user.minorLecturer.model.MinorLecturers;
import com.example.demo.user.student.model.Students;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LecturerEvaluationServiceImpl implements LecturerEvaluationService {
    @Override
    public List<LecturerEvaluations> findByClassIdByStudentId(String classId, String studentId) {
        return dao.findByClassIdByStudentId(classId, studentId);
    }

    @Override
    public List<MajorLecturerEvaluations> findAllMajorLecturerEvaluationsByCampus(String campus) {
        return dao.findAllMajorLecturerEvaluationsByCampus(campus);
    }

    @Override
    public List<MinorLecturerEvaluations> findAllMinorLecturerEvaluationsByCampus(String campus) {
        return dao.findAllMinorLecturerEvaluationsByCampus(campus);
    }

    @Override
    public MajorLecturerEvaluations addMajorLecturerEvaluation(Students reviewer, Classes classEntity, MajorLecturers lecturer, String text) {
        return dao.addMajorLecturerEvaluation(reviewer, classEntity, lecturer, text);
    }

    @Override
    public MinorLecturerEvaluations addMinorLecturerEvaluation(Students reviewer, Classes classEntity, MinorLecturers lecturer, String text) {
        return dao.addMinorLecturerEvaluation(reviewer, classEntity, lecturer, text);
    }

    private final LecturerEvaluationDAO dao;

    public LecturerEvaluationServiceImpl(LecturerEvaluationDAO dao) {
        this.dao = dao;
    }

    @Override
    public <T extends LecturerEvaluations> T save(T evaluation) {
        return dao.save(evaluation);
    }

    @Override
    public LecturerEvaluations findById(String evaluationId) {
        return dao.findById(evaluationId);
    }

    @Override
    public List<LecturerEvaluations> findAll() {
        return dao.findAll();
    }

    @Override
    public List<LecturerEvaluations> findByStudentId(String studentId) {
        return dao.findByStudentId(studentId);
    }


    @Override
    public List<MajorLecturerEvaluations> findMajorByLecturerId(String lecturerId) {
        return dao.findMajorByLecturerId(lecturerId);
    }

    @Override
    public List<MinorLecturerEvaluations> findMinorByLecturerId(String lecturerId) {
        return dao.findMinorByLecturerId(lecturerId);
    }

    @Override
    public List<LecturerEvaluations> findAllByLecturerId(String lecturerId) {
        return dao.findAllByLecturerId(lecturerId);
    }

    @Override
    public long countByLecturerId(String lecturerId) {
        return dao.countByLecturerId(lecturerId);
    }

    @Override
    public long countByStudentId(String studentId) {
        return dao.countByStudentId(studentId);
    }

    @Override
    public void deleteById(String evaluationId) {
        dao.deleteById(evaluationId);
    }

    // Các method tiện ích (giữ lại tên cũ để controller cũ không lỗi)
    public List<LecturerEvaluations> getAllEvaluations() {
        return findAll();
    }

    public List<LecturerEvaluations> getByStudent(String studentId) {
        return findByStudentId(studentId);
    }

    public List<LecturerEvaluations> getByLecturer(String lecturerId) {
        return findAllByLecturerId(lecturerId);
    }

    public long countEvaluationsByLecturer(String lecturerId) {
        return countByLecturerId(lecturerId);
    }

    public void delete(String evaluationId) {
        deleteById(evaluationId);
    }
}