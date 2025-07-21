package com.example.demo.dao;

import com.example.demo.entity.*;

import java.util.List;

public interface StaffsDAO {
    Staffs getStaffs();
    List<Students> getAll();
    List<Classes> getClasses();
    List<Lecturers> getLecturers();
    Majors getMajors();
    Students addStudents(Students students);
    Lecturers addLecturers(Lecturers lecturers);
    long numberOfStudents ();
    long numberOfLecturers();
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsPersonById(String id);

}
