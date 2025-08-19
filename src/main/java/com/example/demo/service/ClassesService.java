package com.example.demo.service;

import com.example.demo.entity.MajorClasses;
import com.example.demo.entity.Majors;
import com.example.demo.entity.MajorSubjects;

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
    MajorClasses updateClass(String id, MajorClasses classObj);
    void deleteClass(String id);
    String generateUniqueClassId(String majorId, LocalDateTime createdDate);
    List<String> validateClass(MajorClasses classObj, String excludeId);
}
