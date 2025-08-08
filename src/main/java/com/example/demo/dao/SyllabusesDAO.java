package com.example.demo.dao;

import com.example.demo.entity.MajorSubjects;
import com.example.demo.entity.Syllabuses;

import java.util.List;

public interface SyllabusesDAO {
    List<Syllabuses> syllabusesList();
    List<Syllabuses> getSyllabusesBySubject(MajorSubjects subject);
    void addSyllabus(Syllabuses syllabus);
    Syllabuses getSyllabusById(String syllabusId);
    void  deleteSyllabusBySubject(MajorSubjects subject);
}
