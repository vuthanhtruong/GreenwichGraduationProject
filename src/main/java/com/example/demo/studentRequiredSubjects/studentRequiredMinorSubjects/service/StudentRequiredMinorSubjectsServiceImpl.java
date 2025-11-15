package com.example.demo.studentRequiredSubjects.studentRequiredMinorSubjects.service;

import com.example.demo.subject.minorSubject.model.MinorSubjects;
import com.example.demo.studentRequiredSubjects.studentRequiredMinorSubjects.dao.StudentRequiredMinorSubjectsDAO;
import com.example.demo.studentRequiredSubjects.studentRequiredMinorSubjects.model.StudentRequiredMinorSubjects;
import com.example.demo.user.student.model.Students;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentRequiredMinorSubjectsServiceImpl implements StudentRequiredMinorSubjectsService {
    @Override
    public List<String> getRequiredSubjectNotificationsForStudent(String studentId) {
        return dao.getRequiredSubjectNotificationsForStudent(studentId);
    }

    private final StudentRequiredMinorSubjectsDAO dao;

    public StudentRequiredMinorSubjectsServiceImpl(StudentRequiredMinorSubjectsDAO dao) {
        this.dao = dao;
    }

    @Override
    public List<StudentRequiredMinorSubjects> getStudentRequiredMinorSubjects(MinorSubjects subject) {
        if (subject == null) {
            throw new IllegalArgumentException("MinorSubjects cannot be null");
        }
        return dao.getStudentRequiredMinorSubjects(subject);
    }

    @Override
    public List<Students> getStudentsNotRequiredMinorSubject(MinorSubjects subject) {
        if (subject == null) {
            throw new IllegalArgumentException("MinorSubjects cannot be null");
        }
        return dao.getStudentsNotRequiredMinorSubject(subject);
    }

    @Override
    public boolean isStudentAlreadyRequiredForSubject(String studentId, String subjectId) {
        if (studentId == null || subjectId == null) {
            throw new IllegalArgumentException("Student ID and Subject ID cannot be null");
        }
        return dao.isStudentAlreadyRequiredForSubject(studentId, subjectId);
    }

    @Override
    public void addStudentRequiredMinorSubject(StudentRequiredMinorSubjects srm) {
        if (srm == null) {
            throw new IllegalArgumentException("StudentRequiredMinorSubjects cannot be null");
        }
        dao.addStudentRequiredMinorSubject(srm);
    }

    @Override
    public boolean removeStudentRequiredMinorSubject(String studentId, String subjectId) {
        if (studentId == null || subjectId == null) {
            throw new IllegalArgumentException("Student ID and Subject ID cannot be null");
        }
        return dao.removeStudentRequiredMinorSubject(studentId, subjectId);
    }
}