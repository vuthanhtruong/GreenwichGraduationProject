// File: MinorLecturersDAO.java
package com.example.demo.user.minorLecturer.dao;

import com.example.demo.user.minorLecturer.model.MinorLecturers;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface MinorLecturersDAO {
    List<MinorLecturers> getMinorLecturers();
    MinorLecturers addMinorLecturers(MinorLecturers minorLecturer, String randomPassword);
    long numberOfMinorLecturers();
    void deleteMinorLecturer(String id);
    MinorLecturers getMinorLecturerById(String id);
    void updateMinorLecturer(String id, MinorLecturers minorLecturer, MultipartFile avatarFile) throws Exception;
    List<MinorLecturers> getPaginatedMinorLecturers(int firstResult, int pageSize);
    List<MinorLecturers> searchMinorLecturers(String searchType, String keyword, int firstResult, int pageSize);
    long countSearchResults(String searchType, String keyword);
    String generateRandomPassword(int length);
    String generateUniqueMinorLectureId(LocalDate createdDate);
    Map<String, String> minorLecturerValidation(MinorLecturers minorLecturer, MultipartFile avatarFile);
    List<MinorLecturers> colleagueBycampusId(String campusId);
    MinorLecturers getMinorLecturer();
    List<MinorLecturers> searchMinorLecturersByCampus(String campusId, String searchType, String keyword, int firstResult, int pageSize);
    long countSearchMinorLecturersByCampus(String campusId, String searchType, String keyword);
    List<MinorLecturers> colleaguesByCampusId(String campusId); // ĐÃ SỬA TÊN
}