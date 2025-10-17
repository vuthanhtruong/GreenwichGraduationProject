package com.example.demo.students_Classes.abstractStudent_Class.dao;

import com.example.demo.students_Classes.abstractStudent_Class.model.Students_Classes;

import java.util.List;

public interface StudentsClassesDAO {
    List<Students_Classes> getClassByStudent(String studentId);
}
