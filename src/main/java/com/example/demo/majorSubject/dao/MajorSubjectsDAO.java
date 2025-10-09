package com.example.demo.majorSubject.dao;

import com.example.demo.major.model.Majors;
import com.example.demo.majorSubject.model.MajorSubjects;

import java.util.List;
import java.util.Map;

public interface MajorSubjectsDAO {
    List<MajorSubjects> searchSubjects(String searchType, String keyword, int firstResult, int pageSize, Majors major);
    long countSearchResults(String searchType, String keyword, Majors major);
    boolean existsBySubjectExcludingName(String subjectName, String subjectId);
    void addSubject(MajorSubjects subject);
    MajorSubjects getSubjectById(String subjectId);
    MajorSubjects getSubjectByName(String subjectName);
    MajorSubjects checkNameSubject(MajorSubjects subject);
    List<MajorSubjects> subjectsByMajor(Majors major);
    List<MajorSubjects> AcceptedSubjectsByMajor(Majors major);
    List<MajorSubjects> getSubjects();
    MajorSubjects editSubject(String id, MajorSubjects subject);
    void deleteSubject(String id);
    String generateUniqueSubjectId(String majorId, java.time.LocalDate createdDate);
    Map<String, String> validateSubject(MajorSubjects subject, String curriculumId);
    List<MajorSubjects> getPaginatedSubjects(int firstResult, int pageSize, Majors major);
    long numberOfSubjects(Majors major);
    boolean isDuplicateSubjectName(String subjectName, String subjectId);
    List<MajorSubjects> getSubjectsByCurriculumId(String curriculumId);
}