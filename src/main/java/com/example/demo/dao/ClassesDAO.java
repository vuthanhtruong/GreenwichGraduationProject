package com.example.demo.dao;

import com.example.demo.entity.MajorClasses;
import com.example.demo.entity.Majors;
import com.example.demo.entity.MajorSubjects;

import java.util.List;

public interface ClassesDAO {
    List<MajorClasses> getClasses();
    List<MajorClasses> ClassesByMajor(Majors major);
    MajorClasses getClassById(String id);
    MajorClasses getClassByName(String name);
    void addClass(MajorClasses c);
    MajorClasses updateClass(String id, MajorClasses classObj);
    void deleteClass(String id);
    void deleteClassBySubject(MajorSubjects subject);
    void SetNullWhenDeletingSubject(MajorSubjects subject);
}
