package com.example.demo.dao;

import com.example.demo.entity.MajorSubjects;
import com.example.demo.entity.AbstractClasses.StudentRequiredSubjects;
import com.example.demo.entity.StudentRequiredMajorSubjects;
import com.example.demo.entity.Students;

import java.util.List;

public interface StudentRequiredMajorSubjectsDAO {
    List<StudentRequiredMajorSubjects> getStudentRequiredMajorSubjects(MajorSubjects subjects);
    List<Students> getStudentNotRequiredMajorSubjects(MajorSubjects subjects);
}
