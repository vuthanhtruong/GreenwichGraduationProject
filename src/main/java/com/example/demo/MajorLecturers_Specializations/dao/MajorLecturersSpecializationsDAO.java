package com.example.demo.MajorLecturers_Specializations.dao;

import com.example.demo.MajorLecturers_Specializations.model.MajorLecturers_Specializations;
import com.example.demo.Specialization.model.Specialization;
import com.example.demo.lecturer.model.MajorLecturers;

import java.util.List;

public interface MajorLecturersSpecializationsDAO {
    List<MajorLecturers> getLecturersNotAssignedToSpecialization(Specialization specialization);
    List<MajorLecturers_Specializations> getLecturersAssignedToSpecialization(Specialization specialization);
    boolean isLecturerAlreadyAssignedToSpecialization(String lecturerId, String specializationId);
    void addLecturerSpecialization(MajorLecturers_Specializations assignment);
    boolean removeLecturerSpecialization(String lecturerId, String specializationId);
    List<MajorLecturers> getLecturersNotAssignedToSpecialization(Specialization specialization, int firstResult, int maxResults);
    List<MajorLecturers_Specializations> getLecturersAssignedToSpecialization(Specialization specialization, int firstResult, int maxResults);
}
