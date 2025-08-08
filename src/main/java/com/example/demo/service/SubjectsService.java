package com.example.demo.service;

import com.example.demo.entity.Majors;
import com.example.demo.entity.MajorSubjects;

import java.util.List;

public interface SubjectsService {
    void addSubject(MajorSubjects subject);
    MajorSubjects getSubjectById(String subjectId);
    MajorSubjects getSubjectByName(String subjectName);
    MajorSubjects checkNameSubject(MajorSubjects subject);
    List<MajorSubjects> subjectsByMajor(Majors major);
    List<MajorSubjects> getSubjects();
    MajorSubjects updateSubject(String id, MajorSubjects subject);
    void deleteSubject(String id);
}