package com.example.demo.dao;

import com.example.demo.entity.*;
import jakarta.mail.MessagingException;

import java.util.List;

public interface StaffsDAO {
    Staffs getStaffs();
    List<Students> getAll();
    List<Classes> getClasses();
    List<Lecturers> getLecturers();
    Majors getMajors();
    Students addStudents(Students students, String randomPassword);
    Lecturers addLecturers(Lecturers lecturers, String randomPassword);
    long numberOfStudents ();
    long numberOfLecturers();
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsPersonById(String id);
    void deleteStudent(String id);
    void deleteLecturer(String id);
    void updateLecturer(String id, Lecturers lecturer) throws MessagingException;
    void updateStudent(String id,  Students student) throws MessagingException;
    Students getStudentById(String id);
    Lecturers getLecturerById(String id);
    boolean existsByEmailExcludingId(String email, String id);
    boolean existsByPhoneNumberExcludingId(String phoneNumber, String id);
    List<Students> getPaginatedStudents(int firstResult, int pageSize);
    List<Lecturers> getPaginatedLecturers(int firstResult, int pageSize);

}
