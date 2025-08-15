package com.example.demo.dao;
import com.example.demo.entity.MajorLecturers;
import jakarta.mail.MessagingException;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface LecturesDAO {
    List<MajorLecturers> getLecturers();
    MajorLecturers addLecturers(MajorLecturers lecturers, String randomPassword);
    long numberOfLecturers();
    void deleteLecturer(String id);
    void updateLecturer(String id, MajorLecturers lecturer) throws MessagingException;
    MajorLecturers getLecturerById(String id);
    List<MajorLecturers> getPaginatedLecturers(int firstResult, int pageSize);
    List<String> lectureValidation(MajorLecturers lecturer, MultipartFile avatarFile);
    String generateRandomPassword(int length);
    String generateUniqueLectureId(String majorId, LocalDate createdDate);
}
