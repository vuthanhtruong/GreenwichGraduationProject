package com.example.demo.user.majorLecturer.service;

import com.example.demo.user.majorLecturer.dao.MajorLecturersDAO;
import com.example.demo.user.majorLecturer.model.MajorLecturers;
import com.example.demo.user.minorLecturer.model.MinorLecturers;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class MajorLecturersServiceImpl implements MajorLecturersService {

    private final MajorLecturersDAO lecturesDAO;

    public MajorLecturersServiceImpl(MajorLecturersDAO lecturesDAO) {
        this.lecturesDAO = lecturesDAO;
    }

    @Override
    public MajorLecturers getMajorLecturer() {
        return lecturesDAO.getMajorLecturer();
    }

    @Override
    public long minorLecturersCountByCampus(String campusId) {
        return lecturesDAO.minorLecturersCountByCampus(campusId);
    }

    @Override
    public long lecturersCountByCampus(String campusId) {
        return lecturesDAO.lecturersCountByCampus(campusId);
    }

    @Override
    public String generateRandomPassword(int length) {
        return lecturesDAO.generateRandomPassword(length);
    }

    @Override
    public String generateUniqueLectureId(String majorId, LocalDate createdDate) {
        return lecturesDAO.generateUniqueLectureId(majorId, createdDate);
    }

    @Override
    public Map<String, String> lectureValidation(MajorLecturers lecturer, MultipartFile avatarFile) {
        return lecturesDAO.lectureValidation(lecturer, avatarFile);
    }

    @Override
    public List<MajorLecturers> getLecturers() {
        return lecturesDAO.getLecturers();
    }

    @Override
    public MajorLecturers addLecturers(MajorLecturers lecturer, String randomPassword) {
        return lecturesDAO.addLecturers(lecturer, randomPassword);
    }

    @Override
    public long numberOfLecturersByCampus(String campusId) {
        return lecturesDAO.numberOfLecturersByCampus(campusId);
    }

    @Override
    public void deleteLecturer(String id) {
        lecturesDAO.deleteLecturer(id);
    }

    @Override
    public void updateLecturer(String id, MajorLecturers lecturer, MultipartFile avatarFile) throws Exception {
        lecturesDAO.updateLecturer(id, lecturer, avatarFile);
    }

    @Override
    public MajorLecturers getLecturerById(String id) {
        return lecturesDAO.getLecturerById(id);
    }

    @Override
    public List<MajorLecturers> getPaginatedLecturersByCampus(String campusId, int firstResult, int pageSize) {
        return lecturesDAO.getPaginatedLecturersByCampus(campusId, firstResult, pageSize);
    }

    @Override
    public List<MajorLecturers> searchLecturersByCampus(String campusId, String searchType, String keyword, int firstResult, int pageSize) {
        return lecturesDAO.searchLecturersByCampus(campusId, searchType, keyword, firstResult, pageSize);
    }

    @Override
    public long countSearchResultsByCampus(String campusId, String searchType, String keyword) {
        return lecturesDAO.countSearchResultsByCampus(campusId, searchType, keyword);
    }

    @Override
    public long countLecturersByCampus(String campusId) {
        return lecturesDAO.countLecturersByCampus(campusId);
    }

    @Override
    public List<MinorLecturers> getPaginatedMinorLecturersByCampus(String campusId, int firstResult, int pageSize) {
        return lecturesDAO.getPaginatedMinorLecturersByCampus(campusId, firstResult, pageSize);
    }

    @Override
    public MinorLecturers getMinorLecturerById(String id) {
        return lecturesDAO.getMinorLecturerById(id);
    }

    @Override
    public long countMinorLecturersSearchResultsByCampus(String campusId, String searchType, String keyword) {
        return lecturesDAO.countMinorLecturersSearchResultsByCampus(campusId, searchType, keyword);
    }

    @Override
    public long countMajorLecturersSearchResultsByCampus(String campusId, String searchType, String keyword) {
        return lecturesDAO.countMajorLecturersSearchResultsByCampus(campusId, searchType, keyword);
    }

    @Override
    public List<MinorLecturers> searchMinorLecturersByCampus(String campusId, String searchType, String keyword, int firstResult, int pageSize) {
        return lecturesDAO.searchMinorLecturersByCampus(campusId, searchType, keyword, firstResult, pageSize);
    }

    @Override
    public List<MajorLecturers> searchMajorLecturersByCampus(String campusId, String searchType, String keyword, int firstResult, int pageSize) {
        return lecturesDAO.searchMajorLecturersByCampus(campusId, searchType, keyword, firstResult, pageSize);
    }
}