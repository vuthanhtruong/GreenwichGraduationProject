package com.example.demo.service;

import com.example.demo.entity.Majors;
import com.example.demo.entity.Subjects;

import javax.security.auth.Subject;
import java.util.List;

public interface SubjectsService {
    List<Subject> getSubjects();
    List<Subject> subjectsByMajor (Majors major);
    Subject getSubjectBySubjectId(String subjectId);
    void addSubject(Subjects subject);
    Subjects getSubjectById(String subjectId);
    Subjects checkNameSubject (Subjects subject);
    Subjects getSubjectByName(String subjectName);
    Subjects updateSubject(String id, Subjects subject);
    void deleteSubject(String id);

}
