package com.example.demo.studentRequiredSubjects.studentRequiredMajorSubjects.dao;

import com.example.demo.subject.majorSubject.model.MajorSubjects;
import com.example.demo.studentRequiredSubjects.studentRequiredMajorSubjects.model.StudentRequiredMajorSubjects;
import com.example.demo.user.student.model.Students;
import com.example.demo.subject.abstractSubject.model.MinorSubjects;

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
