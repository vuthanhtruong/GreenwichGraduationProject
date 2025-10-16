package com.example.demo.majorLecturers_Specializations.dao;

import com.example.demo.majorLecturers_Specializations.model.MajorLecturers_Specializations;
import com.example.demo.lecturer.model.MajorLecturers;
import com.example.demo.Specialization.model.Specialization;
import com.example.demo.staff.service.StaffsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class MajorLecturersSpecializationsDAOImpl implements MajorLecturersSpecializationsDAO {

    private static final Logger logger = LoggerFactory.getLogger(MajorLecturersSpecializationsDAOImpl.class);

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
        if (specialization == null || staffsService.getStaffMajor() == null) {
            return List.of();
        }
        try {
            List<MajorLecturers> result = entityManager.createQuery(
                            "SELECT l FROM MajorLecturers l " +
                                    "WHERE l.majorManagement = :major " +
                                    "AND NOT EXISTS (" +
                                    "   SELECT 1 FROM MajorLecturers_Specializations mls " +
                                    "   WHERE mls.majorLecturer = l " +
                                    "   AND mls.specialization.specializationId = :specializationId" +
                                    ")",
                            MajorLecturers.class)
                    .setParameter("major", staffsService.getStaffMajor())
                    .setParameter("specializationId", specialization.getSpecializationId())
                    .getResultList();

            logger.info("Fetched {} unassigned lecturers for specialization {}", result.size(), specialization.getSpecializationId());
            return result;
        } catch (Exception e) {
            logger.error("Error fetching unassigned lecturers for specialization {}: {}",
                    specialization.getSpecializationId(), e.getMessage());
            return List.of();
        }
    }


    @Override
    public List<MajorLecturers_Specializations> getLecturersAssignedToSpecialization(Specialization specialization) {
        if (specialization == null || specialization.getSpecializationId() == null || staffsService.getStaffMajor() == null) {
            logger.warn("Invalid input for getLecturersAssignedToSpecialization: specialization={}, staffMajor={}",
                    specialization, staffsService.getStaffMajor());
            return List.of();
        }

        try {
            Query query = entityManager.createQuery(
                    "SELECT mls FROM MajorLecturers_Specializations mls " +
                            "WHERE mls.specialization.specializationId = :specializationId AND mls.majorLecturer.majorManagement = :major",
                    MajorLecturers_Specializations.class);
            query.setParameter("specializationId", specialization.getSpecializationId());
            query.setParameter("major", staffsService.getStaffMajor());
            List<MajorLecturers_Specializations> result = query.getResultList();
            logger.info("Fetched {} assigned lecturers for specialization {}", result.size(), specialization.getSpecializationId());
            return result;
        } catch (Exception e) {
            logger.error("Error fetching assigned lecturers for specialization {}: {}",
                    specialization.getSpecializationId(), e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public boolean isLecturerAlreadyAssignedToSpecialization(String lecturerId, String specializationId) {
        if (lecturerId == null || specializationId == null) {
            logger.warn("Invalid input for isLecturerAlreadyAssignedToSpecialization: lecturerId={}, specializationId={}",
                    lecturerId, specializationId);
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
            boolean isAssigned = count > 0;
            logger.info("Checked assignment for lecturer {} and specialization {}: {}", lecturerId, specializationId, isAssigned);
            return isAssigned;
        } catch (Exception e) {
            logger.error("Error checking assignment for lecturer {} and specialization {}: {}",
                    lecturerId, specializationId, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void addLecturerSpecialization(MajorLecturers_Specializations assignment) {
        entityManager.persist(assignment);
    }

    @Override
    public boolean removeLecturerSpecialization(String lecturerId, String specializationId) {
        if (lecturerId == null || specializationId == null) {
            logger.warn("Invalid input for removeLecturerSpecialization: lecturerId={}, specializationId={}",
                    lecturerId, specializationId);
            return false;
        }

        try {
            Query query = entityManager.createQuery(
                    "DELETE FROM MajorLecturers_Specializations mls " +
                            "WHERE mls.majorLecturer.id = :lecturerId AND mls.specialization.specializationId = :specializationId");
            query.setParameter("lecturerId", lecturerId);
            query.setParameter("specializationId", specializationId);
            int rowsAffected = query.executeUpdate();
            boolean removed = rowsAffected > 0;
            if (removed) {
                logger.info("Successfully removed lecturer {} from specialization {}", lecturerId, specializationId);
            } else {
                logger.warn("No assignment found for lecturer {} and specialization {}", lecturerId, specializationId);
            }
            return removed;
        } catch (Exception e) {
            logger.error("Error removing lecturer specialization for lecturer {} and specialization {}: {}",
                    lecturerId, specializationId, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<MajorLecturers> getLecturersNotAssignedToSpecialization(Specialization specialization, int firstResult, int maxResults) {
        if (specialization == null || specialization.getSpecializationId() == null || staffsService.getStaffMajor() == null) {
            logger.warn("Invalid input for getLecturersNotAssignedToSpecialization with pagination: specialization={}, staffMajor={}",
                    specialization, staffsService.getStaffMajor());
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
            List<MajorLecturers> result = query.getResultList();
            logger.info("Fetched {} unassigned lecturers for specialization {} with pagination", result.size(), specialization.getSpecializationId());
            return result;
        } catch (Exception e) {
            logger.error("Error fetching unassigned lecturers for specialization {} with pagination: {}",
                    specialization.getSpecializationId(), e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public List<MajorLecturers_Specializations> getLecturersAssignedToSpecialization(Specialization specialization, int firstResult, int maxResults) {
        if (specialization == null || specialization.getSpecializationId() == null || staffsService.getStaffMajor() == null) {
            logger.warn("Invalid input for getLecturersAssignedToSpecialization with pagination: specialization={}, staffMajor={}",
                    specialization, staffsService.getStaffMajor());
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
            List<MajorLecturers_Specializations> result = query.getResultList();
            logger.info("Fetched {} assigned lecturers for specialization {} with pagination", result.size(), specialization.getSpecializationId());
            return result;
        } catch (Exception e) {
            logger.error("Error fetching assigned lecturers for specialization {} with pagination: {}",
                    specialization.getSpecializationId(), e.getMessage(), e);
            return List.of();
        }
    }
}