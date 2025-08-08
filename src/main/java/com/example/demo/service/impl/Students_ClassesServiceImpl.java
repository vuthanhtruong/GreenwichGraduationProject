package com.example.demo.service.impl;

import com.example.demo.dao.Students_ClassesDAO;
import com.example.demo.entity.MajorClasses;
import com.example.demo.entity.Students;
import com.example.demo.entity.Students_MajorClasses;
import com.example.demo.service.Students_ClassesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Students_ClassesServiceImpl implements Students_ClassesService {
    @Override
    public List<Students> listStudentsCompletedPreviousSemesterWithSufficientBalance(MajorClasses classes) {
        return studentsClassesDAO.listStudentsCompletedPreviousSemesterWithSufficientBalance(classes);
    }

    @Override
    public List<Students> listStudentsCompletedPreviousSemesterWithInsufficientBalance(MajorClasses classes) {
        return studentsClassesDAO.listStudentsCompletedPreviousSemesterWithInsufficientBalance(classes);
    }

    @Override
    public void addStudentsToClass(MajorClasses classes, List<String> studentIds) {
        studentsClassesDAO.addStudentsToClass(classes, studentIds);
    }

    private final Students_ClassesDAO studentsClassesDAO;

    @Autowired
    public Students_ClassesServiceImpl(Students_ClassesDAO studentsClassesDAO) {
        this.studentsClassesDAO = studentsClassesDAO;
    }

    @Override
    public List<Students_MajorClasses> listStudentsInClass(MajorClasses classes) {
        return studentsClassesDAO.listStudentsInClass(classes);
    }

    @Override
    public List<Students> listStudentsNotInClass(MajorClasses classes) {
        return studentsClassesDAO.listStudentsNotInClass(classes);
    }

    @Override
    public List<Students> listStudentsFailedSubjectAndNotPaid(MajorClasses classes) {
        return studentsClassesDAO.listStudentsFailedSubjectAndNotPaid(classes);
    }

    @Override
    public List<Students> listStudentsFailedSubjectAndPaid(MajorClasses classes) {
        return studentsClassesDAO.listStudentsFailedSubjectAndPaid(classes);
    }

    @Override
    public List<Students> listStudentsNotTakenSubject(MajorClasses classes, boolean hasPaid) {
        return studentsClassesDAO.listStudentsNotTakenSubject(classes, hasPaid);
    }

    @Override
    public List<Students> listStudentsCurrentlyTakingSubject(MajorClasses classes) {
        return studentsClassesDAO.listStudentsCurrentlyTakingSubject(classes);
    }


}