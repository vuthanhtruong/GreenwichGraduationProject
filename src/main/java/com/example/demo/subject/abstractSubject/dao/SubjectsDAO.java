package com.example.demo.subject.abstractSubject.dao;

import com.example.demo.subject.abstractSubject.model.Subjects;

import java.util.List;

public interface SubjectsDAO {
    List<Subjects> getSubjects();
    List<Subjects> getSubjectsByAdmissionYear(Integer admissionYear);
    Subjects getSubjectById(String id);
    long countSearchResults(String searchType, String keyword);
    List<Subjects> searchSubjects(String searchType, String keyword, int firstResult, int pageSize);
    long numberOfSubjects();
    List<Subjects> getPaginatedSubjects(int firstResult, int pageSize);
    boolean existsSubjectById(String subjectId);
    List<Subjects> YetAcceptedSubjects();
    void approveSubjects(List<String> subjectIds, String acceptorId);
    boolean existsBySubjectNameExcludingId(String subjectName, String subjectId);
}