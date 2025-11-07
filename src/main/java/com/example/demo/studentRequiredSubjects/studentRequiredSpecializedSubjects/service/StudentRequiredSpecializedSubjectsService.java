package com.example.demo.studentRequiredSubjects.studentRequiredSpecializedSubjects.service;

import com.example.demo.subject.specializedSubject.model.SpecializedSubject;
import com.example.demo.user.student.model.Students;
import com.example.demo.studentRequiredSubjects.studentRequiredSpecializedSubjects.model.StudentRequiredSpecializedSubjects;

import java.time.LocalDate;
import java.util.List;

public interface StudentRequiredSpecializedSubjectsService {
    List<SpecializedSubject> studentSpecializedRoadmap(Students student);
    List<StudentRequiredSpecializedSubjects> getStudentRequiredSpecializedSubjects(SpecializedSubject subject);
    List<Students> getStudentNotRequiredSpecializedSubjects(SpecializedSubject subject, LocalDate admissionYear);
    List<SpecializedSubject> getSubjectsByCurriculumId(String curriculumId);
    boolean isStudentAlreadyRequiredForSubject(String studentId, String subjectId);
    void addStudentRequiredSpecializedSubject(StudentRequiredSpecializedSubjects srs);
    boolean removeStudentRequiredSpecializedSubject(String studentId, String subjectId);
    boolean isStudentAlreadyRequiredForSpecializedSubject(String studentId, String subjectId);
}
