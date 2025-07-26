package com.example.demo.service.impl;

import com.example.demo.dao.StudentsDAO;
import com.example.demo.entity.Majors;
import com.example.demo.entity.Students;
import com.example.demo.service.StudentsService;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentsServiceImpl implements StudentsService {
    @Override
    public Students dataStudent() {
        return studentsDAO.dataStudent();
    }

    @Override
    public Majors getMajors() {
        return studentsDAO.getMajors();
    }

    private final StudentsDAO studentsDAO;

    public StudentsServiceImpl(StudentsDAO studentsDAO) {
        this.studentsDAO = studentsDAO;
    }

    @Override
    public List<Students> getStudents() {
        return studentsDAO.getStudents();
    }

    @Override
    public Students addStudents(Students students, String randomPassword) {
        return studentsDAO.addStudents(students, randomPassword);
    }

    @Override
    public long numberOfStudents() {
        return studentsDAO.numberOfStudents();
    }

    @Override
    public void deleteStudent(String id) {
        studentsDAO.deleteStudent(id);
    }

    @Override
    public void updateStudent(String id, Students student) throws MessagingException {
        studentsDAO.updateStudent(id, student);
    }

    @Override
    public Students getStudentById(String id) {
        return studentsDAO.getStudentById(id);
    }

    @Override
    public List<Students> getPaginatedStudents(int firstResult, int pageSize) {
        return studentsDAO.getPaginatedStudents(firstResult, pageSize);
    }
}