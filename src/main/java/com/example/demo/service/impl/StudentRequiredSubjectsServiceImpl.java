package com.example.demo.service.impl;

import com.example.demo.dao.StudentRequiredMajorSubjectsDAO;
import com.example.demo.entity.MajorSubjects;
import com.example.demo.entity.AbstractClasses.StudentRequiredSubjects;
import com.example.demo.entity.StudentRequiredMajorSubjects;
import com.example.demo.entity.Students;
import com.example.demo.service.StudentRequiredSubjectsService;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class StudentRequiredSubjectsServiceImpl implements StudentRequiredSubjectsService {
    private final StudentRequiredMajorSubjectsDAO studentRequiredSubjectsDAO;

    public StudentRequiredSubjectsServiceImpl(StudentRequiredMajorSubjectsDAO studentRequiredSubjectsDAO) {
        this.studentRequiredSubjectsDAO = studentRequiredSubjectsDAO;
    }

    @Override
    public List<StudentRequiredMajorSubjects> getStudentRequiredMajorSubjects(MajorSubjects subjects) {
        return studentRequiredSubjectsDAO.getStudentRequiredMajorSubjects(subjects);
    }

    @Override
    public List<Students> getStudentNotRequiredMajorSubjects(MajorSubjects subjects) {
        return studentRequiredSubjectsDAO.getStudentNotRequiredMajorSubjects(subjects);
    }
}
