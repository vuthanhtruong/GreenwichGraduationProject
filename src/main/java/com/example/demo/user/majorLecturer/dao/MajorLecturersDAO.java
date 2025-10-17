package com.example.demo.user.majorLecturer.dao;
import com.example.demo.user.majorLecturer.model.MajorLecturers;
import com.example.demo.user.majorLecturer.model.MinorLecturers;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface MajorLecturersDAO {
    MajorLecturers getMajorLecturer();
    long minorLecturersCountByCampus(String campus);
    long lecturersCountByCampus(String campus);
    String generateRandomPassword(int length);
    String generateUniqueLectureId(String majorId, LocalDate createdDate);
    Map<String, String> lectureValidation(MajorLecturers lecturer, MultipartFile avatarFile);
    List<MajorLecturers> getLecturers();
    MajorLecturers addLecturers(MajorLecturers lecturer, String randomPassword);
    long numberOfLecturers();
    void deleteLecturer(String id);
    void updateLecturer(String id, MajorLecturers lecturer, MultipartFile avatarFile) throws Exception;
    MajorLecturers getLecturerById(String id);
    List<MajorLecturers> getPaginatedLecturers(int firstResult, int pageSize);
    List<MajorLecturers> searchLecturers(String searchType, String keyword, int firstResult, int pageSize);
    long countSearchResults(String searchType, String keyword);
    long countLecturersByCampus(String campusId);
    List<MajorLecturers> getPaginatedLecturersByCampus(String campusId, int firstResult, int pageSize);
    List<MinorLecturers> getPaginatedMinorLecturersByCampus(String campusId, int firstResult, int pageSize);
    MinorLecturers getMinorLecturerById(String id);
    long countMinorLecturersSearchResultsByCampus(String campusId, String searchType, String keyword);
    long countMajorLecturersSearchResultsByCampus(String campusId, String searchType, String keyword);
    List<MinorLecturers> searchMinorLecturersByCampus(String campusId, String searchType, String keyword, int firstResult, int pageSize);
    List<MajorLecturers> searchMajorLecturersByCampus(String campusId, String searchType, String keyword, int firstResult, int pageSize);
}
