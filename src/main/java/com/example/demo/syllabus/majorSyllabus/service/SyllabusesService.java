package com.example.demo.syllabus.majorSyllabus.service;

import com.example.demo.subject.majorSubject.model.MajorSubjects;
import com.example.demo.syllabus.majorSyllabus.model.MajorSyllabuses;
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
