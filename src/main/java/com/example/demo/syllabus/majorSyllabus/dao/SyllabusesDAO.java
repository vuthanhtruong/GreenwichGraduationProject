package com.example.demo.syllabus.majorSyllabus.dao;

import com.example.demo.subject.majorSubject.model.MajorSubjects;
import com.example.demo.syllabus.majorSyllabus.model.MajorSyllabuses;

import java.util.List;

public interface SyllabusesDAO {
    void addSyllabus(MajorSyllabuses syllabus);
    MajorSyllabuses getSyllabusById(String syllabusId);
    List<MajorSyllabuses> getSyllabusesBySubject(MajorSubjects subject);
    void deleteSyllabusBySubject(MajorSubjects subject);
    List<MajorSyllabuses> getPaginatedSyllabuses(String subjectId, int firstResult, int pageSize);
    Long numberOfSyllabuses(String subjectId);
    void deleteSyllabus(MajorSyllabuses syllabus);
}
