package com.example.demo.studentRequiredSpecializedSubjects.service;

import com.example.demo.specializedSubject.model.SpecializedSubject;
import com.example.demo.student.model.Students;
import com.example.demo.studentRequiredSpecializedSubjects.model.StudentRequiredSpecializedSubjects;

import java.util.List;

public interface StudentRequiredSpecializedSubjectsService {
    List<SpecializedSubject> studentSpecializedRoadmap(Students student);
    List<StudentRequiredSpecializedSubjects> getStudentRequiredSpecializedSubjects(SpecializedSubject subject);
    List<Students> getStudentNotRequiredSpecializedSubjects(SpecializedSubject subject);
    List<SpecializedSubject> getSubjectsByCurriculumId(String curriculumId);
    boolean isStudentAlreadyRequiredForSubject(String studentId, String subjectId);
    void addStudentRequiredSpecializedSubject(StudentRequiredSpecializedSubjects srs);
    boolean removeStudentRequiredSpecializedSubject(String studentId, String subjectId);
    boolean isStudentAlreadyRequiredForSpecializedSubject(String studentId, String subjectId);
}
