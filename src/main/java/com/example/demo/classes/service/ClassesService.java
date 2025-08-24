package com.example.demo.classes.service;

import com.example.demo.classes.model.MajorClasses;
import com.example.demo.major.model.Majors;
import com.example.demo.subject.model.MajorSubjects;

import java.time.LocalDateTime;
import java.util.List;

public interface ClassesService {
    void SetNullWhenDeletingSubject(MajorSubjects subject);
    void deleteClassBySubject(MajorSubjects subject);
    List<MajorClasses> ClassesByMajor(Majors major);
    List<MajorClasses> getClasses();
    MajorClasses getClassById(String id);
    MajorClasses getClassByName(String name);
    void addClass(MajorClasses c);
    MajorClasses editClass(String id, MajorClasses classObj);
    void deleteClass(String id);
    String generateUniqueClassId(String majorId, LocalDateTime createdDate);
    List<String> validateClass(MajorClasses classObj, String excludeId);
}
