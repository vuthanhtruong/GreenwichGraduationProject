package com.example.demo.subject.dao;

import com.example.demo.major.model.Majors;
import com.example.demo.subject.model.MajorSubjects;

import java.time.LocalDate;
import java.util.List;

public interface MajorSubjectsDAO {
    void addSubject(MajorSubjects subject);
    MajorSubjects getSubjectById(String subjectId);
    MajorSubjects getSubjectByName(String subjectName);
    MajorSubjects checkNameSubject(MajorSubjects subject);
    List<MajorSubjects> subjectsByMajor(Majors major);
    List<MajorSubjects> AcceptedSubjectsByMajor(Majors major);
    List<MajorSubjects> getSubjects();
    MajorSubjects editSubject(String id, MajorSubjects subject);
    void deleteSubject(String id);
    String generateUniqueSubjectId(String majorId, LocalDate createdDate);
    List<String> validateSubject(MajorSubjects subject);
    boolean existsBySubjectExcludingName(String SubjectName, String SubjectId);
    List<MajorSubjects> getPaginatedSubjects(int firstResult, int pageSize, Majors major);
    long numberOfSubjects(Majors major);
    List<MajorSubjects> searchSubjects(String searchType, String keyword, int firstResult, int pageSize, Majors major);
    long countSearchResults(String searchType, String keyword, Majors major);
}