package com.example.demo.service;

import com.example.demo.entity.Classes;
import com.example.demo.entity.Students;
import com.example.demo.entity.Students_Classes;

import java.util.List;

public interface Students_ClassesService {
    List<Students_Classes> listStudentsInClass(Classes classes);
    List<Students> listStudentsNotInClass(Classes classes);
    List<Students> listStudentsFailedSubjectAndNotPaid(Classes classes);
    List<Students> listStudentsFailedSubjectAndPaid(Classes classes);
    List<Students> listStudentsNotTakenSubject(Classes classes, boolean hasPaid);
    List<Students> listStudentsCurrentlyTakingSubject(Classes classes);
    List<Students> listStudentsCompletedPreviousSemester(Classes classes);
    void addStudentsToClass(Classes classes, List<String> studentIds);
}
