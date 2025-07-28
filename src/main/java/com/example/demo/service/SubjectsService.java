package com.example.demo.service;

import com.example.demo.entity.Majors;
import com.example.demo.entity.Subjects;

import java.util.List;

public interface SubjectsService {
    void addSubject(Subjects subject);
    Subjects getSubjectById(String subjectId);
    Subjects getSubjectByName(String subjectName);
    Subjects checkNameSubject(Subjects subject);
    List<Subjects> subjectsByMajor(Majors major);
    List<Subjects> getSubjects();
    Subjects updateSubject(String id, Subjects subject);
    void deleteSubject(String id);
}