package com.example.demo.academicTranscript.service;

import com.example.demo.academicTranscript.dao.AcademicTranscriptsDAO;
import com.example.demo.academicTranscript.model.MajorAcademicTranscripts;
import com.example.demo.academicTranscript.model.MinorAcademicTranscripts;
import com.example.demo.academicTranscript.model.SpecializedAcademicTranscripts;
import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.user.student.model.Students;
import com.example.demo.students_Classes.abstractStudents_Class.model.Students_Classes;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AcademicTranscriptsServiceImpl implements AcademicTranscriptsService {
    @Override
    public List<MajorAcademicTranscripts> getAcademicTranscriptsByMajorClass(Students student, MajorClasses majorClass) {
        return academicTranscriptsDAO.getAcademicTranscriptsByMajorClass(student, majorClass);
    }

    @Override
    public List<MinorAcademicTranscripts> getAcademicTranscriptsByMinorClass(Students student, MinorClasses classes) {
        return academicTranscriptsDAO.getAcademicTranscriptsByMinorClass(student, classes);
    }

    @Override
    public List<SpecializedAcademicTranscripts> getAcademicTranscriptsBySpecializedClass(Students student, SpecializedClasses classes) {
        return academicTranscriptsDAO.getAcademicTranscriptsBySpecializedClass(student, classes);
    }

    @Override
    public List<Students_Classes> getLearningProcess(Students student) {
        return academicTranscriptsDAO.getLearningProcess(student);
    }

    @Override
    public List<SpecializedAcademicTranscripts> getSpecializedAcademicTranscripts(Students student) {
        return academicTranscriptsDAO.getSpecializedAcademicTranscripts(student);
    }

    @Override
    public List<MajorAcademicTranscripts> getMajorAcademicTranscripts(Students student) {
        return academicTranscriptsDAO.getMajorAcademicTranscripts(student);
    }

    @Override
    public List<MinorAcademicTranscripts> getMinorAcademicTranscripts(Students student) {
        return academicTranscriptsDAO.getMinorAcademicTranscripts(student);
    }

    private final AcademicTranscriptsDAO academicTranscriptsDAO;

    public AcademicTranscriptsServiceImpl(AcademicTranscriptsDAO academicTranscriptsDAO) {
        this.academicTranscriptsDAO = academicTranscriptsDAO;
    }
}
