// File: MinorSyllabusesDAO.java
package com.example.demo.syllabus.minorSyllabuses.dao;

import com.example.demo.subject.minorSubject.model.MinorSubjects;
import com.example.demo.syllabus.minorSyllabuses.model.MinorSyllabuses;

import java.util.List;

public interface MinorSyllabusesDAO {
    void addSyllabus(MinorSyllabuses syllabus);
    MinorSyllabuses getSyllabusById(String syllabusId);
    List<MinorSyllabuses> getSyllabusesBySubject(MinorSubjects subject);
    void deleteSyllabusBySubject(MinorSubjects subject);
    List<MinorSyllabuses> getPaginatedSyllabuses(String subjectId, int firstResult, int pageSize);
    Long numberOfSyllabuses(String subjectId);
    void deleteSyllabus(MinorSyllabuses syllabus);
}