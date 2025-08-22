package com.example.demo.subject.service;

import com.example.demo.subject.dao.SubjectsDAO;
import com.example.demo.subject.model.Subjects;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class SubjectsServiceImpl implements SubjectsService {
    @Override
    public Subjects getSubjectById(String id) {
        return subjectsDAO.getSubjectById(id);
    }

    @Override
    public List<Subjects> getSubjectsByAdmissionYear(Integer admissionYear) {
        return subjectsDAO.getSubjectsByAdmissionYear(admissionYear);
    }

    private final SubjectsDAO subjectsDAO;

    public SubjectsServiceImpl(SubjectsDAO subjectsDAO) {
        this.subjectsDAO = subjectsDAO;
    }

    @Override
    public List<Subjects> getSubjects() {
        return subjectsDAO.getSubjects();
    }
}
