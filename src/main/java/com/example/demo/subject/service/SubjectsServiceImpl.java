package com.example.demo.subject.service;

import com.example.demo.subject.dao.SubjectsDAO;
import com.example.demo.subject.model.Subjects;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class SubjectsServiceImpl implements SubjectsService {
    @Override
    public boolean existsSubjectById(String subjectId) {
        return subjectsDAO.existsSubjectById(subjectId);
    }

    @Override
    public long countSearchResults(String searchType, String keyword) {
        return subjectsDAO.countSearchResults(searchType, keyword);
    }

    @Override
    public List<Subjects> searchSubjects(String searchType, String keyword, int firstResult, int pageSize) {
        return subjectsDAO.searchSubjects(searchType, keyword, firstResult, pageSize);
    }

    @Override
    public long numberOfSubjects() {
        return subjectsDAO.numberOfSubjects();
    }

    @Override
    public List<Subjects> getPaginatedSubjects(int firstResult, int pageSize) {
        return subjectsDAO.getPaginatedSubjects(firstResult, pageSize);
    }

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
