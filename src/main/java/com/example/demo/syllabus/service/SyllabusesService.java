package com.example.demo.syllabus.service;

import com.example.demo.subject.model.MajorSubjects;
import com.example.demo.syllabus.model.Syllabuses;

import java.util.List;

public interface SyllabusesService {
    List<Syllabuses> syllabusesList();
    List<Syllabuses> getSyllabusesBySubject(MajorSubjects subject);
    void addSyllabus(Syllabuses syllabus);
    Syllabuses getSyllabusById(String syllabusId);
    void  deleteSyllabusBySubject(MajorSubjects subject);
}
