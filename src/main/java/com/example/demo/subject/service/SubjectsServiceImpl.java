package com.example.demo.subject.service;

import com.example.demo.subject.dao.SubjectsDAO;
import com.example.demo.subject.model.Subjects;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class SubjectsServiceImpl implements SubjectsService {
    private final SubjectsDAO subjectsDAO;

    public SubjectsServiceImpl(SubjectsDAO subjectsDAO) {
        this.subjectsDAO = subjectsDAO;
    }

    @Override
    public List<Subjects> getSubjects() {
        return subjectsDAO.getSubjects();
    }
}
