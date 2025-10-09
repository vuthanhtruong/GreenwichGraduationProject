package com.example.demo.studentRequiredSubjects.dao;

import com.example.demo.majorSubject.model.MajorSubjects;
import com.example.demo.studentRequiredSubjects.model.StudentRequiredMajorSubjects;
import com.example.demo.student.model.Students;
import com.example.demo.subject.model.MinorSubjects;

import java.util.List;

public interface StudentRequiredMajorSubjectsDAO {
    List<StudentRequiredMajorSubjects> getStudentRequiredMajorSubjects(MajorSubjects subjects);
    List<Students> getStudentNotRequiredMajorSubjects(MajorSubjects subjects);
    List<MajorSubjects> getSubjectsByCurriculumId(String curriculumId);
    List<MajorSubjects> studentMajorRoadmap(Students student);
    List<MinorSubjects> studentMinorRoadmap(Students student);
    boolean isStudentAlreadyRequiredForSubject(String studentId, String subjectId);
    void addStudentRequiredMajorSubject(StudentRequiredMajorSubjects srm);
    boolean removeStudentRequiredMajorSubject(String studentId, String subjectId);
}
