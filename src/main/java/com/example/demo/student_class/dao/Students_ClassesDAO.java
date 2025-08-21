package com.example.demo.student_class.dao;

import com.example.demo.classes.model.MajorClasses;
import com.example.demo.student.model.Students;
import com.example.demo.entity.Students_MajorClasses;

import java.util.List;

public interface Students_ClassesDAO {
    List<Students_MajorClasses> listStudentsInClass(MajorClasses classes);
    List<Students> listStudentsNotInClass(MajorClasses classes);
    List<Students> listStudentsFailedSubjectAndNotPaid(MajorClasses classes);
    List<Students> listStudentsFailedSubjectAndPaid(MajorClasses classes);
    List<Students> listStudentsNotTakenSubject(MajorClasses classes, boolean hasPaid);
    List<Students> listStudentsCurrentlyTakingSubject(MajorClasses classes);
    List<Students> listStudentsCompletedPreviousSemesterWithSufficientBalance(MajorClasses classes);
    List<Students> listStudentsCompletedPreviousSemesterWithInsufficientBalance(MajorClasses classes);
    void addStudentsToClass(MajorClasses classes, List<String> studentIds);
}