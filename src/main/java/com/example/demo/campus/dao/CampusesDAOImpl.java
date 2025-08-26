package com.example.demo.campus.dao;

import com.example.demo.campus.model.Campuses;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class CampusesDAOImpl implements CampusesDAO {
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
    }
}