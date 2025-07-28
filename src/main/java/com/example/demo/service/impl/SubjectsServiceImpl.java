package com.example.demo.service.impl;

import com.example.demo.dao.SubjectsDAO;
import com.example.demo.entity.Majors;
import com.example.demo.entity.Subjects;
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
    public void addSubject(Subjects subject) {
        subjectsDAO.addSubject(subject);
    }

    @Override
    public Subjects getSubjectById(String subjectId) {
        return subjectsDAO.getSubjectById(subjectId);
    }

    @Override
    public Subjects getSubjectByName(String subjectName) {
        return subjectsDAO.getSubjectByName(subjectName);
    }

    @Override
    public Subjects checkNameSubject(Subjects subject) {
        return subjectsDAO.checkNameSubject(subject);
    }

    @Override
    public List<Subjects> subjectsByMajor(Majors major) {
        return subjectsDAO.subjectsByMajor(major);
    }

    @Override
    public List<Subjects> getSubjects() {
        return subjectsDAO.getSubjects();
    }

    @Override
    public Subjects updateSubject(String id, Subjects subject) {
        return subjectsDAO.updateSubject(id, subject);
    }

    @Override
    public void deleteSubject(String id) {
        subjectsDAO.deleteSubject(id);
    }
}