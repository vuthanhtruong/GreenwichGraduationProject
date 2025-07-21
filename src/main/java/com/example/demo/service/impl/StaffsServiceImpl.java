package com.example.demo.service.impl;

import com.example.demo.dao.StaffsDAO;
import com.example.demo.dto.LecturersDTO;
import com.example.demo.dto.StaffsDTO;
import com.example.demo.dto.StudentsDTO;
import com.example.demo.entity.*;
import com.example.demo.service.StaffsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StaffsServiceImpl implements StaffsService {
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
    public Students addStudents(Students students) {
        return staffsDAO.addStudents(students);
    }

    @Override
    public Lecturers addLecturers(Lecturers lecturers) {
        return staffsDAO.addLecturers(lecturers);
    }

}
