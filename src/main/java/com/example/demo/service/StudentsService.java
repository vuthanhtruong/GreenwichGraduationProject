package com.example.demo.service;

import com.example.demo.entity.Students;
import jakarta.mail.MessagingException;

import java.util.List;

public interface StudentsService {
    List<Students> getStudents();
    Students addStudents(Students students, String randomPassword);
    long numberOfStudents();
    void deleteStudent(String id);
    void updateStudent(String id, Students student) throws MessagingException;
    Students getStudentById(String id);
    List<Students> getPaginatedStudents(int firstResult, int pageSize);
}
