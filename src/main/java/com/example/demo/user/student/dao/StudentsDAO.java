package com.example.demo.user.student.dao;

import com.example.demo.curriculum.model.Curriculum;
import com.example.demo.major.model.Majors;
import com.example.demo.specialization.model.Specialization;
import com.example.demo.user.student.model.Students;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface StudentsDAO {
    Students findById(String studentId);
    long totalStudentsByCampus(String campusId);
    String generateRandomPassword(int length);
    String generateUniqueStudentId(String majorId, LocalDate createdDate);
    Map<String, String> StudentValidation(Students student, MultipartFile avatarFile);
    Students getStudent();
    Majors getStudentMajor();
    List<Students> getStudents();
    Students addStudents(Students students, Curriculum curriculum, Specialization specialization, String randomPassword);
    long numberOfStudentsByCampus(String campusId);
    void deleteStudent(String id);
    void editStudent(String id, Curriculum curriculum, Specialization specialization, Students student) throws jakarta.mail.MessagingException;
    Students getStudentById(String id);
    List<Students> getPaginatedStudentsByCampus(String campusId, int firstResult, int pageSize);
    List<Students> searchStudentsByCampus(String campusId, String searchType, String keyword, int firstResult, int pageSize);
    long countSearchResultsByCampus(String campusId, String searchType, String keyword);
    List<Integer> getUniqueAdmissionYearsByCampus(String campusId);
}