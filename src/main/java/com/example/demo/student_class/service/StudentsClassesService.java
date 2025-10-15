package com.example.demo.student_class.service;

import com.example.demo.student_class.model.Students_Classes;

import java.util.List;

public interface StudentsClassesService {
    List<Students_Classes> getStudentsInClassByStudent(String studentId);
}
