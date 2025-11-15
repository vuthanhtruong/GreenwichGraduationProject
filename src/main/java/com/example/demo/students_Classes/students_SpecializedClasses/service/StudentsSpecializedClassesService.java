package com.example.demo.students_Classes.students_SpecializedClasses.service;

import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.user.student.model.Students;
import com.example.demo.students_Classes.students_SpecializedClasses.model.Students_SpecializedClasses;

import java.util.List;

public interface StudentsSpecializedClassesService {
    List<Students> getStudentsNotInClassAndSubject(String classId, String subjectId);
    void addStudentToClass(Students_SpecializedClasses studentsSpecializedClasses);
    void removeStudentFromClass(String studentId, String classId);
    List<Students_SpecializedClasses> getStudentsInClass(String classId);
    List<Students> getStudentsByClass(SpecializedClasses specializedClass);
    boolean existsByStudentAndClass(String studentId, String classId);
    List<Students_SpecializedClasses> getStudentsInClassByStudent(String studentId);
    List<String> getClassNotificationsForStudent(String studentId);
}
