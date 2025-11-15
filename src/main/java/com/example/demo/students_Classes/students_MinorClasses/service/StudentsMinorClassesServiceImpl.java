package com.example.demo.students_Classes.students_MinorClasses.service;

import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.students_Classes.students_MinorClasses.dao.StudentsMinorClassesDAO;
import com.example.demo.students_Classes.students_MinorClasses.model.Students_MinorClasses;
import com.example.demo.user.student.model.Students;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentsMinorClassesServiceImpl implements StudentsMinorClassesService {
    @Override
    public List<String> getClassNotificationsForStudent(String studentId) {
        return dao.getClassNotificationsForStudent(studentId);
    }

    private final StudentsMinorClassesDAO dao;

    public StudentsMinorClassesServiceImpl(StudentsMinorClassesDAO dao) {
        this.dao = dao;
    }

    @Override
    public List<Students_MinorClasses> getStudentsInClass(String classId) {
        if (classId == null) throw new IllegalArgumentException("Class ID cannot be null");
        return dao.getStudentsInClass(classId);
    }

    @Override
    public List<Students> getStudentsByClass(MinorClasses minorClass) {
        if (minorClass == null) throw new IllegalArgumentException("MinorClasses cannot be null");
        return dao.getStudentsByClass(minorClass);
    }

    @Override
    public List<Students> getStudentsNotInClassAndSubject(String classId, String subjectId) {
        if (classId == null || subjectId == null) {
            throw new IllegalArgumentException("Class ID and Subject ID cannot be null");
        }
        return dao.getStudentsNotInClassAndSubject(classId, subjectId);
    }

    @Override
    public void addStudentToClass(Students_MinorClasses studentsMinorClasses) {
        if (studentsMinorClasses == null) {
            throw new IllegalArgumentException("Students_MinorClasses cannot be null");
        }
        dao.addStudentToClass(studentsMinorClasses);
    }

    @Override
    public void removeStudentFromClass(String studentId, String classId) {
        if (studentId == null || classId == null) {
            throw new IllegalArgumentException("Student ID and Class ID cannot be null");
        }
        dao.removeStudentFromClass(studentId, classId);
    }

    @Override
    public boolean existsByStudentAndClass(String studentId, String classId) {
        if (studentId == null || classId == null) return false;
        return dao.existsByStudentAndClass(studentId, classId);
    }

    @Override
    public List<Students_MinorClasses> getStudentsInClassByStudent(String studentId) {
        if (studentId == null) throw new IllegalArgumentException("Student ID cannot be null");
        return dao.getStudentsInClassByStudent(studentId);
    }
}