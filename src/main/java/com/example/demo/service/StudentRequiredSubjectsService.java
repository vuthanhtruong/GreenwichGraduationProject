package com.example.demo.service;

import com.example.demo.entity.MajorSubjects;
import com.example.demo.entity.AbstractClasses.StudentRequiredSubjects;
import com.example.demo.entity.StudentRequiredMajorSubjects;
import com.example.demo.entity.Students;

import java.util.List;

public interface StudentRequiredSubjectsService {
    List<StudentRequiredMajorSubjects> getStudentRequiredMajorSubjects(MajorSubjects subjects);
    List<Students> getStudentNotRequiredMajorSubjects(MajorSubjects subjects);
}
