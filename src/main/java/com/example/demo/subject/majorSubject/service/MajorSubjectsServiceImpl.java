package com.example.demo.subject.majorSubject.service;

import com.example.demo.subject.majorSubject.dao.MajorSubjectsDAO;
import com.example.demo.major.model.Majors;
import com.example.demo.subject.majorSubject.model.MajorSubjects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class MajorSubjectsServiceImpl implements MajorSubjectsService {
    @Override
    public List<MajorSubjects> getSubjectsByCurriculumId(String curriculumId) {
        return subjectsDAO.getSubjectsByCurriculumId(curriculumId);
    }

    @Override
    public boolean isDuplicateSubjectName(String subjectName, String subjectId) {
        return subjectsDAO.isDuplicateSubjectName(subjectName, subjectId);
    }

    @Override
    public Map<String, String> validateSubject(MajorSubjects subject, String curriculumId) {
        return subjectsDAO.validateSubject(subject, curriculumId);
    }

    @Override
    public List<MajorSubjects> getPaginatedSubjects(int firstResult, int pageSize, Majors major) {
        return subjectsDAO.getPaginatedSubjects(firstResult, pageSize, major);
    }

    @Override
    public long numberOfSubjects(Majors major) {
        return subjectsDAO.numberOfSubjects(major);
    }

    @Override
    public List<MajorSubjects> searchSubjects(String searchType, String keyword, int firstResult, int pageSize, Majors major) {
        return subjectsDAO.searchSubjects(searchType, keyword, firstResult, pageSize, major);
    }

    @Override
    public long countSearchResults(String searchType, String keyword, Majors major) {
        return 0;
    }

    @Override
    public boolean existsBySubjectExcludingName(String SubjectName, String SubjectId) {
        return subjectsDAO.existsBySubjectExcludingName(SubjectName, SubjectId);
    }

    @Override
    public String generateUniqueSubjectId(String majorId, LocalDate createdDate) {
        return subjectsDAO.generateUniqueSubjectId(majorId, createdDate);
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
    public MajorSubjects editSubject(String id, MajorSubjects subject) {
        return subjectsDAO.editSubject(id, subject);
    }

    @Override
    public void deleteSubject(String id) {
        subjectsDAO.deleteSubject(id);
    }
}