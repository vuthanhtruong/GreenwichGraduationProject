package com.example.demo.dao;
import com.example.demo.entity.Classes;
import com.example.demo.entity.Majors;
import com.example.demo.entity.Students;
import jakarta.mail.MessagingException;

import java.util.List;

public interface StudentsDAO {
    List<Students> getStudents();
    Students addStudents(Students students, String randomPassword);
    long numberOfStudents();
    void deleteStudent(String id);
    void updateStudent(String id, Students student) throws MessagingException;
    Students getStudentById(String id);
    List<Students> getPaginatedStudents(int firstResult, int pageSize);
    Majors getMajors();
    Students dataStudent();
}
