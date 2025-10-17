package com.example.demo.lecturers_Classes.majorLecturers_Specializations.service;

import com.example.demo.lecturers_Classes.majorLecturers_Specializations.model.MajorLecturers_Specializations;
import com.example.demo.Specialization.model.Specialization;
import com.example.demo.user.majorLecturer.model.MajorLecturers;

import java.util.List;

public interface MajorLecturersSpecializationsService {
    List<MajorLecturers> getLecturersNotAssignedToSpecialization(Specialization specialization);
    List<MajorLecturers_Specializations> getLecturersAssignedToSpecialization(Specialization specialization);
    boolean isLecturerAlreadyAssignedToSpecialization(String lecturerId, String specializationId);
    void addLecturerSpecialization(MajorLecturers_Specializations assignment);
    boolean removeLecturerSpecialization(String lecturerId, String specializationId);
    List<MajorLecturers> getLecturersNotAssignedToSpecialization(Specialization specialization, int firstResult, int maxResults);
    List<MajorLecturers_Specializations> getLecturersAssignedToSpecialization(Specialization specialization, int firstResult, int maxResults);
}
