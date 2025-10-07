package com.example.demo.subject.dao;

import com.example.demo.Specialization.model.Specialization;
import com.example.demo.major.model.Majors;
import com.example.demo.subject.model.SpecializedSubject;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface SpecializedSubjectsDAO {
    List<SpecializedSubject> searchSubjects(String searchType, String keyword, int firstResult, int pageSize, Specialization specialization);
    long countSearchResults(String searchType, String keyword, Specialization specialization);
    boolean existsBySubjectExcludingName(String subjectName, String subjectId);
    void addSubject(SpecializedSubject subject, Specialization specialization);
    SpecializedSubject getSubjectById(String subjectId);
    SpecializedSubject getSubjectByName(String subjectName);
    SpecializedSubject checkNameSubject(SpecializedSubject subject);
    List<SpecializedSubject> subjectsBySpecialization(Specialization specialization);
    List<SpecializedSubject> AcceptedSubjectsBySpecialization(Specialization specialization);
    List<SpecializedSubject> getSubjects();
    SpecializedSubject editSubject(String id, SpecializedSubject subject);
    void deleteSubject(String id);
    String generateUniqueSubjectId(String specializationId, LocalDate createdDate);
    Map<String, String> validateSubject(SpecializedSubject subject, String specializationId, String curriculumId);
    List<SpecializedSubject> getPaginatedSubjects(int firstResult, int pageSize);
    long numberOfSubjects(Majors  majors);
    long numberOfSubjectsBySpecialization(String specializationId);
    List<SpecializedSubject> getPaginatedSubjectsBySpecialization(int firstResult, int pageSize, String specializationId);
}
