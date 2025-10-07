package com.example.demo.subject.dao;

import com.example.demo.Curriculum.model.Curriculum;
import com.example.demo.Specialization.model.Specialization;
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
    Map<String, String> validateSubject(SpecializedSubject subject);
    List<SpecializedSubject> getPaginatedSubjects(int firstResult, int pageSize, Specialization specialization);
    long numberOfSubjects(Specialization specialization);
}
