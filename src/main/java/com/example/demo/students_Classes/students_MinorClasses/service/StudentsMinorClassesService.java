package com.example.demo.students_Classes.students_MinorClasses.service;

import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.students_Classes.students_MinorClasses.model.Students_MinorClasses;
import com.example.demo.user.student.model.Students;

import java.util.List;

public interface StudentsMinorClassesService {

    List<Students_MinorClasses> getStudentsInClass(String classId);

    List<Students> getStudentsByClass(MinorClasses minorClass);

    List<Students> getStudentsNotInClassAndSubject(String classId, String subjectId);

    void addStudentToClass(Students_MinorClasses studentsMinorClasses);

    void removeStudentFromClass(String studentId, String classId);

    boolean existsByStudentAndClass(String studentId, String classId);

    List<Students_MinorClasses> getStudentsInClassByStudent(String studentId);
}