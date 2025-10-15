package com.example.demo.student_class.service;

import com.example.demo.classes.model.MajorClasses;
import com.example.demo.student.model.Students;
import com.example.demo.student_class.dao.StudentsMajorClassesDAO;
import com.example.demo.student_class.model.Students_MajorClasses;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentsMajorClassesServiceImpl implements StudentsMajorClassesService{
    @Override
    public List<Students_MajorClasses> getStudentsInClassByStudent(String studentId) {
        return studentsMajorClassesDAO.getStudentsInClassByStudent(studentId);
    }

    @Override
    public List<Students> getStudentsNotInClassAndSubject(String classId, String subjectId) {
        return studentsMajorClassesDAO.getStudentsNotInClassAndSubject(classId, subjectId);
    }

    private final StudentsMajorClassesDAO studentsMajorClassesDAO;

    public StudentsMajorClassesServiceImpl(StudentsMajorClassesDAO studentsMajorClassesDAO) {
        this.studentsMajorClassesDAO = studentsMajorClassesDAO;
    }

    @Override
    public void addStudentToClass(Students_MajorClasses studentsMajorClasses) {
        studentsMajorClassesDAO.addStudentToClass(studentsMajorClasses);
    }

    @Override
    public void removeStudentFromClass(String studentId, String classId) {
        studentsMajorClassesDAO.removeStudentFromClass(studentId, classId);
    }

    @Override
    public List<Students_MajorClasses> getStudentsInClass(String classId) {
        return studentsMajorClassesDAO.getStudentsInClass(classId);
    }

    @Override
    public List<Students> getStudentsByClass(MajorClasses majorClass) {
        return studentsMajorClassesDAO.getStudentsByClass(majorClass);
    }

    @Override
    public boolean existsByStudentAndClass(String studentId, String classId) {
        return studentsMajorClassesDAO.existsByStudentAndClass(studentId, classId);
    }
}
