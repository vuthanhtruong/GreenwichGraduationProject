package com.example.demo.service;

import com.example.demo.entity.Lecturers;
import jakarta.mail.MessagingException;

import java.util.List;

public interface LecturesService {
    List<Lecturers> getLecturers();
    Lecturers addLecturers(Lecturers lecturers, String randomPassword);
    long numberOfLecturers();
    void deleteLecturer(String id);
    void updateLecturer(String id, Lecturers lecturer) throws MessagingException;
    Lecturers getLecturerById(String id);
    List<Lecturers> getPaginatedLecturers(int firstResult, int pageSize);
}
