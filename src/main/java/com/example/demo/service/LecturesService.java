package com.example.demo.service;

import com.example.demo.entity.MajorLecturers;
import jakarta.mail.MessagingException;

import java.util.List;

public interface LecturesService {
    List<MajorLecturers> getLecturers();
    MajorLecturers addLecturers(MajorLecturers lecturers, String randomPassword);
    long numberOfLecturers();
    void deleteLecturer(String id);
    void updateLecturer(String id, MajorLecturers lecturer) throws MessagingException;
    MajorLecturers getLecturerById(String id);
    List<MajorLecturers> getPaginatedLecturers(int firstResult, int pageSize);
}
