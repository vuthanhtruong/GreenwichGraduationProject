package com.example.demo.MajorLecturers_Specializations.dao;

import com.example.demo.MajorLecturers_Specializations.model.MajorLecturers_Specializations;
import com.example.demo.lecturer.model.MajorLecturers;
import com.example.demo.Specialization.model.Specialization;
import com.example.demo.staff.service.StaffsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class MajorLecturersSpecializationsDAOImpl implements MajorLecturersSpecializationsDAO {

    @PersistenceContext
    private EntityManager entityManager;

    private final StaffsService staffsService;

    public MajorLecturersSpecializationsDAOImpl(StaffsService staffsService) {
        if (staffsService == null) {
            throw new IllegalArgumentException("StaffsService cannot be null");
        }
        this.staffsService = staffsService;
    }

    @Override
    public List<MajorLecturers> getLecturersNotAssignedToSpecialization(Specialization specialization) {
        if (specialization == null || specialization.getSpecializationId() == null || staffsService.getStaffMajor() == null) {
            return List.of();
        }

        try {
            Query query = entityManager.createQuery(
                    "SELECT l FROM MajorLecturers l LEFT JOIN MajorLecturers_Specializations mls " +
                            "ON l.id = mls.majorLecturer.id AND mls.specialization.specializationId = :specializationId " +
                            "WHERE l.majorManagement = :major AND mls.majorLecturer.id IS NULL",
                    MajorLecturers.class);
            query.setParameter("specializationId", specialization.getSpecializationId());
            query.setParameter("major", staffsService.getStaffMajor());
            return query.getResultList();
        } catch (Exception e) {
            // Log exception if logging is configured
            return List.of();
        }
    }

    @Override
    public List<MajorLecturers_Specializations> getLecturersAssignedToSpecialization(Specialization specialization) {
        if (specialization == null || specialization.getSpecializationId() == null || staffsService.getStaffMajor() == null) {
            return List.of();
        }

        try {
            Query query = entityManager.createQuery(
                    "SELECT mls FROM MajorLecturers_Specializations mls " +
                            "WHERE mls.specialization.specializationId = :specializationId AND mls.majorLecturer.majorManagement = :major",
                    MajorLecturers_Specializations.class);
            query.setParameter("specializationId", specialization.getSpecializationId());
            query.setParameter("major", staffsService.getStaffMajor());
            return query.getResultList();
        } catch (Exception e) {
            // Log exception if logging is configured
            return List.of();
        }
    }

    @Override
    public boolean isLecturerAlreadyAssignedToSpecialization(String lecturerId, String specializationId) {
        if (lecturerId == null || specializationId == null) {
            return false;
        }

        try {
            Long count = entityManager.createQuery(
                            "SELECT COUNT(mls) FROM MajorLecturers_Specializations mls " +
                                    "WHERE mls.majorLecturer.id = :lecturerId AND mls.specialization.specializationId = :specializationId",
                            Long.class)
                    .setParameter("lecturerId", lecturerId)
                    .setParameter("specializationId", specializationId)
                    .getSingleResult();
            return count > 0;
        } catch (Exception e) {
            // Log exception if logging is configured
            return false;
        }
    }

    @Override
    public void addLecturerSpecialization(MajorLecturers_Specializations assignment) {
        if (assignment == null || assignment.getId() == null) {
            throw new IllegalArgumentException("MajorLecturers_Specializations and its ID cannot be null");
        }
        try {
            entityManager.persist(assignment);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to add lecturer specialization: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean removeLecturerSpecialization(String lecturerId, String specializationId) {
        if (lecturerId == null || specializationId == null) {
            return false;
        }

        try {
            Query query = entityManager.createQuery(
                    "DELETE FROM MajorLecturers_Specializations mls " +
                            "WHERE mls.majorLecturer.id = :lecturerId AND mls.specialization.specializationId = :specializationId");
            query.setParameter("lecturerId", lecturerId);
            query.setParameter("specializationId", specializationId);
            int rowsAffected = query.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            // Log exception if logging is configured
            return false;
        }
    }

    // Optional: Add pagination support if needed by LecturesService
    public List<MajorLecturers> getLecturersNotAssignedToSpecialization(Specialization specialization, int firstResult, int maxResults) {
        if (specialization == null || specialization.getSpecializationId() == null || staffsService.getStaffMajor() == null) {
            return List.of();
        }

        try {
            Query query = entityManager.createQuery(
                    "SELECT l FROM MajorLecturers l LEFT JOIN MajorLecturers_Specializations mls " +
                            "ON l.id = mls.majorLecturer.id AND mls.specialization.specializationId = :specializationId " +
                            "WHERE l.majorManagement = :major AND mls.majorLecturer.id IS NULL",
                    MajorLecturers.class);
            query.setParameter("specializationId", specialization.getSpecializationId());
            query.setParameter("major", staffsService.getStaffMajor());
            query.setFirstResult(firstResult);
            query.setMaxResults(maxResults);
            return query.getResultList();
        } catch (Exception e) {
            // Log exception if logging is configured
            return List.of();
        }
    }

    public List<MajorLecturers_Specializations> getLecturersAssignedToSpecialization(Specialization specialization, int firstResult, int maxResults) {
        if (specialization == null || specialization.getSpecializationId() == null || staffsService.getStaffMajor() == null) {
            return List.of();
        }

        try {
            Query query = entityManager.createQuery(
                    "SELECT mls FROM MajorLecturers_Specializations mls " +
                            "WHERE mls.specialization.specializationId = :specializationId AND mls.majorLecturer.majorManagement = :major",
                    MajorLecturers_Specializations.class);
            query.setParameter("specializationId", specialization.getSpecializationId());
            query.setParameter("major", staffsService.getStaffMajor());
            query.setFirstResult(firstResult);
            query.setMaxResults(maxResults);
            return query.getResultList();
        } catch (Exception e) {
            // Log exception if logging is configured
            return List.of();
        }
    }
}