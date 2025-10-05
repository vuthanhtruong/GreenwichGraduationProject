package com.example.demo.major.dao;

import com.example.demo.major.model.Majors;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface MajorsDAO {
    List<Majors> getMajors();
    List<Majors> existsMajorByName(String majorName);
    Map<String, String> validateMajor(Majors major, MultipartFile avatarFile);
    Map<String, String> validateMajor(Majors major);
    Majors getMajorById(String majorId);
    void addMajor(Majors major);
    boolean existsMajorById(String majorId);
    void deleteMajor(String majorId);
    void editMajor(Majors major, MultipartFile avatarFile) throws IOException;
    String generateUniqueMajorId(LocalDate createdDate);
    void updateMajorFields(Majors existing, Majors updated);
}
