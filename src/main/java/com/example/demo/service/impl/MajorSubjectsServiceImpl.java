package com.example.demo.service.impl;

import com.example.demo.dao.MajorSubjectsDAO;
import com.example.demo.entity.Majors;
import com.example.demo.entity.MajorSubjects;
import com.example.demo.service.MajorSubjectsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MajorSubjectsServiceImpl implements MajorSubjectsService {
    @Override
    public String generateUniqueSubjectId(String majorId, LocalDate createdDate) {
        return subjectsDAO.generateUniqueSubjectId(majorId, createdDate);
    }

    @Override
    public List<String> validateSubject(MajorSubjects subject, String excludeId) {
        return List.of();
    }

    @Override
    public List<MajorSubjects> AcceptedSubjectsByMajor(Majors major) {
        return subjectsDAO.AcceptedSubjectsByMajor(major);
    }

    private final MajorSubjectsDAO subjectsDAO;

    @Autowired
    public MajorSubjectsServiceImpl(MajorSubjectsDAO subjectsDAO) {
        this.subjectsDAO = subjectsDAO;
    }

    @Override
    public void addSubject(MajorSubjects subject) {
        subjectsDAO.addSubject(subject);
    }

    @Override
    public MajorSubjects getSubjectById(String subjectId) {
        return subjectsDAO.getSubjectById(subjectId);
    }

    @Override
    public MajorSubjects getSubjectByName(String subjectName) {
        return subjectsDAO.getSubjectByName(subjectName);
    }

    @Override
    public MajorSubjects checkNameSubject(MajorSubjects subject) {
        return subjectsDAO.checkNameSubject(subject);
    }

    @Override
    public List<MajorSubjects> subjectsByMajor(Majors major) {
        return subjectsDAO.subjectsByMajor(major);
    }

    @Override
    public List<MajorSubjects> getSubjects() {
        return subjectsDAO.getSubjects();
    }

    @Override
    public MajorSubjects updateSubject(String id, MajorSubjects subject) {
        return subjectsDAO.updateSubject(id, subject);
    }

    @Override
    public void deleteSubject(String id) {
        subjectsDAO.deleteSubject(id);
    }
}