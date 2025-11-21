package com.example.demo.major.dao;

import com.example.demo.major.model.Majors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class MajorsDAOImpl implements MajorsDAO {
    private static final Logger logger = LoggerFactory.getLogger(MajorsDAOImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Majors> existsMajorByName(String majorName) {
        return entityManager.createQuery("from Majors m where m.majorName = :majorName", Majors.class)
                .setParameter("majorName", majorName)
                .getResultList();
    }

    @Override
    public Map<String, String> validateMajor(Majors major, MultipartFile avatarFile) {
        Map<String, String> errors = new HashMap<>();
        if (major.getMajorName() == null || major.getMajorName().trim().isEmpty()) {
            errors.put("majorName", "Major Name is required.");
        } else if (!major.getMajorName().matches("^[\\p{L}][\\p{L} .'-]{1,49}$")) {
            errors.put("majorName", "Major Name is not valid. Only letters, spaces, and standard punctuation are allowed.");
        }
        if (avatarFile != null && !avatarFile.isEmpty()) {
            String contentType = avatarFile.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                errors.put("avatarFile", "Avatar must be an image file.");
            }
            if (avatarFile.getSize() > 5 * 1024 * 1024) {
                errors.put("avatarFile", "Avatar file size must not exceed 5MB.");
            }
        }
        return errors;
    }

    @Override
    public Map<String, String> validateMajor(Majors major) {
        Map<String, String> errors = new HashMap<>();
        if (major.getMajorName() == null || major.getMajorName().trim().isEmpty()) {
            errors.put("majorName", "Major Name is required.");
        } else if (!major.getMajorName().matches("^[\\p{L}][\\p{L} .'-]{1,49}$")) {
            errors.put("majorName", "Major Name is not valid. Only letters, spaces, and standard punctuation are allowed.");
        }
        return errors;
    }

    @Override
    public Majors getMajorById(String majorId) {
        try {
            return entityManager.find(Majors.class, majorId);
        } catch (Exception e) {
            logger.error("Error retrieving major by ID {}: {}", majorId, e.getMessage());
            throw new RuntimeException("Error retrieving major: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Majors> getMajors() {
        try {
            return entityManager.createQuery("FROM Majors", Majors.class).getResultList();
        } catch (Exception e) {
            logger.error("Error retrieving majors: {}", e.getMessage());
            throw new RuntimeException("Error retrieving majors: " + e.getMessage(), e);
        }
    }

    @Override
    public void addMajor(Majors major) {
        try {
            entityManager.persist(major);
            logger.info("Added major with ID: {}", major.getMajorId());
        } catch (Exception e) {
            logger.error("Error adding major: {}", e.getMessage());
            throw new RuntimeException("Error adding major: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsMajorById(String majorId) {
        return entityManager.find(Majors.class, majorId) != null;
    }

    @Override
    @Transactional
    public void deleteMajor(String majorId) {
        if (majorId == null || majorId.trim().isEmpty()) {
            throw new IllegalArgumentException("Major ID cannot be null or empty");
        }

        try {
            // 1. XÓA FinancialHistories của các sinh viên thuộc specialization của major này
            int deletedFinances = entityManager.createQuery(
                            """
                            DELETE FROM FinancialHistories fh
                            WHERE fh.student IN (
                                SELECT s FROM Students s 
                                WHERE s.specialization IN (
                                    SELECT spec FROM Specialization spec 
                                    WHERE spec.major.majorId = :majorId
                                )
                            )
                            """)
                    .setParameter("majorId", majorId)
                    .executeUpdate();

            // 2. XÓA các Students thuộc specialization của major này
            int deletedStudents = entityManager.createQuery(
                            """
                            DELETE FROM Students s 
                            WHERE s.specialization IN (
                                SELECT spec FROM Specialization spec 
                                WHERE spec.major.majorId = :majorId
                            )
                            """)
                    .setParameter("majorId", majorId)
                    .executeUpdate();

            // 3. XÓA Specialization thuộc major này
            int deletedSpecs = entityManager.createQuery(
                            "DELETE FROM Specialization s WHERE s.major.majorId = :majorId")
                    .setParameter("majorId", majorId)
                    .executeUpdate();

            // 4. Cuối cùng mới xóa Major
            int deletedMajors = entityManager.createQuery(
                            "DELETE FROM Majors m WHERE m.majorId = :majorId")
                    .setParameter("majorId", majorId)
                    .executeUpdate();

            if (deletedMajors == 0) {
                throw new IllegalArgumentException("Major with ID " + majorId + " not found");
            }

            logger.info(
                    "Successfully deleted Major ID: {} | " +
                            "Specializations: {} | Students: {} | FinancialHistories: {}",
                    majorId, deletedSpecs, deletedStudents, deletedFinances
            );

        } catch (Exception e) {
            logger.error("Failed to delete Major ID {}: {}", majorId, e.getMessage(), e);
            throw new RuntimeException("Cannot delete Major because it is being used by students/financial records", e);
        }
    }

    @Override
    public void editMajor(Majors major, MultipartFile avatarFile) throws IOException {
        try {
            Majors existingMajor = entityManager.find(Majors.class, major.getMajorId());
            if (existingMajor == null) {
                throw new IllegalArgumentException("Major with ID " + major.getMajorId() + " not found");
            }
            if (avatarFile != null && !avatarFile.isEmpty()) {
                major.setAvatar(avatarFile.getBytes());
            } else {
                major.setAvatar(existingMajor.getAvatar());
            }
            updateMajorFields(existingMajor, major);
            entityManager.merge(existingMajor);
            logger.info("Updated major with ID: {}", major.getMajorId());
        } catch (IOException e) {
            logger.error("IO error updating major: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error updating major: {}", e.getMessage());
            throw new RuntimeException("Error updating major: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateUniqueMajorId(LocalDate createdDate) {
        String prefix = "MAJ";
        String year = String.format("%02d", createdDate.getYear() % 100);
        String date = String.format("%02d%02d", createdDate.getMonthValue(), createdDate.getDayOfMonth());
        String majorId;
        SecureRandom random = new SecureRandom();
        do {
            String randomDigit = String.valueOf(random.nextInt(10));
            majorId = prefix + year + date + randomDigit;
        } while (existsMajorById(majorId));
        return majorId;
    }

    @Override
    public void updateMajorFields(Majors existing, Majors updated) {
        if (updated.getMajorName() != null) {
            existing.setMajorName(updated.getMajorName());
        }
        if (updated.getCreator() != null) {
            existing.setCreator(updated.getCreator());
        }
        if (updated.getAvatar() != null) {
            existing.setAvatar(updated.getAvatar());
        }
    }
}