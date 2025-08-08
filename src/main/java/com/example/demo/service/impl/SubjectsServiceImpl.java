package com.example.demo.service.impl;

import com.example.demo.dao.SubjectsDAO;
import com.example.demo.entity.Majors;
import com.example.demo.entity.MajorSubjects;
import com.example.demo.service.SubjectsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectsServiceImpl implements SubjectsService {

    private final SubjectsDAO subjectsDAO;

    @Autowired
    public SubjectsServiceImpl(SubjectsDAO subjectsDAO) {
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