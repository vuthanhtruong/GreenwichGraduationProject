package com.example.demo.students_Classes.abstractStudents_Class.service;

import com.example.demo.students_Classes.abstractStudents_Class.model.Students_Classes;

import java.util.List;

public interface StudentsClassesService {
    List<Students_Classes> getClassByStudent(String studentId);
}
