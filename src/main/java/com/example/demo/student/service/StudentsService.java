package com.example.demo.student.service;

import com.example.demo.major.model.Majors;
import com.example.demo.student.model.Students;
import jakarta.mail.MessagingException;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface StudentsService {
    List<Students> getStudents();
    Students addStudents(Students students, String randomPassword);
    long numberOfStudents();
    void deleteStudent(String id);
    void updateStudent(String id, Students student) throws MessagingException;
    Students getStudentById(String id);
    List<Students> getPaginatedStudents(int firstResult, int pageSize);
    Majors getStudentMajor();
    Students getStudent();
    List<String> StudentValidation(Students student, MultipartFile avatarFile);
    String generateUniqueStudentId(String majorId, LocalDate createdDate);
    String generateRandomPassword(int length);
}
