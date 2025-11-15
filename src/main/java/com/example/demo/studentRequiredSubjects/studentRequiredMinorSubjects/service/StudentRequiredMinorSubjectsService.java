package com.example.demo.studentRequiredSubjects.studentRequiredMinorSubjects.service;

import com.example.demo.subject.minorSubject.model.MinorSubjects;
import com.example.demo.studentRequiredSubjects.studentRequiredMinorSubjects.model.StudentRequiredMinorSubjects;
import com.example.demo.user.student.model.Students;

import java.util.List;

public interface StudentRequiredMinorSubjectsService {

    List<StudentRequiredMinorSubjects> getStudentRequiredMinorSubjects(MinorSubjects subject);

    List<Students> getStudentsNotRequiredMinorSubject(MinorSubjects subject);

    boolean isStudentAlreadyRequiredForSubject(String studentId, String subjectId);

    void addStudentRequiredMinorSubject(StudentRequiredMinorSubjects srm);

    boolean removeStudentRequiredMinorSubject(String studentId, String subjectId);
    List<String> getRequiredSubjectNotificationsForStudent(String studentId);
}