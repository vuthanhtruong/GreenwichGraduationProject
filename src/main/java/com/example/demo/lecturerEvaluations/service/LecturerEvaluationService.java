// src/main/java/com/example/demo/lecturerEvaluations/service/LecturerEvaluationService.java
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

public interface LecturerEvaluationService {

    <T extends LecturerEvaluations> T save(T evaluation);

    // Find
    LecturerEvaluations findById(String evaluationId);
    List<LecturerEvaluations> findAll();
    List<LecturerEvaluations> findByStudentId(String studentId);
    List<LecturerEvaluations> findByClassIdByStudentId(String classId, String studentId);

    // Theo loại giảng viên
    List<MajorLecturerEvaluations> findMajorByLecturerId(String lecturerId);
    List<MinorLecturerEvaluations> findMinorByLecturerId(String lecturerId);

    // Tổng hợp tất cả đánh giá của 1 giảng viên (Major + Minor)
    List<LecturerEvaluations> findAllByLecturerId(String lecturerId);

    // Đếm số đánh giá
    long countByLecturerId(String lecturerId);
    long countByStudentId(String studentId);

    // Xóa
    void deleteById(String evaluationId);
    MajorLecturerEvaluations addMajorLecturerEvaluation(
            Students reviewer,
            Classes classEntity,
            MajorLecturers lecturer,
            String text);
    MinorLecturerEvaluations addMinorLecturerEvaluation(
            Students reviewer,
            Classes classEntity,
            MinorLecturers lecturer,
            String text);
    List<MajorLecturerEvaluations> findAllMajorLecturerEvaluationsByCampus(String campus);
    // LecturerEvaluationService.java
    List<MinorLecturerEvaluations> findAllMinorLecturerEvaluationsByCampus(String campus);
}