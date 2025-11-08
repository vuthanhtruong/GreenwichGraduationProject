package com.example.demo.academicTranscript.dao;

import com.example.demo.academicTranscript.model.*;
import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import com.example.demo.user.staff.model.Staffs;
import com.example.demo.user.student.model.Students;
import com.example.demo.students_Classes.abstractStudents_Class.model.Students_Classes;

import java.util.List;

public interface AcademicTranscriptsDAO {

    // === MAJOR ===
    List<MajorAcademicTranscripts> getTranscriptsByClass(MajorClasses majorClass);
    MajorAcademicTranscripts findOrCreateTranscript(String transcriptId, Students student, MajorClasses majorClass, Staffs creator);
    void saveOrUpdateTranscript(MajorAcademicTranscripts transcript);

    // === SPECIALIZED ===
    List<SpecializedAcademicTranscripts> getTranscriptsByClass(SpecializedClasses specializedClass);
    SpecializedAcademicTranscripts findOrCreateTranscript(String transcriptId, Students student, SpecializedClasses specializedClass, Staffs creator);
    void saveOrUpdateTranscript(SpecializedAcademicTranscripts transcript);

    // === MINOR ===
    List<MinorAcademicTranscripts> getTranscriptsByClass(MinorClasses minorClass);
    MinorAcademicTranscripts findOrCreateTranscript(String transcriptId, Students student, MinorClasses minorClass, DeputyStaffs creator);
    void saveOrUpdateTranscript(MinorAcademicTranscripts transcript);

    // === CÁC PHƯƠNG THỨC CŨ ===
    List<MajorAcademicTranscripts> getMajorAcademicTranscripts(Students student);
    List<MinorAcademicTranscripts> getMinorAcademicTranscripts(Students student);
    List<SpecializedAcademicTranscripts> getSpecializedAcademicTranscripts(Students student);
    List<Students_Classes> getLearningProcess(Students student);
    List<MajorAcademicTranscripts> getAcademicTranscriptsByMajorClass(Students student, MajorClasses majorClass);
    List<MinorAcademicTranscripts> getAcademicTranscriptsByMinorClass(Students student, MinorClasses classes);
    List<SpecializedAcademicTranscripts> getAcademicTranscriptsBySpecializedClass(Students student, SpecializedClasses classes);
    List<AcademicTranscripts> getFailSubjectsByStudent(Students student);
}