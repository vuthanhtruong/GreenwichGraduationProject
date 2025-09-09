package com.example.demo.studentRequiredSubjects.service;

import com.example.demo.subject.model.MajorSubjects;
import com.example.demo.studentRequiredSubjects.model.StudentRequiredMajorSubjects;
import com.example.demo.student.model.Students;
import com.example.demo.subject.model.MinorSubjects;

import java.util.List;

public interface StudentRequiredSubjectsService {
    List<StudentRequiredMajorSubjects> getStudentRequiredMajorSubjects(MajorSubjects subjects);
    List<Students> getStudentNotRequiredMajorSubjects(MajorSubjects subjects);
    List<MajorSubjects> getSubjectsByLearningProgramType(String learningProgramType);
    List<MajorSubjects> studentMajorRoadmap(Students student);
    List<MinorSubjects> studentMinorRoadmap(Students student);
}
