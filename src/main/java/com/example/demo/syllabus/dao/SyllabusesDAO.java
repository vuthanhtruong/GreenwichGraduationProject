package com.example.demo.syllabus.dao;

import com.example.demo.majorSubject.model.MajorSubjects;
import com.example.demo.syllabus.model.Syllabuses;

import java.util.List;

public interface SyllabusesDAO {
    void addSyllabus(Syllabuses syllabus);
    Syllabuses getSyllabusById(String syllabusId);
    List<Syllabuses> getSyllabusesBySubject(MajorSubjects subject);
    void deleteSyllabusBySubject(MajorSubjects subject);
    List<Syllabuses> getPaginatedSyllabuses(String subjectId, int firstResult, int pageSize);
    Long numberOfSyllabuses(String subjectId);
}
