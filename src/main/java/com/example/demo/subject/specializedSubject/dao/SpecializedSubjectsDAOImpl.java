package com.example.demo.subject.specializedSubject.dao;

import com.example.demo.curriculum.model.Curriculum;
import com.example.demo.specialization.model.Specialization;
import com.example.demo.major.model.Majors;
import com.example.demo.subject.specializedSubject.model.SpecializedSubject;
import com.example.demo.subject.abstractSubject.model.Subjects;
import com.example.demo.user.staff.service.StaffsService;
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
public class SpecializedSubjectsDAOImpl implements SpecializedSubjectsDAO {
    @Override
    public List<SpecializedSubject> getSubjectsByCurriculumId(String curriculumId) {
        if (curriculumId == null || curriculumId.trim().isEmpty()) {
            return entityManager.createQuery(
                            "SELECT s FROM SpecializedSubject s WHERE s.specialization.major = :major ORDER BY s.semester ASC",
                            SpecializedSubject.class)
                    .setParameter("major", staffsService.getStaffMajor())
                    .getResultList();
        }

        return entityManager.createQuery(
                        "SELECT s FROM SpecializedSubject s WHERE s.curriculum.curriculumId = :curriculumId AND s.specialization.major = :major ORDER BY s.semester ASC",
                        SpecializedSubject.class)
                .setParameter("curriculumId", curriculumId)
                .setParameter("major", staffsService.getStaffMajor())
                .getResultList();
    }

    @Override
    public List<SpecializedSubject> getSpecializedSubjectsByMajorAndCurriculum(Majors majors, Curriculum curriculum) {
        return entityManager.createQuery("from SpecializedSubject s where s.specialization.major=:majors and s.curriculum=:curriculum", SpecializedSubject.class)
                .setParameter("majors",staffsService.getStaffMajor()).
        setParameter("curriculum",curriculum).getResultList();
    }

    @Override
    public List<SpecializedSubject> subjectsByMajor(Majors majors) {
        return entityManager.createQuery("from SpecializedSubject s where s.specialization.major=:majors",SpecializedSubject.class).
                setParameter("majors", majors).getResultList();
    }

