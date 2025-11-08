package com.example.demo.studentRequiredSubjects.studentRequiredMajorSubjects.service;

import com.example.demo.studentRequiredSubjects.studentRequiredMajorSubjects.dao.StudentRequiredMajorSubjectsDAO;
import com.example.demo.subject.majorSubject.model.MajorSubjects;
import com.example.demo.studentRequiredSubjects.studentRequiredMajorSubjects.model.StudentRequiredMajorSubjects;
import com.example.demo.user.student.model.Students;
import com.example.demo.subject.minorSubject.model.MinorSubjects;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class StudentRequiredMajorSubjectsServiceImpl implements StudentRequiredMajorSubjectsService {
    @Override
    public List<Students> getStudentNotRequiredMajorSubjects(MajorSubjects subjects, Integer admissionYear) {
        return studentRequiredSubjectsDAO.getStudentNotRequiredMajorSubjects(subjects, admissionYear);
    }

    @Override
    public List<MajorSubjects> getSubjectsByCurriculumId(String curriculumId) {
        return studentRequiredSubjectsDAO.getSubjectsByCurriculumId(curriculumId);
    }

    @Override
    public boolean isStudentAlreadyRequiredForSubject(String studentId, String subjectId) {
        return studentId.equals(subjectId);
    }

    @Override
    public void addStudentRequiredMajorSubject(StudentRequiredMajorSubjects srm) {
        studentRequiredSubjectsDAO.addStudentRequiredMajorSubject(srm);
    }

    @Override
    public boolean removeStudentRequiredMajorSubject(String studentId, String subjectId) {
        return studentRequiredSubjectsDAO.removeStudentRequiredMajorSubject(studentId, subjectId);
    }

    @Override
    public List<MajorSubjects> studentMajorRoadmap(Students student) {
        return studentRequiredSubjectsDAO.studentMajorRoadmap(student);
    }

    @Override
    public List<MinorSubjects> studentMinorRoadmap(Students student) {
        return studentRequiredSubjectsDAO.studentMinorRoadmap(student);
    }

    private final StudentRequiredMajorSubjectsDAO studentRequiredSubjectsDAO;

    public StudentRequiredMajorSubjectsServiceImpl(StudentRequiredMajorSubjectsDAO studentRequiredSubjectsDAO) {
        this.studentRequiredSubjectsDAO = studentRequiredSubjectsDAO;
    }

    @Override
    public List<StudentRequiredMajorSubjects> getStudentRequiredMajorSubjects(MajorSubjects subjects,Integer admissionYear) {
        return studentRequiredSubjectsDAO.getStudentRequiredMajorSubjects(subjects, admissionYear);
    }

}
