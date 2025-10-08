package com.example.demo.Specialization.dao;

import com.example.demo.Specialization.model.Specialization;
import com.example.demo.admin.service.AdminsService;
import com.example.demo.major.model.Majors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class SpecializationDAOImpl implements SpecializationDAO {

    @PersistenceContext
    private EntityManager entityManager;

    private final AdminsService adminsService;

    public SpecializationDAOImpl(AdminsService adminsService) {
        this.adminsService = adminsService;
    }

    @Override
    public List<Specialization> searchSpecializations(String searchType, String keyword, int firstResult, int pageSize, Majors major) {
        if (keyword == null || keyword.trim().isEmpty() || pageSize <= 0 || major == null) {
            return List.of();
        }

        String queryString = "SELECT s FROM Specialization s JOIN FETCH s.major JOIN FETCH s.creator WHERE s.major = :major";

        if ("name".equals(searchType)) {
            keyword = keyword.toLowerCase().trim();
            String[] words = keyword.split("\\s+");
            StringBuilder nameCondition = new StringBuilder();
            for (int i = 0; i < words.length; i++) {
                if (i > 0) {
                    nameCondition.append(" AND ");
                }
                nameCondition.append("LOWER(s.specializationName) LIKE :word").append(i);
            }
            queryString += " AND (" + nameCondition.toString() + ")";
        } else if ("id".equals(searchType)) {
            queryString += " AND LOWER(s.specializationId) LIKE LOWER(:keyword)";
        } else {
            return List.of();
        }

        try {
            TypedQuery<Specialization> query = entityManager.createQuery(queryString, Specialization.class)
                    .setParameter("major", major)
                    .setFirstResult(firstResult)
                    .setMaxResults(pageSize);

            if ("name".equals(searchType)) {
                String[] words = keyword.split("\\s+");
                for (int i = 0; i < words.length; i++) {
                    query.setParameter("word" + i, "%" + words[i] + "%");
                }
            } else if ("id".equals(searchType)) {
                query.setParameter("keyword", "%" + keyword.trim() + "%");
            }

            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error searching specializations: " + e.getMessage(), e);
        }
    }
    @Override
    public List<Specialization> specializationByMajor(Majors major) {
        if (major == null) {
            return List.of();
        }
        try {
            return entityManager.createQuery(
                            "SELECT s FROM Specialization s WHERE s.major = :major ORDER BY s.specializationName ASC",
                            Specialization.class)
                    .setParameter("major", major)
                    .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving specializations by major: " + e.getMessage(), e);
        }
    }

    @Override
    public long countSearchResults(String searchType, String keyword, Majors major) {
        if (keyword == null || keyword.trim().isEmpty() || major == null) {
            return 0L;
        }

        String queryString = "SELECT COUNT(s) FROM Specialization s WHERE s.major = :major";

        if ("name".equals(searchType)) {
            keyword = keyword.toLowerCase().trim();
            String[] words = keyword.split("\\s+");
            StringBuilder nameCondition = new StringBuilder();
            for (int i = 0; i < words.length; i++) {
                if (i > 0) {
                    nameCondition.append(" AND ");
                }
                nameCondition.append("LOWER(s.specializationName) LIKE :word").append(i);
            }
            queryString += " AND (" + nameCondition.toString() + ")";
        } else if ("id".equals(searchType)) {
            queryString += " AND LOWER(s.specializationId) LIKE LOWER(:keyword)";
        } else {
            return 0L;
        }

        try {
            TypedQuery<Long> query = entityManager.createQuery(queryString, Long.class)
                    .setParameter("major", major);

            if ("name".equals(searchType)) {
                String[] words = keyword.split("\\s+");
                for (int i = 0; i < words.length; i++) {
                    query.setParameter("word" + i, "%" + words[i] + "%");
                }
            } else if ("id".equals(searchType)) {
                query.setParameter("keyword", "%" + keyword.trim() + "%");
            }

            return query.getSingleResult();
        } catch (Exception e) {
            throw new RuntimeException("Error counting search results: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsByNameExcludingId(String name, String id) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        try {
            List<Specialization> specializations = entityManager.createQuery(
                            "SELECT s FROM Specialization s WHERE s.specializationName = :name AND s.specializationId != :id", Specialization.class)
                    .setParameter("name", name.trim())
                    .setParameter("id", id != null ? id : "")
                    .getResultList();
            return !specializations.isEmpty();
        } catch (Exception e) {
            throw new RuntimeException("Error checking specialization existence by name: " + e.getMessage(), e);
        }
    }

    @Override
    public void addSpecialization(Specialization specialization) {
        try {
            specialization.setCreator(adminsService.getAdmin());
            entityManager.persist(specialization);
        } catch (Exception e) {
            throw new RuntimeException("Error adding specialization: " + e.getMessage(), e);
        }
    }

    @Override
    public Specialization getSpecializationById(String id) {
        try {
            Specialization specialization = entityManager.find(Specialization.class, id);
            return specialization;
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving specialization by ID " + id + ": " + e.getMessage(), e);
        }
    }

    @Override
    public Specialization getSpecializationByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        try {
            List<Specialization> specializations = entityManager.createQuery(
                            "SELECT s FROM Specialization s WHERE s.specializationName = :name", Specialization.class)
                    .setParameter("name", name.trim())
                    .getResultList();
            return specializations.isEmpty() ? null : specializations.get(0);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving specialization by name " + name + ": " + e.getMessage(), e);
        }
    }

    @Override
    public List<Specialization> specializationsByMajor(Majors major) {
        if (major == null) {
            return List.of();
        }
        try {
            return entityManager.createQuery(
                            "SELECT s FROM Specialization s WHERE s.major = :major ORDER BY s.createdAt ASC",
                            Specialization.class)
                    .setParameter("major", major)
                    .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving specializations by major: " + e.getMessage(), e);
        }
    }

    @Override
    public Specialization editSpecialization(String id, Specialization specialization) {
        try {
            Specialization existing = entityManager.find(Specialization.class, id);
            if (existing == null) {
                throw new IllegalArgumentException("Specialization with ID " + id + " not found");
            }
            if (specialization.getSpecializationName() != null) {
                existing.setSpecializationName(specialization.getSpecializationName());
            }
            return entityManager.merge(existing);
        } catch (Exception e) {
            throw new RuntimeException("Error editing specialization with ID " + id + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteSpecialization(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Specialization ID cannot be null");
        }
        try {
            Specialization specialization = entityManager.find(Specialization.class, id);
            if (specialization != null) {
                entityManager.remove(specialization);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error deleting specialization with ID " + id + ": " + e.getMessage(), e);
        }
    }

    @Override
    public String generateUniqueId(String majorId, LocalDate createdDate) {
        String prefix = majorId != null ? majorId : "SPECGEN";
        String year = String.format("%02d", createdDate.getYear() % 100);
        String date = String.format("%02d%02d", createdDate.getMonthValue(), createdDate.getDayOfMonth());
        String id;
        SecureRandom random = new SecureRandom();
        do {
            String randomDigit = String.valueOf(random.nextInt(10));
            id = prefix + year + date + randomDigit;
        } while (getSpecializationById(id) != null);
        return id;
    }

    @Override
    public Map<String, String> specializationValidation(Specialization specialization) { // Renamed from validate
        Map<String, String> errors = new HashMap<>();

        if (specialization.getSpecializationName() == null || specialization.getSpecializationName().trim().isEmpty()) {
            errors.put("specializationName", "Specialization name cannot be blank.");
        } else if (!isValidName(specialization.getSpecializationName())) {
            errors.put("specializationName", "Specialization name is not valid. Only letters, numbers, spaces, and standard punctuation are allowed.");
        }

        if (specialization.getSpecializationName() != null &&
                existsByNameExcludingId(specialization.getSpecializationName(), specialization.getSpecializationId())) {
            errors.put("specializationName", "Specialization name is already in use.");
        }

        return errors;
    }

    @Override
    public List<Specialization> getPaginated(int firstResult, int pageSize, Majors major) {
        if (major == null || pageSize <= 0 || firstResult < 0) {
            return List.of();
        }
        try {
            return entityManager.createQuery(
                            "SELECT s FROM Specialization s WHERE s.major = :major ORDER BY s.createdAt ASC",
                            Specialization.class)
                    .setParameter("major", major)
                    .setFirstResult(firstResult)
                    .setMaxResults(pageSize)
                    .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving paginated specializations: " + e.getMessage(), e);
        }
    }

    @Override
    public long numberOfSpecializations(Majors major) {
        if (major == null) {
            return 0;
        }
        try {
            return entityManager.createQuery(
                            "SELECT COUNT(s) FROM Specialization s WHERE s.major = :major",
                            Long.class)
                    .setParameter("major", major)
                    .getSingleResult();
        } catch (Exception e) {
            throw new RuntimeException("Error counting specializations: " + e.getMessage(), e);
        }
    }

    private boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        String nameRegex = "^[\\p{L}0-9][\\p{L}0-9 .'-]{0,49}$";
        return name.matches(nameRegex);
    }
}