    private static final Logger logger = LoggerFactory.getLogger(SpecializedSubjectsDAOImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    private final StaffsService staffsService;
    private final SubjectsService subjectsService;

    public SpecializedSubjectsDAOImpl(StaffsService staffsService, SubjectsService subjectsService) {
        this.staffsService = staffsService;
        this.subjectsService = subjectsService;
    }

    @Override
    public List<SpecializedSubject> searchSubjects(String searchType, String keyword, int firstResult, int pageSize, Specialization specialization) {
        if (keyword == null || keyword.trim().isEmpty() || pageSize <= 0 || specialization == null) {
            logger.warn("Invalid parameters for searchSubjects: searchType={}, keyword={}, pageSize={}, specializationId={}",
                    searchType, keyword, pageSize, specialization != null ? specialization.getSpecializationId() : null);
            return List.of();
        }

        String queryString = "SELECT s FROM SpecializedSubject s JOIN FETCH s.specialization JOIN FETCH s.creator JOIN FETCH s.curriculum " +
                "WHERE s.specialization = :specialization";

        if ("name".equalsIgnoreCase(searchType)) {
            keyword = keyword.toLowerCase().trim();
            String[] words = keyword.split("\\s+");
            StringBuilder nameCondition = new StringBuilder();
            for (int i = 0; i < words.length; i++) {
                if (i > 0) {
                    nameCondition.append(" AND ");
                }
                nameCondition.append("LOWER(s.subjectName) LIKE :word").append(i);
            }
            queryString += " AND (" + nameCondition.toString() + ")";
        } else if ("id".equalsIgnoreCase(searchType)) {
            queryString += " AND LOWER(s.subjectId) LIKE LOWER(:keyword)";
        } else {
            logger.warn("Invalid searchType: {}", searchType);
            return List.of();
        }

        try {
            TypedQuery<SpecializedSubject> query = entityManager.createQuery(queryString, SpecializedSubject.class)
                    .setParameter("specialization", specialization)
                    .setFirstResult(firstResult)
                    .setMaxResults(pageSize);

            if ("name".equalsIgnoreCase(searchType)) {
                String[] words = keyword.split("\\s+");
                for (int i = 0; i < words.length; i++) {
                    query.setParameter("word" + i, "%" + words[i] + "%");
                }
            } else if ("id".equalsIgnoreCase(searchType)) {
                query.setParameter("keyword", "%" + keyword.trim() + "%");
            }

            List<SpecializedSubject> subjects = query.getResultList();
            logger.info("Retrieved {} specialized subjects for specialization ID: {} with searchType: {} and keyword: {}",
                    subjects.size(), specialization.getSpecializationId(), searchType, keyword);
            return subjects;
        } catch (Exception e) {
            logger.error("Error searching specialized subjects for specialization ID {}: {}", specialization.getSpecializationId(), e.getMessage(), e);
            throw new RuntimeException("Error searching specialized subjects: " + e.getMessage(), e);
        }
    }

    @Override
    public long countSearchResults(String searchType, String keyword, Specialization specialization) {
        if (keyword == null || keyword.trim().isEmpty() || specialization == null) {
            logger.warn("Invalid parameters for countSearchResults: searchType={}, keyword={}, specializationId={}",
                    searchType, keyword, specialization != null ? specialization.getSpecializationId() : null);
            return 0L;
        }

        String queryString = "SELECT COUNT(s) FROM SpecializedSubject s WHERE s.specialization = :specialization";

        if ("name".equalsIgnoreCase(searchType)) {
            keyword = keyword.toLowerCase().trim();
            String[] words = keyword.split("\\s+");
            StringBuilder nameCondition = new StringBuilder();
            for (int i = 0; i < words.length; i++) {
                if (i > 0) {
                    nameCondition.append(" AND ");
                }
                nameCondition.append("LOWER(s.subjectName) LIKE :word").append(i);
            }
            queryString += " AND (" + nameCondition.toString() + ")";
        } else if ("id".equalsIgnoreCase(searchType)) {
            queryString += " AND LOWER(s.subjectId) LIKE LOWER(:keyword)";
        } else {
            logger.warn("Invalid searchType: {}", searchType);
            return 0L;
        }

        try {
            TypedQuery<Long> query = entityManager.createQuery(queryString, Long.class)
                    .setParameter("specialization", specialization);

            if ("name".equalsIgnoreCase(searchType)) {
                String[] words = keyword.split("\\s+");
                for (int i = 0; i < words.length; i++) {
                    query.setParameter("word" + i, "%" + words[i] + "%");
                }
            } else if ("id".equalsIgnoreCase(searchType)) {
                query.setParameter("keyword", "%" + keyword.trim() + "%");
            }

            long count = query.getSingleResult();
            logger.info("Counted {} specialized subjects for specialization ID: {} with searchType: {} and keyword: {}",
                    count, specialization.getSpecializationId(), searchType, keyword);
            return count;
        } catch (Exception e) {
            logger.error("Error counting search results for specialization ID {}: {}", specialization.getSpecializationId(), e.getMessage(), e);
            throw new RuntimeException("Error counting search results: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsBySubjectExcludingName(String subjectName, String subjectId) {
        if (subjectName == null || subjectName.trim().isEmpty()) {
            logger.warn("Invalid subjectName for existence check: {}", subjectName);
            return false;
        }
        try {
            List<Subjects> subjects = entityManager.createQuery(
                            "SELECT s FROM Subjects s WHERE s.subjectName = :name AND s.subjectId != :subjectId", Subjects.class)
                    .setParameter("name", subjectName.trim())
                    .setParameter("subjectId", subjectId != null ? subjectId : "")
                    .getResultList();
            logger.debug("Found {} subjects with name {} excluding ID {}", subjects.size(), subjectName, subjectId);
            return !subjects.isEmpty();
        } catch (Exception e) {
            logger.error("Error checking subject existence by name: {}", e.getMessage(), e);
            throw new RuntimeException("Error checking subject existence by name: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isDuplicateSubjectName(String subjectName, String subjectId) {
        if (subjectName == null || subjectName.trim().isEmpty()) {
            logger.warn("Invalid subjectName for duplicate check: {}", subjectName);
            return false;
        }
        SpecializedSubject existingSubject = subjectId != null ? getSubjectById(subjectId) : null;
        if (existingSubject == null || !existingSubject.getSubjectName().equalsIgnoreCase(subjectName.trim())) {
            return subjectsService.existsBySubjectNameExcludingId(subjectName.trim(), subjectId != null ? subjectId : "");
        }
        return false;
    }

    @Override
    public void addSubject(SpecializedSubject subject, Specialization specialization) {
        try {
            subject.setCreator(staffsService.getStaff());
            subject.setSpecialization(specialization);
            if (subject.getCurriculum() == null) {
                Curriculum defaultCurriculum = entityManager.createQuery("SELECT c FROM Curriculum c WHERE c.name = 'Default'", Curriculum.class)
                        .setMaxResults(1)
                        .getResultList().stream().findFirst().orElse(null);
                if (defaultCurriculum != null) {
                    subject.setCurriculum(defaultCurriculum);
                } else {
                    throw new IllegalStateException("No default curriculum found.");
                }
            }
            entityManager.persist(subject);
            logger.info("Added new specialized subject with ID: {} by staff ID: {} for specialization ID: {}",
                    subject.getSubjectId(), subject.getCreator().getId(), specialization.getSpecializationId());
        } catch (Exception e) {
            logger.error("Error adding specialized subject: {}", e.getMessage(), e);
            throw new RuntimeException("Error adding specialized subject: " + e.getMessage(), e);
        }
    }

    @Override
    public SpecializedSubject getSubjectById(String subjectId) {
        if (subjectId == null) {
            logger.warn("Subject ID is null");
            throw new IllegalArgumentException("Subject ID cannot be null");
        }
        try {
            SpecializedSubject subject = entityManager.find(SpecializedSubject.class, subjectId);
            return subject;
        } catch (Exception e) {
            logger.error("Error retrieving specialized subject by ID {}: {}", subjectId, e.getMessage(), e);
            throw new RuntimeException("Error retrieving specialized subject by ID " + subjectId + ": " + e.getMessage(), e);
        }
    }

    @Override
    public SpecializedSubject getSubjectByName(String subjectName) {
        if (subjectName == null || subjectName.trim().isEmpty()) {
            logger.warn("Subject name is null or empty");
            return null;
        }
        try {
            List<SpecializedSubject> subjects = entityManager.createQuery(
                            "SELECT s FROM SpecializedSubject s JOIN FETCH s.curriculum WHERE s.subjectName = :name", SpecializedSubject.class)
                    .setParameter("name", subjectName.trim())
                    .getResultList();
            SpecializedSubject subject = subjects.isEmpty() ? null : subjects.get(0);
            if (subject == null) {
                logger.warn("Specialized subject with name {} not found", subjectName);
            }
            return subject;
        } catch (Exception e) {
            logger.error("Error retrieving specialized subject by name {}: {}", subjectName, e.getMessage(), e);
            throw new RuntimeException("Error retrieving specialized subject by name " + subjectName + ": " + e.getMessage(), e);
        }
    }

    @Override
    public SpecializedSubject checkNameSubject(SpecializedSubject subject) {
        try {
            return getSubjectByName(subject != null ? subject.getSubjectName() : null);
        } catch (Exception e) {
            logger.error("Error checking specialized subject name: {}", e.getMessage(), e);
            throw new RuntimeException("Error checking specialized subject name: " + e.getMessage(), e);
        }
    }

    @Override
    public List<SpecializedSubject> subjectsBySpecialization(Specialization specialization) {
        if (specialization == null) {
            logger.warn("Specialization is null");
            return List.of();
        }
        try {
            return entityManager.createQuery(
                            "SELECT s FROM SpecializedSubject s WHERE s.specialization = :specialization",
                            SpecializedSubject.class)
                    .setParameter("specialization", specialization)
                    .getResultList();
        } catch (Exception e) {
            logger.error("Error retrieving specialized subjects by specialization ID {}: {}", specialization.getSpecializationId(), e.getMessage(), e);
            throw new RuntimeException("Error retrieving specialized subjects by specialization: " + e.getMessage(), e);
        }
    }

    @Override
    public List<SpecializedSubject> AcceptedSubjectsBySpecialization(Specialization specialization) {
        if (specialization == null) {
            logger.warn("Specialization is null");
            return List.of();
        }
        try {
            return entityManager.createQuery(
                            "SELECT s FROM SpecializedSubject s WHERE s.specialization = :specialization",
                            SpecializedSubject.class)
                    .setParameter("specialization", specialization)
                    .getResultList();
        } catch (Exception e) {
            logger.error("Error retrieving accepted specialized subjects by specialization ID {}: {}", specialization.getSpecializationId(), e.getMessage(), e);
            throw new RuntimeException("Error retrieving accepted specialized subjects by specialization: " + e.getMessage(), e);
        }
    }

    @Override
    public List<SpecializedSubject> getSubjects() {
        try {
            return entityManager.createQuery(
                            "SELECT s FROM SpecializedSubject s",
                            SpecializedSubject.class)
                    .getResultList();
        } catch (Exception e) {
            logger.error("Error retrieving all specialized subjects: {}", e.getMessage(), e);
            throw new RuntimeException("Error retrieving all specialized subjects: " + e.getMessage(), e);
        }
    }

    @Override
    public SpecializedSubject editSubject(String id, SpecializedSubject subject) {
        try {
            SpecializedSubject existing = entityManager.find(SpecializedSubject.class, id);
            if (existing == null) {
                throw new IllegalArgumentException("Specialized subject with ID " + id + " not found");
            }
            if (subject.getSubjectName() != null) {
                existing.setSubjectName(subject.getSubjectName());
            }
            if (subject.getCurriculum() != null) {
                existing.setCurriculum(subject.getCurriculum());
            }
            if (subject.getSpecialization() != null) {
                existing.setSpecialization(subject.getSpecialization());
            }
            return entityManager.merge(existing);
        } catch (Exception e) {
            logger.error("Error editing specialized subject with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error editing specialized subject with ID " + id + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteSubject(String id) {
        if (id == null) {
            logger.warn("Subject ID is null");
            throw new IllegalArgumentException("Subject ID cannot be null");
        }
        try {
            SpecializedSubject subject = entityManager.find(SpecializedSubject.class, id);
            if (subject != null) {
                entityManager.remove(subject);
                logger.info("Deleted specialized subject with ID: {}", id);
            }
        } catch (Exception e) {
            logger.error("Error deleting specialized subject with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error deleting specialized subject with ID " + id + ": " + e.getMessage(), e);
        }
    }

    @Override
    public String generateUniqueSubjectId(String specializationId, LocalDate createdDate) {
        String prefix = specializationId != null ? specializationId : "SUBGEN";
        String year = String.format("%02d", createdDate.getYear() % 100);
        String date = String.format("%02d%02d", createdDate.getMonthValue(), createdDate.getDayOfMonth());
        String id;
        SecureRandom random = new SecureRandom();
        do {
            String randomDigit = String.valueOf(random.nextInt(10));
            id = prefix + year + date + randomDigit;
        } while (getSubjectById(id) != null);
        return id;
    }

    @Override
    public Map<String, String> validateSubject(SpecializedSubject subject, String specializationId, String curriculumId) {
        Map<String, String> errors = new HashMap<>();

        // Validate subject
        if (subject == null) {
            errors.put("general", "Subject cannot be null.");
            return errors;
        }
        // Validate subject name
        if (subject.getSubjectName() == null || subject.getSubjectName().trim().isEmpty()) {
            errors.put("subjectName", "Subject name cannot be blank.");
        } else if (!isValidName(subject.getSubjectName())) {
            errors.put("subjectName", "Subject name is not valid. Only letters, numbers, spaces, and standard punctuation are allowed.");
        } else if (isDuplicateSubjectName(subject.getSubjectName(), subject.getSubjectId())) {
            errors.put("subjectName", "Subject name is already in use.");
        }
        // Validate specialization
        if (specializationId == null || specializationId.trim().isEmpty()) {
            errors.put("specializationId", "Specialization is required.");
        } else {
            try {
                Specialization specialization = entityManager.find(Specialization.class, specializationId);
                if (specialization == null) {
                    errors.put("specializationId", "Invalid specialization selected.");
                }
            } catch (Exception e) {
                errors.put("specializationId", "Error validating specialization: " + e.getMessage());
            }
        }

        // Validate curriculum
        if (curriculumId == null || curriculumId.trim().isEmpty()) {
            errors.put("curriculumId", "Curriculum is required.");
        } else {
            try {
                Curriculum curriculum = entityManager.find(Curriculum.class, curriculumId);
                if (curriculum == null) {
                    errors.put("curriculumId", "Invalid curriculum selected.");
                }
            } catch (Exception e) {
                errors.put("curriculumId", "Error validating curriculum: " + e.getMessage());
            }
        }

        // Validate staff
        if (staffsService.getStaff() == null) {
            errors.put("general", "Authenticated staff not found.");
        }

        return errors;
    }

    @Override
    public List<SpecializedSubject> getPaginatedSubjects(int firstResult, int pageSize) {
        try {
            return entityManager.createQuery(
                            "SELECT s FROM SpecializedSubject s WHERE s.specialization.major = :major",
                            SpecializedSubject.class)
                    .setParameter("major", staffsService.getStaffMajor())
                    .setFirstResult(firstResult)
                    .setMaxResults(pageSize)
                    .getResultList();
        } catch (Exception e) {
            logger.error("Error retrieving paginated specialized subjects: {}", e.getMessage(), e);
            throw new RuntimeException("Error retrieving paginated specialized subjects: " + e.getMessage(), e);
        }
    }

    @Override
    public long numberOfSubjects(Majors majors) {
        try {
            return entityManager.createQuery(
                            "SELECT COUNT(s) FROM SpecializedSubject s WHERE s.specialization.major = :major",
                            Long.class)
                    .setParameter("major", majors)
                    .getSingleResult();
        } catch (Exception e) {
            logger.error("Error counting specialized subjects: {}", e.getMessage(), e);
            throw new RuntimeException("Error counting specialized subjects: " + e.getMessage(), e);
        }
    }

    @Override
    public List<SpecializedSubject> getPaginatedSubjectsBySpecialization(int firstResult, int pageSize, String specializationId) {
        if (specializationId == null || specializationId.trim().isEmpty()) {
            logger.warn("Specialization ID is null or empty for getPaginatedSubjectsBySpecialization");
            return List.of();
        }
        try {
            Specialization specialization = entityManager.find(Specialization.class, specializationId);
            if (specialization == null) {
                logger.warn("Specialization with ID {} not found", specializationId);
                return List.of();
            }
            return entityManager.createQuery(
                            "SELECT s FROM SpecializedSubject s WHERE s.specialization = :specialization",
                            SpecializedSubject.class)
                    .setParameter("specialization", specialization)
                    .setFirstResult(firstResult)
                    .setMaxResults(pageSize)
                    .getResultList();
        } catch (Exception e) {
            logger.error("Error retrieving paginated specialized subjects for specialization ID {}: {}", specializationId, e.getMessage(), e);
            throw new RuntimeException("Error retrieving paginated specialized subjects for specialization ID " + specializationId + ": " + e.getMessage(), e);
        }
    }

    @Override
    public long numberOfSubjectsBySpecialization(String specializationId) {
        if (specializationId == null || specializationId.trim().isEmpty()) {
            logger.warn("Specialization ID is null or empty for numberOfSubjectsBySpecialization");
            return 0L;
        }
        try {
            Specialization specialization = entityManager.find(Specialization.class, specializationId);
            if (specialization == null) {
                logger.warn("Specialization with ID {} not found", specializationId);
                return 0L;
            }
            return entityManager.createQuery(
                            "SELECT COUNT(s) FROM SpecializedSubject s WHERE s.specialization = :specialization",
                            Long.class)
                    .setParameter("specialization", specialization)
                    .getSingleResult();
        } catch (Exception e) {
            logger.error("Error counting specialized subjects for specialization ID {}: {}", specializationId, e.getMessage(), e);
            throw new RuntimeException("Error counting specialized subjects for specialization ID " + specializationId + ": " + e.getMessage(), e);
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