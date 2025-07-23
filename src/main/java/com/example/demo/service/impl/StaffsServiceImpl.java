package com.example.demo.service.impl;

import com.example.demo.dao.StaffsDAO;
import com.example.demo.dto.LecturersDTO;
import com.example.demo.dto.StaffsDTO;
import com.example.demo.dto.StudentsDTO;
import com.example.demo.entity.*;
import com.example.demo.service.StaffsService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StaffsServiceImpl implements StaffsService {
    @Override
    public List<Lecturers> getPaginatedLecturers(int firstResult, int pageSize) {
        return staffsDAO.getPaginatedLecturers(firstResult, pageSize);
    }

    @Override
    public List<Students> getPaginatedStudents(int firstResult, int pageSize) {
        return staffsDAO.getPaginatedStudents(firstResult, pageSize);
    }

    @Override
    public boolean existsByPhoneNumberExcludingId(String phoneNumber, String id) {
        return staffsDAO.existsByPhoneNumberExcludingId(phoneNumber, id);
    }

    @Override
    public boolean existsByEmailExcludingId(String email, String id) {
        return staffsDAO.existsByEmailExcludingId(email, id);
    }

    @Override
    public Students getStudentById(String id) {
        return staffsDAO.getStudentById(id);
    }

    @Override
    public Lecturers getLecturerById(String id) {
        return staffsDAO.getLecturerById(id);
    }

    @Override
    public void updateLecturer(String id, Lecturers lecturer) throws MessagingException{
        staffsDAO.updateLecturer(id, lecturer);
    }

    @Override
    public void updateStudent(String id, Students student) throws MessagingException {
        staffsDAO.updateStudent(id, student);
    }

    @Override
    public void deleteStudent(String id) {
        staffsDAO.deleteStudent(id);
    }

    @Override
    public void deleteLecturer(String id) {
        staffsDAO.deleteLecturer(id);
    }

    @Override
    public boolean existsPersonById(String id) {
        return staffsDAO.existsPersonById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return staffsDAO.existsByEmail(email);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return staffsDAO.existsByPhoneNumber(phoneNumber);
    }

    @Override
    public Majors getMajors() {
        return staffsDAO.getMajors();
    }

    @Override
    public long numberOfStudents() {
        return staffsDAO.numberOfStudents();
    }

    @Override
    public long numberOfLecturers() {
        return staffsDAO.numberOfLecturers();
    }

    private final StaffsDAO staffsDAO;

    public StaffsServiceImpl(StaffsDAO staffsDAO) {
        this.staffsDAO = staffsDAO;
    }
    @Override
    public Staffs getStaffs() {
        return staffsDAO.getStaffs();
    }
    @Override
    public List<Students> getAll() {
        return staffsDAO.getAll();
    }

    @Override
    public List<Classes> getClasses() {
        return staffsDAO.getClasses();
    }

    @Override
    public List<Lecturers> getLecturers() {
        return staffsDAO.getLecturers();
    }

    @Override
    public Students addStudents(Students students, String randomPassword) {
        return staffsDAO.addStudents(students, randomPassword);
    }

    @Override
    public Lecturers addLecturers(Lecturers lecturers, String randomPassword) {
        return staffsDAO.addLecturers(lecturers, randomPassword);
    }

}
