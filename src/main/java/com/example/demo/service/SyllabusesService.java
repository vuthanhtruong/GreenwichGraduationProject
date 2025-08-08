package com.example.demo.service;

import com.example.demo.entity.MajorSubjects;
import com.example.demo.entity.Syllabuses;

import java.util.List;

public interface SyllabusesService {
    List<Syllabuses> syllabusesList();
    List<Syllabuses> getSyllabusesBySubject(MajorSubjects subject);
    void addSyllabus(Syllabuses syllabus);
    Syllabuses getSyllabusById(String syllabusId);
    void  deleteSyllabusBySubject(MajorSubjects subject);
}
