package com.example.demo.dao;

import com.example.demo.entity.Classes;
import com.example.demo.entity.Majors;
import com.example.demo.entity.Subjects;

import java.util.List;

public interface ClassesDAO {
    List<Classes> getClasses();
    List<Classes> ClassesByMajor(Majors major);
    Classes getClassById(String id);
    Classes getClassByName(String name);
    void addClass(Classes c);
    Classes updateClass(String id, Classes classObj);
    void deleteClass(String id);
    void deleteClassBySubject(Subjects subject);
    void SetNullWhenDeletingSubject(Subjects subject);
}
