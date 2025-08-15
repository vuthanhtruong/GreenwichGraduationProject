package com.example.demo.dao;
import com.example.demo.entity.Majors;
import com.example.demo.entity.Students;
import jakarta.mail.MessagingException;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface StudentsDAO {
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
