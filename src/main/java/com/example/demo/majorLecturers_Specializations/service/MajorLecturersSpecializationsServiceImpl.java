package com.example.demo.majorLecturers_Specializations.service;

import com.example.demo.majorLecturers_Specializations.model.MajorLecturers_Specializations;
import com.example.demo.majorLecturers_Specializations.dao.MajorLecturersSpecializationsDAO;
import com.example.demo.specialization.model.Specialization;
import com.example.demo.user.majorLecturer.model.MajorLecturers;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MajorLecturersSpecializationsServiceImpl implements MajorLecturersSpecializationsService {
    @Override
    public List<MajorLecturers_Specializations> getSpecializationsByLecturer(MajorLecturers lecturer) {
        return majorLecturersSpecializationsDAO.getSpecializationsByLecturer(lecturer);
    }

    @Override
    public List<MajorLecturers> getLecturersNotAssignedToSpecialization(Specialization specialization, int firstResult, int maxResults) {
        return majorLecturersSpecializationsDAO.getLecturersNotAssignedToSpecialization( specialization, firstResult, maxResults );
    }

    @Override
    public List<MajorLecturers_Specializations> getLecturersAssignedToSpecialization(Specialization specialization, int firstResult, int maxResults) {
        return majorLecturersSpecializationsDAO.getLecturersAssignedToSpecialization( specialization, firstResult, maxResults );
    }

    private final MajorLecturersSpecializationsDAO majorLecturersSpecializationsDAO;

    public MajorLecturersSpecializationsServiceImpl(MajorLecturersSpecializationsDAO majorLecturersSpecializationsDAO) {
        if (majorLecturersSpecializationsDAO == null) {
            throw new IllegalArgumentException("MajorLecturersSpecializationsDAO cannot be null");
        }
        this.majorLecturersSpecializationsDAO = majorLecturersSpecializationsDAO;
    }

    @Override
    public List<MajorLecturers> getLecturersNotAssignedToSpecialization(Specialization specialization) {
        try {
            return majorLecturersSpecializationsDAO.getLecturersNotAssignedToSpecialization(specialization);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving unassigned lecturers: " + e.getMessage(), e);
        }
    }

    @Override
    public List<MajorLecturers_Specializations> getLecturersAssignedToSpecialization(Specialization specialization) {
        try {
            return majorLecturersSpecializationsDAO.getLecturersAssignedToSpecialization(specialization);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving assigned lecturers: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isLecturerAlreadyAssignedToSpecialization(String lecturerId, String specializationId) {
        try {
            return majorLecturersSpecializationsDAO.isLecturerAlreadyAssignedToSpecialization(lecturerId, specializationId);
        } catch (Exception e) {
            throw new RuntimeException("Error checking lecturer assignment: " + e.getMessage(), e);
        }
    }

    @Override
    public void addLecturerSpecialization(MajorLecturers_Specializations assignment) {
        try {
            majorLecturersSpecializationsDAO.addLecturerSpecialization(assignment);
        } catch (Exception e) {
            throw new RuntimeException("Error adding lecturer specialization: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean removeLecturerSpecialization(String lecturerId, String specializationId) {
        try {
            return majorLecturersSpecializationsDAO.removeLecturerSpecialization(lecturerId, specializationId);
        } catch (Exception e) {
            throw new RuntimeException("Error removing lecturer specialization: " + e.getMessage(), e);
        }
    }
}
