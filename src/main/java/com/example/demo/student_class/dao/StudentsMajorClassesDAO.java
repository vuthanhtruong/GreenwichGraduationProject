package com.example.demo.student_class.dao;

import com.example.demo.classes.model.MajorClasses;
import com.example.demo.student.model.Students;
import com.example.demo.student_class.model.Students_MajorClasses;

import java.util.List;

public interface StudentsMajorClassesDAO {
    void addStudentToClass(Students_MajorClasses studentsMajorClasses);

    void removeStudentFromClass(String studentId, String classId);

    List<Students_MajorClasses> getStudentsInClass(String classId);

    List<Students> getStudentsByClass(MajorClasses majorClass);

    boolean existsByStudentAndClass(String studentId, String classId);

    List<Students> getStudentsNotInClassAndSubject(String classId, String subjectId);
}
