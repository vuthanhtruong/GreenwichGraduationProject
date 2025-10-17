package com.example.demo.syllabus.specializationSyllabus.dao;

import com.example.demo.syllabus.specializationSyllabus.model.SpecializationSyllabuses;
import com.example.demo.subject.specializedSubject.model.SpecializedSubject;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SpecializationSyllabusesDAO {
    void deleteSyllabus(SpecializationSyllabuses syllabus);

    void addSyllabus(SpecializationSyllabuses syllabus);

    SpecializationSyllabuses getSyllabusById(String syllabusId);

    List<SpecializationSyllabuses> getSyllabusesBySubject(SpecializedSubject subject);

    void deleteSyllabusBySubject(SpecializedSubject subject);

    List<SpecializationSyllabuses> getPaginatedSyllabuses(String subjectId, int firstResult, int pageSize);

    Long numberOfSyllabuses(String subjectId);

    List<String> validateSyllabus(SpecializationSyllabuses syllabus, MultipartFile file, String subjectId);

    boolean existsBySyllabusNameAndSubject(String syllabusName, String subjectId);
}