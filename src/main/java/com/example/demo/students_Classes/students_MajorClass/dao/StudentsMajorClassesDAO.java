package com.example.demo.students_Classes.students_MajorClass.dao;

import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.user.student.model.Students;
import com.example.demo.students_Classes.students_MajorClass.model.Students_MajorClasses;
import java.util.List;

public interface StudentsMajorClassesDAO {
    void addStudentToClass(Students_MajorClasses studentsMajorClasses);

    void removeStudentFromClass(String studentId, String classId);

    List<Students_MajorClasses> getStudentsInClass(String classId);

    List<Students> getStudentsByClass(MajorClasses majorClass);

    boolean existsByStudentAndClass(String studentId, String classId);

    List<Students> getStudentsNotInClassAndSubject(String classId, String subjectId);
    List<Students_MajorClasses> getStudentsInClassByStudent(String studentId);
    List<String> getClassNotificationsForStudent(String studentId);
}
