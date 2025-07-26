package com.example.demo.service;

import com.example.demo.entity.Classes;
import com.example.demo.entity.Majors;
import com.example.demo.entity.Subjects;

import java.util.List;

public interface ClassesService {
    List<Classes> getClasses();
    Classes getClassById(String id);
    Classes getClassByName(String name);
    void addClass(Classes c);
    void deleteClass(String id);
    List<Classes> ClassesByMajor(Majors major);
    Classes updateClass(String id, Classes classObj);
    void deleteClassBySubject(Subjects subject);
    void SetNullWhenDeletingSubject(Subjects subject);
}
