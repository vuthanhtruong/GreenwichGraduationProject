package com.example.demo.service.impl;

import com.example.demo.dao.LecturesDAO;
import com.example.demo.entity.MajorLecturers;
import com.example.demo.service.LecturesService;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
public class LecturesServiceImpl implements LecturesService {
    @Override
    public List<String> lectureValidation(MajorLecturers lecturer, MultipartFile avatarFile, String excludeId) {
        return lecturesDAO.lectureValidation(lecturer, avatarFile, excludeId);
    }

    @Override
    public void updateLecturer(String id, MajorLecturers lecturer, MultipartFile avatarFile) throws MessagingException, IOException {
        lecturesDAO.updateLecturer(id, lecturer, avatarFile);
    }

    @Override
    public String generateRandomPassword(int length) {
        return lecturesDAO.generateRandomPassword(length);
    }

    @Override
    public String generateUniqueLectureId(String majorId, LocalDate createdDate) {
        return lecturesDAO.generateUniqueLectureId(majorId, createdDate);
    }

    private final LecturesDAO lecturesDAO;

    public LecturesServiceImpl(LecturesDAO lecturesDAO) {
        this.lecturesDAO = lecturesDAO;
    }

    @Override
    public List<MajorLecturers> getLecturers() {
        return lecturesDAO.getLecturers();
    }

    @Override
    public MajorLecturers addLecturers(MajorLecturers lecturers, String randomPassword) {
        return lecturesDAO.addLecturers(lecturers, randomPassword);
    }

    @Override
    public long numberOfLecturers() {
        return lecturesDAO.numberOfLecturers();
    }

    @Override
    public void deleteLecturer(String id) {
        lecturesDAO.deleteLecturer(id);
    }

    @Override
    public MajorLecturers getLecturerById(String id) {
        return lecturesDAO.getLecturerById(id);
    }

    @Override
    public List<MajorLecturers> getPaginatedLecturers(int firstResult, int pageSize) {
        return lecturesDAO.getPaginatedLecturers(firstResult, pageSize);
    }
}