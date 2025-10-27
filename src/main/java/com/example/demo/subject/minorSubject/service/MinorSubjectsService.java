package com.example.demo.subject.minorSubject.service;

import com.example.demo.major.model.Majors;
import com.example.demo.subject.minorSubject.model.MinorSubjects;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface MinorSubjectsService {
    List<MinorSubjects> getSubjectsByCreator(DeputyStaffs creator);

    boolean existsBySubjectExcludingName(String subjectName, String subjectId);

    void addSubject(MinorSubjects subject);

    MinorSubjects getSubjectById(String subjectId);

    MinorSubjects getSubjectByName(String subjectName);

    MinorSubjects checkNameSubject(MinorSubjects subject);

    List<MinorSubjects> getPaginatedSubjects(int firstResult, int pageSize);

    long numberOfSubjects();

    List<MinorSubjects> searchSubjects(String searchType, String keyword, int firstResult, int pageSize);

    long countSearchResults(String searchType, String keyword);

    MinorSubjects editSubject(String id, MinorSubjects subject);

    void deleteSubject(String id);

    String generateUniqueSubjectId(String creatorId, LocalDate createdDate);

    boolean isDuplicateSubjectName(String subjectName, String subjectId);

    Map<String, String> validateSubject(MinorSubjects subject);

    List<MinorSubjects> getAllSubjects();
}
