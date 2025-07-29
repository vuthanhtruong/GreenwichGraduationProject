package com.example.demo.service.impl;

import com.example.demo.dao.Students_ClassesDAO;
import com.example.demo.entity.Classes;
import com.example.demo.entity.Students;
import com.example.demo.entity.Students_Classes;
import com.example.demo.service.Students_ClassesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Students_ClassesServiceImpl implements Students_ClassesService {
    @Override
    public void addStudentsToClass(Classes classes, List<String> studentIds) {
        studentsClassesDAO.addStudentsToClass(classes, studentIds);
    }

    private final Students_ClassesDAO studentsClassesDAO;

    @Autowired
    public Students_ClassesServiceImpl(Students_ClassesDAO studentsClassesDAO) {
        this.studentsClassesDAO = studentsClassesDAO;
    }

    @Override
    public List<Students_Classes> listStudentsInClass(Classes classes) {
        return studentsClassesDAO.listStudentsInClass(classes);
    }

    @Override
    public List<Students> listStudentsNotInClass(Classes classes) {
        return studentsClassesDAO.listStudentsNotInClass(classes);
    }

    @Override
    public List<Students> listStudentsFailedSubjectAndNotPaid(Classes classes) {
        return studentsClassesDAO.listStudentsFailedSubjectAndNotPaid(classes);
    }

    @Override
    public List<Students> listStudentsFailedSubjectAndPaid(Classes classes) {
        return studentsClassesDAO.listStudentsFailedSubjectAndPaid(classes);
    }

    @Override
    public List<Students> listStudentsNotTakenSubject(Classes classes, boolean hasPaid) {
        return studentsClassesDAO.listStudentsNotTakenSubject(classes, hasPaid);
    }

    @Override
    public List<Students> listStudentsCurrentlyTakingSubject(Classes classes) {
        return studentsClassesDAO.listStudentsCurrentlyTakingSubject(classes);
    }

    @Override
    public List<Students> listStudentsCompletedPreviousSemester(Classes classes) {
        return studentsClassesDAO.listStudentsCompletedPreviousSemester(classes);
    }
}