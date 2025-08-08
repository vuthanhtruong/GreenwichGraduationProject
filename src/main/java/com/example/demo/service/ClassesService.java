package com.example.demo.service;

import com.example.demo.entity.MajorClasses;
import com.example.demo.entity.Majors;
import com.example.demo.entity.MajorSubjects;

import java.util.List;

public interface ClassesService {
    List<MajorClasses> getClasses();
    MajorClasses getClassById(String id);
    MajorClasses getClassByName(String name);
    void addClass(MajorClasses c);
    void deleteClass(String id);
    List<MajorClasses> ClassesByMajor(Majors major);
    MajorClasses updateClass(String id, MajorClasses classObj);
    void deleteClassBySubject(MajorSubjects subject);
    void SetNullWhenDeletingSubject(MajorSubjects subject);
}
