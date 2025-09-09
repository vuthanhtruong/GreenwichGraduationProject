package com.example.demo.studentRequiredSubjects.service;

import com.example.demo.studentRequiredSubjects.dao.StudentRequiredMajorSubjectsDAO;
import com.example.demo.subject.model.MajorSubjects;
import com.example.demo.studentRequiredSubjects.model.StudentRequiredMajorSubjects;
import com.example.demo.student.model.Students;
import com.example.demo.subject.model.MinorSubjects;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class StudentRequiredSubjectsServiceImpl implements StudentRequiredSubjectsService {
    @Override
    public List<MajorSubjects> studentMajorRoadmap(Students student) {
        return studentRequiredSubjectsDAO.studentMajorRoadmap(student);
    }

    @Override
    public List<MinorSubjects> studentMinorRoadmap(Students student) {
        return studentRequiredSubjectsDAO.studentMinorRoadmap(student);
    }

    @Override
    public List<MajorSubjects> getSubjectsByLearningProgramType(String learningProgramType) {
        return studentRequiredSubjectsDAO.getSubjectsByLearningProgramType(learningProgramType);
    }

    private final StudentRequiredMajorSubjectsDAO studentRequiredSubjectsDAO;

    public StudentRequiredSubjectsServiceImpl(StudentRequiredMajorSubjectsDAO studentRequiredSubjectsDAO) {
        this.studentRequiredSubjectsDAO = studentRequiredSubjectsDAO;
    }

    @Override
    public List<StudentRequiredMajorSubjects> getStudentRequiredMajorSubjects(MajorSubjects subjects) {
        return studentRequiredSubjectsDAO.getStudentRequiredMajorSubjects(subjects);
    }

    @Override
    public List<Students> getStudentNotRequiredMajorSubjects(MajorSubjects subjects) {
        return studentRequiredSubjectsDAO.getStudentNotRequiredMajorSubjects(subjects);
    }
}
