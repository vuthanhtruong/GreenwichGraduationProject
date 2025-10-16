package com.example.demo.AcademicTranscript.service;

import com.example.demo.AcademicTranscript.model.MajorAcademicTranscripts;
import com.example.demo.AcademicTranscript.model.MinorAcademicTranscripts;
import com.example.demo.AcademicTranscript.model.SpecializedAcademicTranscripts;
import com.example.demo.classes.model.Classes;
import com.example.demo.classes.model.MajorClasses;
import com.example.demo.classes.model.MinorClasses;
import com.example.demo.specializedClasses.model.SpecializedClasses;
import com.example.demo.student.model.Students;
import com.example.demo.student_class.model.Students_Classes;

import java.util.List;

public interface AcademicTranscriptsService {
    List<MajorAcademicTranscripts>  getMajorAcademicTranscripts(Students student);
    List<MinorAcademicTranscripts> getMinorAcademicTranscripts(Students student);
    List<SpecializedAcademicTranscripts> getSpecializedAcademicTranscripts(Students student);
    List<Students_Classes> getLearningProcess(Students student);
    List<MajorAcademicTranscripts> getAcademicTranscriptsByMajorClass(Students student, MajorClasses majorClass);
    List<MinorAcademicTranscripts> getAcademicTranscriptsByMinorClass(Students student, MinorClasses classes);
    List<SpecializedAcademicTranscripts> getAcademicTranscriptsBySpecializedClass(Students student, SpecializedClasses classes);
}
