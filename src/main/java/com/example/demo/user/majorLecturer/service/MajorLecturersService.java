package com.example.demo.user.majorLecturer.service;

import com.example.demo.user.majorLecturer.model.MajorLecturers;
import com.example.demo.user.minorLecturer.model.MinorLecturers;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface MajorLecturersService {
    List<MajorLecturers> colleagueBycampusId(String campusId);
    MajorLecturers getMajorLecturer();
    long minorLecturersCountByCampus(String campusId);
    long lecturersCountByCampus(String campusId);
    String generateRandomPassword(int length);
    String generateUniqueLectureId(String majorId, LocalDate createdDate);
    Map<String, String> lectureValidation(MajorLecturers lecturer, MultipartFile avatarFile);
    List<MajorLecturers> getLecturers();
    MajorLecturers addLecturers(MajorLecturers lecturer, String randomPassword);
    long numberOfLecturersByCampus(String campusId);
    void deleteLecturer(String id);
    void updateLecturer(String id, MajorLecturers lecturer, MultipartFile avatarFile) throws Exception;
    MajorLecturers getLecturerById(String id);
    List<MajorLecturers> getPaginatedLecturersByCampus(String campusId, int firstResult, int pageSize);
    List<MajorLecturers> searchLecturersByCampus(String campusId, String searchType, String keyword, int firstResult, int pageSize);
    long countSearchResultsByCampus(String campusId, String searchType, String keyword);
    long countLecturersByCampus(String campusId);
    List<MinorLecturers> getPaginatedMinorLecturersByCampus(String campusId, int firstResult, int pageSize);
    MinorLecturers getMinorLecturerById(String id);
    long countMinorLecturersSearchResultsByCampus(String campusId, String searchType, String keyword);
    long countMajorLecturersSearchResultsByCampus(String campusId, String searchType, String keyword);
    List<MinorLecturers> searchMinorLecturersByCampus(String campusId, String searchType, String keyword, int firstResult, int pageSize);
    List<MajorLecturers> searchMajorLecturersByCampus(String campusId, String searchType, String keyword, int firstResult, int pageSize);
}
