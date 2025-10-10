package com.example.demo.student_class.service;

import com.example.demo.specializedClasses.model.SpecializedClasses;
import com.example.demo.student.model.Students;
import com.example.demo.student_class.dao.StudentsSpecializedClassesDAO;
import com.example.demo.student_class.model.Students_SpecializedClasses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentsSpecializedClassesServiceImpl implements StudentsSpecializedClassesService {

    private final StudentsSpecializedClassesDAO studentsSpecializedClassesDAO;

    @Autowired
    public StudentsSpecializedClassesServiceImpl(StudentsSpecializedClassesDAO studentsSpecializedClassesDAO) {
        this.studentsSpecializedClassesDAO = studentsSpecializedClassesDAO;
    }

    @Override
    public List<Students> getStudentsNotInClassAndSubject(String classId, String subjectId) {
        if (classId == null || subjectId == null) {
            throw new IllegalArgumentException("Class ID and Subject ID cannot be null");
        }
        return studentsSpecializedClassesDAO.getStudentsNotInClassAndSubject(classId, subjectId);
    }

    @Override
    public void addStudentToClass(Students_SpecializedClasses studentsSpecializedClasses) {
        if (studentsSpecializedClasses == null) {
            throw new IllegalArgumentException("Student-Class assignment cannot be null");
        }
        studentsSpecializedClassesDAO.addStudentToClass(studentsSpecializedClasses);
    }

    @Override
    public void removeStudentFromClass(String studentId, String classId) {
        if (studentId == null || classId == null) {
            throw new IllegalArgumentException("Student ID and Class ID cannot be null");
        }
        studentsSpecializedClassesDAO.removeStudentFromClass(studentId, classId);
    }

    @Override
    public List<Students_SpecializedClasses> getStudentsInClass(String classId) {
        if (classId == null) {
            throw new IllegalArgumentException("Class ID cannot be null");
        }
        return studentsSpecializedClassesDAO.getStudentsInClass(classId);
    }

    @Override
    public List<Students> getStudentsByClass(SpecializedClasses specializedClass) {
        if (specializedClass == null) {
            throw new IllegalArgumentException("Class cannot be null");
        }
        return studentsSpecializedClassesDAO.getStudentsByClass(specializedClass);
    }

    @Override
    public boolean isStudentAlreadyRequiredForClass(String studentId, String classId) {
        if (studentId == null || classId == null) {
            throw new IllegalArgumentException("Student ID and Class ID cannot be null");
        }
        return studentsSpecializedClassesDAO.existsByStudentAndClass(studentId, classId);
    }
}