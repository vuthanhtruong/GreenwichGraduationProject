package com.example.demo.student_class.dao;

import com.example.demo.student_class.model.Students_Classes;

import java.util.List;

public interface StudentsClassesDAO {
    List<Students_Classes> getStudentsInClassByStudent(String studentId);
}
