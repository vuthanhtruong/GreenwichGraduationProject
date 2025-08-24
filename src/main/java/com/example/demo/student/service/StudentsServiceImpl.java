package com.example.demo.student.service;

import com.example.demo.student.dao.StudentsDAO;
import com.example.demo.major.model.Majors;
import com.example.demo.student.model.Students;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Service
public class StudentsServiceImpl implements StudentsService {
    @Override
    public String generateUniqueStudentId(String majorId, LocalDate createdDate) {
        return studentsDAO.generateUniqueStudentId(majorId, createdDate);
    }

    @Override
    public String generateRandomPassword(int length) {
        return studentsDAO.generateRandomPassword(length);
    }

    @Override
    public List<String> StudentValidation(Students student, MultipartFile avatarFile) {
        return studentsDAO.StudentValidation(student, avatarFile);
    }

    @Override
    public Students getStudent() {
        return studentsDAO.getStudent();
    }

    @Override
    public Majors getStudentMajor() {
        return studentsDAO.getStudentMajor();
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
    public void editStudent(String id, Students student) throws MessagingException {
        studentsDAO.editStudent(id, student);
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