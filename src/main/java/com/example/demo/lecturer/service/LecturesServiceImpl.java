package com.example.demo.lecturer.service;

import com.example.demo.lecturer.dao.LecturesDAO;
import com.example.demo.lecturer.model.MajorLecturers;
import com.example.demo.lecturer.model.MinorLecturers;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class LecturesServiceImpl implements LecturesService {
    @Override
    public MinorLecturers getMinorLecturerById(String id) {
        return lecturesDAO.getMinorLecturerById(id);
    }

    @Override
    public List<MinorLecturers> getPaginatedMinorLecturersByCampus(String campusId, int firstResult, int pageSize) {
        return lecturesDAO.getPaginatedMinorLecturersByCampus(campusId, firstResult, pageSize);
    }

    @Override
    public List<MajorLecturers> getPaginatedLecturersByCampus(String campusId, int firstResult, int pageSize) {
        return lecturesDAO.getPaginatedLecturersByCampus(campusId, firstResult, pageSize);
    }

    @Override
    public long countLecturersByCampus(String campusId) {
        return lecturesDAO.countLecturersByCampus(campusId);
    }

    @Override
    public Map<String, String> lectureValidation(MajorLecturers lecturer, MultipartFile avatarFile) {
        return lecturesDAO.lectureValidation(lecturer, avatarFile);
    }

    @Override
    public long lecturersCountByCampus(String campus) {
        return lecturesDAO.lecturersCountByCampus(campus);
    }

    @Override
    public long minorLecturersCountByCampus(String campus) {
        return lecturesDAO.minorLecturersCountByCampus(campus);
    }

    @Override
    public List<MajorLecturers> searchLecturers(String searchType, String keyword, int firstResult, int pageSize) {
        return lecturesDAO.searchLecturers(searchType, keyword, firstResult, pageSize);
    }

    @Override
    public long countSearchResults(String searchType, String keyword) {
        return lecturesDAO.countSearchResults(searchType, keyword);
    }

    @Override
    public void updateLecturer(String id, MajorLecturers lecturer, MultipartFile avatarFile) throws Exception {
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