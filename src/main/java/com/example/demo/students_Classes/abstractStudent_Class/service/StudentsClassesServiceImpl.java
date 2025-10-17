package com.example.demo.students_Classes.abstractStudent_Class.service;

import com.example.demo.students_Classes.abstractStudent_Class.dao.StudentsClassesDAO;
import com.example.demo.students_Classes.abstractStudent_Class.model.Students_Classes;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentsClassesServiceImpl implements StudentsClassesService {

    private final StudentsClassesDAO studentsClassesDAO;

    public StudentsClassesServiceImpl(StudentsClassesDAO studentsClassesDAO) {
        this.studentsClassesDAO = studentsClassesDAO;
    }

    @Override
    public List<Students_Classes> getClassByStudent(String studentId) {
        return studentsClassesDAO.getClassByStudent(studentId);
    }
}
