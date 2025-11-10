package com.example.demo.syllabus.minorSyllabuses.service;

import com.example.demo.subject.minorSubject.model.MinorSubjects;
import com.example.demo.syllabus.minorSyllabuses.model.MinorSyllabuses;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MinorSyllabusesService {
    void addSyllabus(MinorSyllabuses syllabus);
    MinorSyllabuses getSyllabusById(String syllabusId);
    List<MinorSyllabuses> getSyllabusesBySubject(MinorSubjects subject);
    void deleteSyllabusBySubject(MinorSubjects subject);
    List<MinorSyllabuses> getPaginatedSyllabuses(String subjectId, int firstResult, int pageSize);
    Long numberOfSyllabuses(String subjectId);
    void deleteSyllabus(MinorSyllabuses syllabus);
    List<String> syllabusValidation(MinorSyllabuses syllabus, MultipartFile file);
}
