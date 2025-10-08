package com.example.demo.syllabus.service;

import com.example.demo.majorSubject.model.MajorSubjects;
import com.example.demo.syllabus.model.Syllabuses;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SyllabusesService {
    void addSyllabus(Syllabuses syllabus);
    Syllabuses getSyllabusById(String syllabusId);
    List<Syllabuses> getSyllabusesBySubject(MajorSubjects subject);
    List<Syllabuses> getPaginatedSyllabuses(String subjectId, int firstResult, int pageSize);
    Long numberOfSyllabuses(String subjectId);
    void deleteSyllabusBySubject(MajorSubjects subject);
    List<String> syllabusValidation(Syllabuses syllabus, MultipartFile file);
}
