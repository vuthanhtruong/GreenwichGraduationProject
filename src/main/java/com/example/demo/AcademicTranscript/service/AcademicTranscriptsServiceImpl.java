package com.example.demo.AcademicTranscript.service;

import com.example.demo.AcademicTranscript.dao.AcademicTranscriptsDAO;
import com.example.demo.AcademicTranscript.model.MajorAcademicTranscripts;
import com.example.demo.AcademicTranscript.model.MinorAcademicTranscripts;
import com.example.demo.student.model.Students;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AcademicTranscriptsServiceImpl implements AcademicTranscriptsService {
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
