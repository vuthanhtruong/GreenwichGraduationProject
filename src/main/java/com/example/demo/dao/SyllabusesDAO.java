package com.example.demo.dao;

import com.example.demo.entity.Subjects;
import com.example.demo.entity.Syllabuses;
import com.example.demo.service.SyllabusesService;

import java.util.List;

public interface SyllabusesDAO {
    List<Syllabuses> syllabusesList();
    List<Syllabuses> getSyllabusesBySubject(Subjects subject);
    void addSyllabus(Syllabuses syllabus);
    Syllabuses getSyllabusById(String syllabusId);
    void  deleteSyllabusBySubject(Subjects subject);
}
