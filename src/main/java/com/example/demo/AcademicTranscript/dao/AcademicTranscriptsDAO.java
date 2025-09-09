package com.example.demo.AcademicTranscript.dao;

import com.example.demo.AcademicTranscript.model.MajorAcademicTranscripts;
import com.example.demo.AcademicTranscript.model.MinorAcademicTranscripts;
import com.example.demo.student.model.Students;

import java.util.List;

public interface AcademicTranscriptsDAO {
    List<MajorAcademicTranscripts>  getMajorAcademicTranscripts(Students student);
    List<MinorAcademicTranscripts> getMinorAcademicTranscripts(Students student);
}
