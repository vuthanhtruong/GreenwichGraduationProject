package com.example.demo.user.student.service;

import com.example.demo.curriculum.model.Curriculum;
import com.example.demo.specialization.model.Specialization;
import com.example.demo.user.student.dao.StudentsDAO;
import com.example.demo.major.model.Majors;
import com.example.demo.user.student.model.Students;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class StudentsServiceImpl implements StudentsService {
    @Override
    public List<Students> getStudentsByCampusAndMajor(String campusId, String majorId) {
        return studentsDAO.getStudentsByCampusAndMajor(campusId, majorId);
    }

    @Override
    public List<Students> getPaginatedStudentsByCampusAndMajor(String campusId, String majorId, int firstResult, int pageSize) {
        return studentsDAO.getPaginatedStudentsByCampusAndMajor(campusId, majorId, firstResult, pageSize);
    }

    @Override
    public List<Students> searchStudentsByCampusAndMajor(String campusId, String majorId, String searchType, String keyword, int firstResult, int pageSize) {
        return studentsDAO.searchStudentsByCampusAndMajor(campusId, majorId, searchType, keyword, firstResult, pageSize);
    }

    @Override
    public long countSearchResultsByCampusAndMajor(String campusId, String majorId, String searchType, String keyword) {
        return studentsDAO.countSearchResultsByCampusAndMajor(campusId, majorId, searchType, keyword);
    }

    @Override
    public long totalStudentsByCampusAndMajor(String campusId, String majorId) {
        return studentsDAO.totalStudentsByCampusAndMajor(campusId, majorId);
    }

    private final StudentsDAO studentsDAO;

    public StudentsServiceImpl(StudentsDAO studentsDAO) {
        this.studentsDAO = studentsDAO;
    }

    @Override
    public Map<String, String> StudentValidation(Students student, MultipartFile avatarFile) {
        return studentsDAO.StudentValidation(student, avatarFile);
    }

    @Override
    public Students addStudents(Students students, Curriculum curriculum, Specialization specialization, String randomPassword) {
        return studentsDAO.addStudents(students, curriculum, specialization, randomPassword);
    }

    @Override
    public long countSearchResultsByCampus(String campusId, String searchType, String keyword) {
        return studentsDAO.countSearchResultsByCampus(campusId, searchType, keyword);
    }

    @Override
    public List<Students> searchStudentsByCampus(String campusId, String searchType, String keyword, int firstResult, int pageSize) {
        return studentsDAO.searchStudentsByCampus(campusId, searchType, keyword, firstResult, pageSize);
    }

    @Override
    public List<Integer> getUniqueAdmissionYearsByCampus(String campusId) {
        return studentsDAO.getUniqueAdmissionYearsByCampus(campusId);
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
    public long totalStudentsByCampus(String campusId) {
        return studentsDAO.totalStudentsByCampus(campusId);
    }

    @Override
    public long numberOfStudentsByCampus(String campusId) {
        return studentsDAO.numberOfStudentsByCampus(campusId);
    }

    @Override
    public List<Students> getStudents() {
        return studentsDAO.getStudents();
    }

    @Override
    public void deleteStudent(String id) {
        studentsDAO.deleteStudent(id);
    }

    @Override
    public void editStudent(String id, Curriculum curriculum, Specialization specialization, Students student) throws MessagingException {
        studentsDAO.editStudent(id, curriculum, specialization, student);
    }

    @Override
    public Students getStudentById(String id) {
        return studentsDAO.getStudentById(id);
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
    public Students getStudent() {
        return studentsDAO.getStudent();
    }

    @Override
    public Majors getStudentMajor() {
        return studentsDAO.getStudentMajor();
    }
}