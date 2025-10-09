package com.example.demo.majorSyllabus.service;

import com.example.demo.majorSubject.model.MajorSubjects;
import com.example.demo.majorSyllabus.model.MajorSyllabuses;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SyllabusesService {
    void addSyllabus(MajorSyllabuses syllabus);
    MajorSyllabuses getSyllabusById(String syllabusId);
    List<MajorSyllabuses> getSyllabusesBySubject(MajorSubjects subject);
    List<MajorSyllabuses> getPaginatedSyllabuses(String subjectId, int firstResult, int pageSize);
    Long numberOfSyllabuses(String subjectId);
    void deleteSyllabusBySubject(MajorSubjects subject);
    List<String> syllabusValidation(MajorSyllabuses syllabus, MultipartFile file);
    void deleteSyllabus(MajorSyllabuses syllabus);
}
