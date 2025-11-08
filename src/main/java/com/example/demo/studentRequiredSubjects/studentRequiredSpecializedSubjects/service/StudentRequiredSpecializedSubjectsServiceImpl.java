package com.example.demo.studentRequiredSubjects.studentRequiredSpecializedSubjects.service;
import com.example.demo.subject.specializedSubject.model.SpecializedSubject;
import com.example.demo.user.student.model.Students;
import com.example.demo.studentRequiredSubjects.studentRequiredSpecializedSubjects.dao.StudentRequiredSpecializedSubjectsDAO;
import com.example.demo.studentRequiredSubjects.studentRequiredSpecializedSubjects.model.StudentRequiredSpecializedSubjects;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class StudentRequiredSpecializedSubjectsServiceImpl implements StudentRequiredSpecializedSubjectsService{
    @Override
    public List<Students> getStudentNotRequiredSpecializedSubjects(SpecializedSubject subject, Integer admissionYear) {
        return studentRequiredSpecializedSubjectsDAO.getStudentNotRequiredSpecializedSubjects(subject,admissionYear);
    }

    @Override
    public boolean isStudentAlreadyRequiredForSpecializedSubject(String studentId, String subjectId) {
        return studentRequiredSpecializedSubjectsDAO.isStudentAlreadyRequiredForSpecializedSubject(studentId, subjectId);
    }

    @Override
    public List<SpecializedSubject> studentSpecializedRoadmap(Students student) {
        return studentRequiredSpecializedSubjectsDAO.studentSpecializedRoadmap(student);
    }

    @Override
    public List<StudentRequiredSpecializedSubjects> getStudentRequiredSpecializedSubjects(SpecializedSubject subject, Integer admissionYear) {
        return studentRequiredSpecializedSubjectsDAO.getStudentRequiredSpecializedSubjects(subject, admissionYear);
    }

    @Override
    public List<SpecializedSubject> getSubjectsByCurriculumId(String curriculumId) {
        return studentRequiredSpecializedSubjectsDAO.getSubjectsByCurriculumId(curriculumId);
    }

    @Override
    public boolean isStudentAlreadyRequiredForSubject(String studentId, String subjectId) {
        return studentRequiredSpecializedSubjectsDAO.isStudentAlreadyRequiredForSubject(studentId, subjectId);
    }

    @Override
    public void addStudentRequiredSpecializedSubject(StudentRequiredSpecializedSubjects srs) {
        studentRequiredSpecializedSubjectsDAO.addStudentRequiredSpecializedSubject(srs);
    }

    @Override
    public boolean removeStudentRequiredSpecializedSubject(String studentId, String subjectId) {
        return studentRequiredSpecializedSubjectsDAO.removeStudentRequiredSpecializedSubject(studentId, subjectId);
    }

    private final StudentRequiredSpecializedSubjectsDAO studentRequiredSpecializedSubjectsDAO;

    public StudentRequiredSpecializedSubjectsServiceImpl(StudentRequiredSpecializedSubjectsDAO studentRequiredSpecializedSubjectsDAO) {
        this.studentRequiredSpecializedSubjectsDAO = studentRequiredSpecializedSubjectsDAO;
    }
}
