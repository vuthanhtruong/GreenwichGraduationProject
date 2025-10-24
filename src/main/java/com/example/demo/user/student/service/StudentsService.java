package com.example.demo.user.student.service;

import com.example.demo.curriculum.model.Curriculum;
import com.example.demo.specialization.model.Specialization;
import com.example.demo.user.student.model.Students;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface StudentsService {
    Students findById(String studentId);
    long totalStudentsByCampus(String campusId);
    String generateRandomPassword(int length);
    String generateUniqueStudentId(String majorId, java.time.LocalDate createdDate);
    Map<String, String> StudentValidation(Students student, MultipartFile avatarFile);
    Students getStudent();
    com.example.demo.major.model.Majors getStudentMajor();
    List<Students> getStudents();
    Students addStudents(Students students, Curriculum curriculum, Specialization specialization, String randomPassword);
    long numberOfStudents();
    void deleteStudent(String id);
    void editStudent(String id, Curriculum curriculum,Specialization specialization,Students student) throws jakarta.mail.MessagingException;
    Students getStudentById(String id);
    List<Students> getPaginatedStudents(int firstResult, int pageSize);
    List<Students> searchStudents(String searchType, String keyword, int firstResult, int pageSize);
    long countSearchResults(String searchType, String keyword);
    List<Students> getPaginatedStudentsByCampus(String campusId, int firstResult, int pageSize);
    List<Integer> getUniqueAdmissionYears();
    List<Students> searchStudentsByCampus(String campusId, String searchType, String keyword, int firstResult, int pageSize);
    Long countSearchResultsByCampus(String campusId, String searchType, String keyword);
}
