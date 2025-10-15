package com.example.demo.curriculum.dao;

import com.example.demo.curriculum.model.Curriculum;
import com.example.demo.admin.model.Admins;
import com.example.demo.admin.service.AdminsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
@PreAuthorize("hasRole('ADMIN')")
public class CurriculumDAOImpl implements CurriculumDAO {
    private static final Logger logger = LoggerFactory.getLogger(CurriculumDAOImpl.class);
    private final AdminsService adminsService;

    @PersistenceContext
    private EntityManager entityManager;

    public CurriculumDAOImpl(AdminsService adminsService) {
        this.adminsService = adminsService;
    }

    @Override
    public Curriculum getCurriculumById(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Curriculum ID cannot be null");
        }
        return entityManager.find(Curriculum.class, id);
    }

    @Override
    public Curriculum getByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        List<Curriculum> curriculums = entityManager.createQuery(
                        "SELECT c FROM Curriculum c WHERE c.name = :name", Curriculum.class)
                .setParameter("name", name.trim())
                .getResultList();
        return curriculums.isEmpty() ? null : curriculums.get(0);
    }

    @Override
    public boolean existsByNameExcludingId(String name, String curriculumId) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        List<Curriculum> curriculums = entityManager.createQuery(
                        "SELECT c FROM Curriculum c WHERE c.name = :name AND c.curriculumId != :curriculumId", Curriculum.class)
                .setParameter("name", name.trim())
                .setParameter("curriculumId", curriculumId != null ? curriculumId : "")
                .getResultList();
        return !curriculums.isEmpty();
    }

    @Override
    public void addCurriculum(Curriculum curriculum) {
        Admins admin = adminsService.getAdmin();
        if (admin == null) {
            throw new IllegalArgumentException("Authenticated admin not found");
        }
        if (curriculum.getCurriculumId() == null || curriculum.getCurriculumId().isBlank()) {
            curriculum.setCurriculumId(generateUniqueCurriculumId());
        }
        curriculum.setCreator(admin);
        curriculum.setCreatedAt(LocalDateTime.now());
        entityManager.persist(curriculum);
        logger.info("Added curriculum with ID: {}", curriculum.getCurriculumId());
    }

    @Override
    public void updateCurriculum(Curriculum curriculum) {
        Curriculum existingCurriculum = entityManager.find(Curriculum.class, curriculum.getCurriculumId());
        if (existingCurriculum == null) {
            throw new IllegalArgumentException("Curriculum with ID " + curriculum.getCurriculumId() + " not found");
        }
        existingCurriculum.setName(curriculum.getName());
        existingCurriculum.setDescription(curriculum.getDescription());
        entityManager.merge(existingCurriculum);
        logger.info("Updated curriculum with ID: {}", curriculum.getCurriculumId());
    }

    @Override
    public void deleteCurriculum(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Curriculum ID cannot be null");
        }
        Curriculum curriculum = entityManager.find(Curriculum.class, id);
        if (curriculum != null) {
            entityManager.remove(curriculum);
            logger.info("Deleted curriculum with ID: {}", id);
        }
    }

    @Override
    public List<Curriculum> getCurriculums() {
        return entityManager.createQuery("FROM Curriculum", Curriculum.class).getResultList();
    }

    @Override
    public Map<String, String> validateCurriculum(Curriculum curriculum) {
        Map<String, String> errors = new HashMap<>();

        if (curriculum.getName() == null || curriculum.getName().trim().isEmpty()) {
            errors.put("name", "Curriculum name cannot be blank.");
        } else if (!isValidName(curriculum.getName())) {
            errors.put("name", "Curriculum name is not valid. Only letters, numbers, spaces, and standard punctuation are allowed.");
        }

        if (curriculum.getName() != null && existsByNameExcludingId(curriculum.getName(), curriculum.getCurriculumId())) {
            errors.put("name", "Curriculum name is already in use.");
        }

        if (curriculum.getDescription() != null && curriculum.getDescription().length() > 1000) {
            errors.put("description", "Description cannot exceed 1000 characters.");
        }

        return errors;
    }

    @Override
    public String generateUniqueCurriculumId() {
        SecureRandom random = new SecureRandom();
        LocalDate currentDate = LocalDate.now();
        String datePart = currentDate.format(DateTimeFormatter.ofPattern("yMMdd"));
        String prefix = "CURR";

        String curriculumId;
        do {
            String randomDigits = String.format("%04d", random.nextInt(10000));
            curriculumId = prefix + datePart + randomDigits;
        } while (entityManager.find(Curriculum.class, curriculumId) != null);
        return curriculumId;
    }

    private boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        String nameRegex = "^[\\p{L}0-9][\\p{L}0-9 .'-]{0,49}$";
        return name.matches(nameRegex);
    }
}