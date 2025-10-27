package com.example.demo.subject.minorSubject.dao;

import com.example.demo.subject.minorSubject.model.MinorSubjects;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import com.example.demo.user.deputyStaff.service.DeputyStaffsService;
import com.example.demo.subject.abstractSubject.service.SubjectsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class MinorSubjectsDAOImpl implements MinorSubjectsDAO {
    @Override
    public List<MinorSubjects> getAllSubjects() {
        return entityManager.createQuery("select m from MinorSubjects m", MinorSubjects.class).getResultList();
    }

    private static final Logger logger = LoggerFactory.getLogger(MinorSubjectsDAOImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    private final DeputyStaffsService deputyStaffsService;
    private final SubjectsService subjectsService;

    public MinorSubjectsDAOImpl(DeputyStaffsService deputyStaffsService, SubjectsService subjectsService) {
        this.deputyStaffsService = deputyStaffsService;
        this.subjectsService = subjectsService;
    }

    @Override
    public List<MinorSubjects> getSubjectsByCreator(DeputyStaffs creator) {
        if (creator == null) {
            logger.warn("Invalid creator for getSubjectsByCreator");
            return List.of();
        }
        try {
            return entityManager.createQuery(
                            "SELECT s FROM MinorSubjects s WHERE s.creator = :creator ORDER BY s.semester ASC",
                            MinorSubjects.class)
                    .setParameter("creator", creator)
                    .getResultList();
        } catch (Exception e) {
            logger.error("Error retrieving subjects by creator {}: {}", creator.getId(), e.getMessage());
            throw new RuntimeException("Error retrieving subjects by creator: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsBySubjectExcludingName(String subjectName, String subjectId) {
        if (subjectName == null || subjectName.trim().isEmpty()) {
            logger.warn("Invalid subjectName for existence check: {}", subjectName);
            return false;
        }
        String queryString = "SELECT s FROM MinorSubjects s WHERE s.subjectName = :name AND s.subjectId != :subjectId";
        try {
            List<MinorSubjects> subjects = entityManager.createQuery(queryString, MinorSubjects.class)
                    .setParameter("name", subjectName.trim())
                    .setParameter("subjectId", subjectId != null ? subjectId : "")
                    .getResultList();
            return !subjects.isEmpty();
        } catch (Exception e) {
            logger.error("Error checking subject existence: {}", e.getMessage());
            throw new RuntimeException("Error checking subject existence: " + e.getMessage(), e);
        }
    }

    @Override
    public void addSubject(MinorSubjects subject) {
        try {
            if (subject.getCreator() == null) {
                subject.setCreator(deputyStaffsService.getDeputyStaff());
            }
            entityManager.persist(subject);
        } catch (Exception e) {
            logger.error("Error adding subject: {}", e.getMessage());
            throw new RuntimeException("Error adding subject: " + e.getMessage(), e);
        }
    }

    @Override
    public MinorSubjects getSubjectById(String subjectId) {
        if (subjectId == null || subjectId.trim().isEmpty()) {
            logger.warn("Invalid subject ID: {}", subjectId);
            throw new IllegalArgumentException("Subject ID cannot be null or empty");
        }
        try {
            MinorSubjects subject = entityManager.find(MinorSubjects.class, subjectId);
            return subject;
        } catch (Exception e) {
            logger.error("Error retrieving subject by ID {}: {}", subjectId, e.getMessage());
            throw new RuntimeException("Error retrieving subject by ID: " + e.getMessage(), e);
        }
    }

    @Override
    public MinorSubjects getSubjectByName(String subjectName) {
        if (subjectName == null || subjectName.trim().isEmpty()) {
            logger.warn("Invalid subject name: {}", subjectName);
            return null;
        }
        try {
            List<MinorSubjects> subjects = entityManager.createQuery(
                            "SELECT s FROM MinorSubjects s WHERE s.subjectName = :name", MinorSubjects.class)
                    .setParameter("name", subjectName.trim())
                    .getResultList();
            return subjects.isEmpty() ? null : subjects.get(0);
        } catch (Exception e) {
            logger.error("Error retrieving subject by name {}: {}", subjectName, e.getMessage());
            throw new RuntimeException("Error retrieving subject by name: " + e.getMessage(), e);
        }
    }

    @Override
    public MinorSubjects checkNameSubject(MinorSubjects subject) {
        try {
            return getSubjectByName(subject != null ? subject.getSubjectName() : null);
        } catch (Exception e) {
            logger.error("Error checking subject name: {}", e.getMessage());
            throw new RuntimeException("Error checking subject name: " + e.getMessage(), e);
        }
    }

    @Override
    public List<MinorSubjects> getPaginatedSubjects(int firstResult, int pageSize) {
        if (pageSize <= 0 || firstResult < 0) {
            logger.warn("Invalid pagination parameters: pageSize={}, firstResult={}", pageSize, firstResult);
            return List.of();
        }
        try {
            return entityManager.createQuery(
                            "SELECT s FROM MinorSubjects s ORDER BY s.semester ASC",
                            MinorSubjects.class)
                    .setFirstResult(firstResult)
                    .setMaxResults(pageSize)
                    .getResultList();
        } catch (Exception e) {
            logger.error("Error retrieving paginated subjects: {}", e.getMessage());
            throw new RuntimeException("Error retrieving paginated subjects: " + e.getMessage(), e);
        }
    }

    @Override
    public long numberOfSubjects() {
        try {
            return entityManager.createQuery(
                            "SELECT COUNT(s) FROM MinorSubjects s",
                            Long.class)
                    .getSingleResult();
        } catch (Exception e) {
            logger.error("Error counting subjects: {}", e.getMessage());
            throw new RuntimeException("Error counting subjects: " + e.getMessage(), e);
        }
    }

    @Override
    public List<MinorSubjects> searchSubjects(String searchType, String keyword, int firstResult, int pageSize) {
        if (keyword == null || keyword.trim().isEmpty() || pageSize <= 0) {
            logger.warn("Invalid search parameters: searchType={}, keyword={}, pageSize={}",
                    searchType, keyword, pageSize);
            return List.of();
        }

        String queryString = "SELECT s FROM MinorSubjects s";

        if ("name".equalsIgnoreCase(searchType)) {
            keyword = keyword.toLowerCase().trim();
            String[] words = keyword.split("\\s+");
            StringBuilder nameCondition = new StringBuilder();
            for (int i = 0; i < words.length; i++) {
                if (i > 0) {
                    nameCondition.append(" AND ");
                }
                nameCondition.append("(LOWER(s.subjectName) LIKE :word").append(i).append(")");
            }
            queryString += " WHERE (" + nameCondition.toString() + ")";
        } else if ("id".equalsIgnoreCase(searchType)) {
            queryString += " WHERE LOWER() = LOWER(:keyword)";
        } else {
            logger.warn("Invalid searchType: {}", searchType);
            return List.of();
        }

        try {
            TypedQuery<MinorSubjects> query = entityManager.createQuery(queryString, MinorSubjects.class)
                    .setFirstResult(firstResult)
                    .setMaxResults(pageSize);

            if ("name".equalsIgnoreCase(searchType)) {
                String[] words = keyword.split("\\s+");
                for (int i = 0; i < words.length; i++) {
                    query.setParameter("word" + i, "%" + words[i] + "%");
                }
            } else if ("id".equalsIgnoreCase(searchType)) {
                query.setParameter("keyword", keyword.trim());
            }

            return query.getResultList();
        } catch (Exception e) {
            logger.error("Error searching subjects: {}", e.getMessage());
            throw new RuntimeException("Error searching subjects: " + e.getMessage(), e);
        }
    }

    @Override
    public long countSearchResults(String searchType, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            logger.warn("Invalid count search parameters: searchType={}, keyword={}",
                    searchType, keyword);
            return 0L;
        }

        String queryString = "SELECT COUNT(s) FROM MinorSubjects s";

        if ("name".equalsIgnoreCase(searchType)) {
            keyword = keyword.toLowerCase().trim();
            String[] words = keyword.split("\\s+");
            StringBuilder nameCondition = new StringBuilder();
            for (int i = 0; i < words.length; i++) {
                if (i > 0) {
                    nameCondition.append(" AND ");
                }
                nameCondition.append("(LOWER(s.subjectName) LIKE :word").append(i).append(")");
            }
            queryString += " WHERE (" + nameCondition.toString() + ")";
        } else if ("id".equalsIgnoreCase(searchType)) {
            queryString += " WHERE LOWER() = LOWER(:keyword)";
        } else {
            logger.warn("Invalid searchType: {}", searchType);
            return 0L;
        }

        try {
            TypedQuery<Long> query = entityManager.createQuery(queryString, Long.class);

            if ("name".equalsIgnoreCase(searchType)) {
                String[] words = keyword.split("\\s+");
                for (int i = 0; i < words.length; i++) {
                    query.setParameter("word" + i, "%" + words[i] + "%");
                }
            } else if ("id".equalsIgnoreCase(searchType)) {
                query.setParameter("keyword", keyword.trim());
            }

            return query.getSingleResult();
        } catch (Exception e) {
            logger.error("Error counting search results: {}", e.getMessage());
            throw new RuntimeException("Error counting search results: " + e.getMessage(), e);
        }
    }

    @Override
    public MinorSubjects editSubject(String id, MinorSubjects subject) {
        try {
            MinorSubjects existingSubject = entityManager.find(MinorSubjects.class, id);
            if (existingSubject == null) {
                logger.warn("Subject not found for ID: {}", id);
                throw new IllegalArgumentException("Subject with ID " + id + " not found");
            }
            if (subject.getSubjectName() != null) {
                existingSubject.setSubjectName(subject.getSubjectName().toUpperCase());
            }
            if (subject.getSemester() != null) {
                existingSubject.setSemester(subject.getSemester());
            }
            return entityManager.merge(existingSubject);
        } catch (Exception e) {
            logger.error("Error editing subject with ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error editing subject: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteSubject(String id) {
        try {
            MinorSubjects subject = entityManager.find(MinorSubjects.class, id);
            if (subject == null) {
                logger.warn("Subject not found for ID: {}", id);
                throw new IllegalArgumentException("Subject with ID " + id + " not found");
            }
            entityManager.remove(subject);
        } catch (Exception e) {
            logger.error("Error deleting subject with ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error deleting subject: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateUniqueSubjectId(String creatorId, LocalDate createdDate) {
        String prefix = creatorId != null ? creatorId : "SUBGEN";
        String year = String.format("%02d", createdDate.getYear() % 100);
        String date = String.format("%02d%02d", createdDate.getMonthValue(), createdDate.getDayOfMonth());
        String subjectId;
        SecureRandom random = new SecureRandom();
        do {
            String randomDigit = String.valueOf(random.nextInt(10));
            subjectId = prefix + year + date + randomDigit;
        } while (getSubjectById(subjectId) != null);
        return subjectId;
    }

    @Override
    public boolean isDuplicateSubjectName(String subjectName, String subjectId) {
        if (subjectName == null || subjectName.trim().isEmpty()) {
            return false;
        }
        MinorSubjects existingSubject = subjectId != null ? getSubjectById(subjectId) : null;
        if (existingSubject == null || !existingSubject.getSubjectName().equalsIgnoreCase(subjectName.trim())) {
            return subjectsService.existsBySubjectNameExcludingId(subjectName.trim(), subjectId != null ? subjectId : "");
        }
        return false;
    }

    @Override
    public Map<String, String> validateSubject(MinorSubjects subject) {
        Map<String, String> errors = new HashMap<>();

        if (subject == null) {
            errors.put("general", "Subject cannot be null");
            return errors;
        }

        // Validate subject name
        if (subject.getSubjectName() == null || subject.getSubjectName().trim().isEmpty()) {
            errors.put("subjectName", "Subject name is required");
        } else if (!isValidName(subject.getSubjectName())) {
            errors.put("subjectName", "Subject name must contain letters, numbers, spaces, or standard punctuation (2-50 characters)");
        } else if (isDuplicateSubjectName(subject.getSubjectName(), subject.getSubjectId())) {
            errors.put("subjectName", "Subject name already exists");
        }

        // Validate semester
        if (subject.getSemester() == null || subject.getSemester() < 1) {
            errors.put("semester", "Semester must be a positive number");
        }

        // Validate deputy staff
        if (deputyStaffsService.getDeputyStaff() == null) {
            errors.put("general", "Authenticated deputy staff not found");
        }

        return errors;
    }

    private boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        String nameRegex = "^[\\p{L}0-9][\\p{L}0-9 .'-]{1,49}$";
        return name.matches(nameRegex);
    }
}