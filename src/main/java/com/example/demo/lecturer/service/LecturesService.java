package com.example.demo.lecturer.service;

import com.example.demo.lecturer.model.MajorLecturers;
import com.example.demo.lecturer.model.MinorLecturers;
import jakarta.mail.MessagingException;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface LecturesService {
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
}
