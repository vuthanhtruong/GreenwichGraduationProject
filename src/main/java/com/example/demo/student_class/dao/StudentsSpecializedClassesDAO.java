package com.example.demo.student_class.dao;

import com.example.demo.specializedClasses.model.SpecializedClasses;
import com.example.demo.student.model.Students;
import com.example.demo.student_class.model.Students_SpecializedClasses;

import java.util.List;

public interface StudentsSpecializedClassesDAO {
    List<Students> getStudentsNotInClassAndSubject(String classId, String subjectId);
    void addStudentToClass(Students_SpecializedClasses studentsSpecializedClasses);
    void removeStudentFromClass(String studentId, String classId);
    List<Students_SpecializedClasses> getStudentsInClass(String classId);
    List<Students> getStudentsByClass(SpecializedClasses specializedClass);
    boolean existsByStudentAndClass(String studentId, String classId);
}
