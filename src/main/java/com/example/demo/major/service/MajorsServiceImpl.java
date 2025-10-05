package com.example.demo.major.service;

import com.example.demo.major.dao.MajorsDAO;
import com.example.demo.major.model.Majors;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class MajorsServiceImpl implements MajorsService{
    @Override
    public List<Majors> existsMajorByName(String majorName) {
        return majorDAO.existsMajorByName(majorName);
    }

    @Override
    public Map<String, String> validateMajor(Majors major, MultipartFile avatarFile) {
        return majorDAO.validateMajor(major, avatarFile);
    }

    @Override
    public Map<String, String> validateMajor(Majors major) {
        return majorDAO.validateMajor(major);
    }

    @Override
    public Majors getMajorById(String majorId) {
        return majorDAO.getMajorById(majorId);
    }

    @Override
    public void addMajor(Majors major) {
        majorDAO.addMajor(major);
    }

    @Override
    public boolean existsMajorById(String majorId) {
        return majorDAO.existsMajorById(majorId);
    }

    @Override
    public void deleteMajor(String majorId) {
        majorDAO.deleteMajor(majorId);
    }

    @Override
    public void editMajor(Majors major, MultipartFile avatarFile) throws IOException {
        majorDAO.editMajor(major, avatarFile);
    }

    @Override
    public String generateUniqueMajorId(LocalDate createdDate) {
        return majorDAO.generateUniqueMajorId(createdDate);
    }

    @Override
    public void updateMajorFields(Majors existing, Majors updated) {
        majorDAO.updateMajorFields(existing, updated);
    }

    private final MajorsDAO majorDAO;

    public MajorsServiceImpl(MajorsDAO majorDAO) {
        this.majorDAO = majorDAO;
    }


    @Override
    public List<Majors> getMajors() {
        return majorDAO.getMajors();
    }
}
