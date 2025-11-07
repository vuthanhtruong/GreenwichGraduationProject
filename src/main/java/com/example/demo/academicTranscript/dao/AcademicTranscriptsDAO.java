package com.example.demo.academicTranscript.dao;

import com.example.demo.academicTranscript.model.AcademicTranscripts;
import com.example.demo.academicTranscript.model.MajorAcademicTranscripts;
import com.example.demo.academicTranscript.model.MinorAcademicTranscripts;
import com.example.demo.academicTranscript.model.SpecializedAcademicTranscripts;
import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.user.student.model.Students;
import com.example.demo.students_Classes.abstractStudents_Class.model.Students_Classes;

import java.util.List;

public interface AcademicTranscriptsDAO {
    List<MajorAcademicTranscripts>  getMajorAcademicTranscripts(Students student);
    List<MinorAcademicTranscripts> getMinorAcademicTranscripts(Students student);
    List<SpecializedAcademicTranscripts> getSpecializedAcademicTranscripts(Students student);
    List<Students_Classes> getLearningProcess(Students student);
    List<MajorAcademicTranscripts> getAcademicTranscriptsByMajorClass(Students student, MajorClasses majorClass);
    List<MinorAcademicTranscripts> getAcademicTranscriptsByMinorClass(Students student, MinorClasses classes);
    List<SpecializedAcademicTranscripts> getAcademicTranscriptsBySpecializedClass(Students student, SpecializedClasses classes);
    List<AcademicTranscripts> getFailSubjectsByStudent(Students student);
}
