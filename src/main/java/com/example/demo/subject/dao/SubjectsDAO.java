package com.example.demo.subject.dao;

import com.example.demo.subject.model.Subjects;

import java.util.List;

public interface SubjectsDAO {
    List<Subjects> getSubjects();
    List<Subjects> getSubjectsByAdmissionYear(Integer admissionYear);
    Subjects getSubjectById(String id);
}
