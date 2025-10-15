package com.example.demo.student_class.service;

import com.example.demo.student_class.dao.StudentsClassesDAO;
import com.example.demo.student_class.model.Students_Classes;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentsClassesServiceImpl implements StudentsClassesService {

    private final StudentsClassesDAO studentsClassesDAO;

    public StudentsClassesServiceImpl(StudentsClassesDAO studentsClassesDAO) {
        this.studentsClassesDAO = studentsClassesDAO;
    }

    @Override
    public List<Students_Classes> getStudentsInClassByStudent(String studentId) {
        return studentsClassesDAO.getStudentsInClassByStudent(studentId);
    }
}
