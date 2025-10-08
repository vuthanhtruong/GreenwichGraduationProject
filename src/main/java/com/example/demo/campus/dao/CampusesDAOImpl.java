package com.example.demo.campus.dao;

import com.example.demo.admin.service.AdminsService;
import com.example.demo.campus.model.Campuses;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class CampusesDAOImpl implements CampusesDAO {
    private final AdminsService  adminsService;

    public CampusesDAOImpl(AdminsService adminsService) {
        this.adminsService = adminsService;
    }

    @Override
    public List<Campuses> listOfExceptionFieldsCampus() {
        return entityManager.createQuery("from Campuses c where c.id!=:campus", Campuses.class).setParameter("campus", adminsService.getAdminCampus().getCampusId()).getResultList();
    }

    @Override
    public List<Campuses> existsCampusByName(String campusName) {
        return entityManager.createQuery("from Campuses c where c.campusName=:campusname", Campuses.class).setParameter("campusname", campusName).getResultList();
    }

    @Override
    public List<String> validateCampus(Campuses campus, MultipartFile avatarFile) {
        List<String> errors = new ArrayList<>();
        if (campus.getCampusName() == null || campus.getCampusName().trim().isEmpty()) {
            errors.add("Campus Name is required.");
        } else if (!campus.getCampusName().matches("^[\\p{L}][\\p{L} .'-]{1,49}$")) {
            errors.add("Campus Name is not valid. Only letters, spaces, and standard punctuation are allowed.");
        }
        if (campus.getOpeningDay() != null && campus.getOpeningDay().isAfter(LocalDate.now())) {
            errors.add("Opening Day must be in the past.");
        }
        if (avatarFile != null && !avatarFile.isEmpty()) {
            String contentType = avatarFile.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                errors.add("Avatar must be an image file.");
            }
            if (avatarFile.getSize() > 5 * 1024 * 1024) {
                errors.add("Avatar file size must not exceed 5MB.");
            }
        }
        return errors;
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Campuses getCampusById(String campusId) {
        return entityManager.find(Campuses.class, campusId);
    }

    @Override
    public List<Campuses> getCampuses() {
        return entityManager.createQuery("FROM Campuses", Campuses.class).getResultList();
    }

    @Override
    public void addCampus(Campuses campus) {
        try {
            entityManager.persist(campus);
        } catch (Exception e) {
            throw new RuntimeException("Error adding campus: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsCampusById(String campusId) {
        return entityManager.find(Campuses.class, campusId) != null;
    }

    @Override
    public void deleteCampus(String campusId) {
        try {
            Campuses campus = entityManager.find(Campuses.class, campusId);
            if (campus == null) {
                throw new IllegalArgumentException("Campus with ID " + campusId + " not found");
            }
            entityManager.remove(campus);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting campus with ID " + campusId + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void editCampus(Campuses campus) {
        try {
            Campuses existingCampus = entityManager.find(Campuses.class, campus.getCampusId());
            if (existingCampus == null) {
                throw new IllegalArgumentException("Campus with ID " + campus.getCampusId() + " not found");
            }
            updateCampusFields(existingCampus, campus);
            entityManager.merge(existingCampus);
        } catch (Exception e) {
            throw new RuntimeException("Error updating campus: " + e.getMessage(), e);
        }
    }

    @Override
    public List<String> validateCampus(Campuses campus) {
        List<String> errors = new ArrayList<>();
        if (campus.getCampusName() == null || campus.getCampusName().trim().isEmpty()) {
            errors.add("Campus Name is required.");
        } else if (!campus.getCampusName().matches("^[\\p{L}][\\p{L} .'-]{1,49}$")) {
            errors.add("Campus Name is not valid. Only letters, spaces, and standard punctuation are allowed.");
        }
        if (campus.getOpeningDay() != null && campus.getOpeningDay().isAfter(LocalDate.now())) {
            errors.add("Opening Day must be in the past.");
        }
        return errors;
    }

    @Override
    public String generateUniqueCampusId(LocalDate createdDate) {
        String prefix = "CMP";
        String year = String.format("%02d", createdDate.getYear() % 100);
        String date = String.format("%02d%02d", createdDate.getMonthValue(), createdDate.getDayOfMonth());
        String campusId;
        SecureRandom random = new SecureRandom();
        do {
            String randomDigit = String.valueOf(random.nextInt(10));
            campusId = prefix + year + date + randomDigit;
        } while (existsCampusById(campusId));
        return campusId;
    }

    @Override
    public void updateCampusFields(Campuses existing, Campuses updated) {
        if (updated.getCampusName() != null) {
            existing.setCampusName(updated.getCampusName());
        }
        if (updated.getOpeningDay() != null) {
            existing.setOpeningDay(updated.getOpeningDay());
        }
        if (updated.getDescription() != null) {
            existing.setDescription(updated.getDescription());
        }
        if (updated.getCreator() != null) {
            existing.setCreator(updated.getCreator());
        }
        if (updated.getAvatar() != null) {
            existing.setAvatar(updated.getAvatar());
        }
    }

    @Override
    public Map<String, Map<String, Long>> getCampusCounts() {
        Map<String, Map<String, Long>> campusCounts = new HashMap<>();

        // Get all campuses
        List<Campuses> campuses = getCampuses();

        for (Campuses campus : campuses) {
            String campusId = campus.getCampusId();
            Map<String, Long> counts = new HashMap<>();

            // Count students
            Long studentCount = entityManager.createQuery(
                            "SELECT COUNT(s) FROM Students s WHERE s.campus.campusId = :campusId", Long.class)
                    .setParameter("campusId", campusId)
                    .getSingleResult();
            counts.put("studentCount", studentCount);

            // Count major lecturers
            Long majorLecturerCount = entityManager.createQuery(
                            "SELECT COUNT(ml) FROM MajorLecturers ml WHERE ml.campus.campusId = :campusId", Long.class)
                    .setParameter("campusId", campusId)
                    .getSingleResult();
            counts.put("majorLecturerCount", majorLecturerCount);

            // Count minor lecturers
            Long minorLecturerCount = entityManager.createQuery(
                            "SELECT COUNT(ml) FROM MinorLecturers ml WHERE ml.campus.campusId = :campusId", Long.class)
                    .setParameter("campusId", campusId)
                    .getSingleResult();
            counts.put("minorLecturerCount", minorLecturerCount);

            // Count major staff
            Long majorStaffCount = entityManager.createQuery(
                            "SELECT COUNT(s) FROM Staffs s WHERE s.campus.campusId = :campusId", Long.class)
                    .setParameter("campusId", campusId)
                    .getSingleResult();
            counts.put("majorStaffCount", majorStaffCount);

            // Count deputy staff
            Long deputyStaffCount = entityManager.createQuery(
                            "SELECT COUNT(ds) FROM DeputyStaffs ds WHERE ds.campus.campusId = :campusId", Long.class)
                    .setParameter("campusId", campusId)
                    .getSingleResult();
            counts.put("deputyStaffCount", deputyStaffCount);

            campusCounts.put(campusId, counts);
        }

        return campusCounts;
    }
}