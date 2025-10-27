package com.example.demo.subject.minorSubject.service;

import com.example.demo.subject.minorSubject.dao.MinorSubjectsDAO;
import com.example.demo.subject.minorSubject.model.MinorSubjects;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class MinorSubjectsServiceImpl implements MinorSubjectsService {
    @Override
    public List<MinorSubjects> getAllSubjects() {
        return subjectsDAO.getAllSubjects();
    }

    private final MinorSubjectsDAO subjectsDAO;

    @Autowired
    public MinorSubjectsServiceImpl(MinorSubjectsDAO subjectsDAO) {
        this.subjectsDAO = subjectsDAO;
    }

    @Override
    public List<MinorSubjects> getSubjectsByCreator(DeputyStaffs creator) {
        return subjectsDAO.getSubjectsByCreator(creator);
    }

    @Override
    public boolean isDuplicateSubjectName(String subjectName, String subjectId) {
        return subjectsDAO.isDuplicateSubjectName(subjectName, subjectId);
    }

    @Override
    public Map<String, String> validateSubject(MinorSubjects subject) {
        return subjectsDAO.validateSubject(subject);
    }

    @Override
    public List<MinorSubjects> getPaginatedSubjects(int firstResult, int pageSize) {
        return subjectsDAO.getPaginatedSubjects(firstResult, pageSize);
    }

    @Override
    public long numberOfSubjects() {
        return subjectsDAO.numberOfSubjects();
    }

    @Override
    public List<MinorSubjects> searchSubjects(String searchType, String keyword, int firstResult, int pageSize) {
        return subjectsDAO.searchSubjects(searchType, keyword, firstResult, pageSize);
    }

    @Override
    public long countSearchResults(String searchType, String keyword) {
        return subjectsDAO.countSearchResults(searchType, keyword);
    }

    @Override
    public boolean existsBySubjectExcludingName(String subjectName, String subjectId) {
        return subjectsDAO.existsBySubjectExcludingName(subjectName, subjectId);
    }

    @Override
    public String generateUniqueSubjectId(String creatorId, LocalDate createdDate) {
        return subjectsDAO.generateUniqueSubjectId(creatorId, createdDate);
    }

    @Override
    public void addSubject(MinorSubjects subject) {
        subjectsDAO.addSubject(subject);
    }

    @Override
    public MinorSubjects getSubjectById(String subjectId) {
        return subjectsDAO.getSubjectById(subjectId);
    }

    @Override
    public MinorSubjects getSubjectByName(String subjectName) {
        return subjectsDAO.getSubjectByName(subjectName);
    }

    @Override
    public MinorSubjects checkNameSubject(MinorSubjects subject) {
        return subjectsDAO.checkNameSubject(subject);
    }


    @Override
    public MinorSubjects editSubject(String id, MinorSubjects subject) {
        return subjectsDAO.editSubject(id, subject);
    }

    @Override
    public void deleteSubject(String id) {
        subjectsDAO.deleteSubject(id);
    }
}