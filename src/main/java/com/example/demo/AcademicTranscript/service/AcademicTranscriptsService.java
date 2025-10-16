package com.example.demo.AcademicTranscript.service;

import com.example.demo.AcademicTranscript.model.MajorAcademicTranscripts;
import com.example.demo.AcademicTranscript.model.MinorAcademicTranscripts;
import com.example.demo.AcademicTranscript.model.SpecializedAcademicTranscripts;
import com.example.demo.student.model.Students;

import java.util.List;

public interface AcademicTranscriptsService {
    List<MajorAcademicTranscripts>  getMajorAcademicTranscripts(Students student);
    List<MinorAcademicTranscripts> getMinorAcademicTranscripts(Students student);
    List<SpecializedAcademicTranscripts> getSpecializedAcademicTranscripts(Students student);
}
