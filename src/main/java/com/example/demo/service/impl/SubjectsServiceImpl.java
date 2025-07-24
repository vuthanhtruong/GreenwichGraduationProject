package com.example.demo.service.impl;

import com.example.demo.dao.SubjectsDAO;
import com.example.demo.entity.Majors;
import com.example.demo.entity.Subjects;
import com.example.demo.service.SubjectsService;
import org.springframework.stereotype.Service;

import javax.security.auth.Subject;
import java.util.List;

@Service
public class SubjectsServiceImpl implements SubjectsService {
    @Override
    public Subjects checkNameSubject(Subjects subject) {
        return subjectsDAO.checkNameSubject(subject);
    }

    @Override
    public Subjects getSubjectById(String subjectId) {
        return subjectsDAO.getSubjectById(subjectId);
    }

    @Override
    public void addSubject(Subjects subject) {
        subjectsDAO.addSubject(subject);
    }

    @Override
    public Subject getSubjectBySubjectId(String subjectId) {
        return subjectsDAO.getSubjectBySubjectId(subjectId);
    }

    private final SubjectsDAO subjectsDAO;

    public SubjectsServiceImpl(SubjectsDAO subjectsDAO) {
        this.subjectsDAO = subjectsDAO;
    }

    @Override
    public List<Subject> getSubjects() {
        return subjectsDAO.getSubjects();
    }

    @Override
    public List<Subject> subjectsByMajor(Majors major) {
        return subjectsDAO.subjectsByMajor(major);
    }
}
