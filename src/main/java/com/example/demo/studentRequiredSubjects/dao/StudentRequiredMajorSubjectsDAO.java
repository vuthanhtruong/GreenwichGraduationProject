package com.example.demo.studentRequiredSubjects.dao;

import com.example.demo.subject.model.MajorSubjects;
import com.example.demo.studentRequiredSubjects.model.StudentRequiredMajorSubjects;
import com.example.demo.student.model.Students;

import java.util.List;

public interface StudentRequiredMajorSubjectsDAO {
    List<StudentRequiredMajorSubjects> getStudentRequiredMajorSubjects(MajorSubjects subjects);
    List<Students> getStudentNotRequiredMajorSubjects(MajorSubjects subjects);
    List<MajorSubjects> getSubjectsByLearningProgramType(String learningProgramType);
}
