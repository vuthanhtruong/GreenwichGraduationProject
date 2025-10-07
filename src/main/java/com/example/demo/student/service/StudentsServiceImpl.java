package com.example.demo.student.service;

import com.example.demo.Curriculum.model.Curriculum;
import com.example.demo.Specialization.model.Specialization;
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
    public Students addStudents(Students students, Curriculum curriculum, Specialization specialization, String randomPassword) {
        return addStudents(students, curriculum,specialization, randomPassword);
    }

    @Override
    public Long countSearchResultsByCampus(String campusId, String searchType, String keyword) {
        return studentsDAO.countSearchResultsByCampus(campusId, searchType, keyword);
    }

    @Override
    public List<Students> searchStudentsByCampus(String campusId, String searchType, String keyword, int firstResult, int pageSize) {
        return studentsDAO.searchStudentsByCampus(campusId, searchType, keyword, firstResult, pageSize);
    }

    @Override
    public List<Integer> getUniqueAdmissionYears() {
        return studentsDAO.getUniqueAdmissionYears();
    }

    @Override
    public List<Students> getPaginatedStudentsByCampus(String campusId, int firstResult, int pageSize) {
        return studentsDAO.getPaginatedStudentsByCampus(campusId, firstResult, pageSize);
    }

    @Override
    public Students findById(String studentId) {
        return studentsDAO.findById(studentId);
    }
    @Override
    public long totalStudentsByCampus(String campus) {
        return studentsDAO.totalStudentsByCampus(campus);
    }

    @Override
    public long countSearchResults(String searchType, String keyword) {
        return studentsDAO.countSearchResults(searchType, keyword);
    }

    @Override
    public List<Students> searchStudents(String searchType, String keyword, int firstResult, int pageSize) {
        return studentsDAO.searchStudents(searchType, keyword, firstResult, pageSize);
    }

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
    public long numberOfStudents() {
        return studentsDAO.numberOfStudents();
    }

    @Override
    public void deleteStudent(String id) {
        studentsDAO.deleteStudent(id);
    }

    @Override
    public void editStudent(String id, Curriculum curriculum, Specialization specialization,Students student) throws MessagingException {
        studentsDAO.editStudent(id, curriculum, specialization,student);
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