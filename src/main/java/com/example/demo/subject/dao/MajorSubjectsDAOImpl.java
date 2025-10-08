package com.example.demo.subject.dao;

import com.example.demo.Curriculum.model.Curriculum;
import com.example.demo.major.model.Majors;
import com.example.demo.subject.model.MajorSubjects;
import com.example.demo.classes.service.ClassesService;
import com.example.demo.staff.service.StaffsService;
import com.example.demo.subject.service.SubjectsService;
import com.example.demo.syllabus.service.SyllabusesService;
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
public class MajorSubjectsDAOImpl implements MajorSubjectsDAO {
    private static final Logger logger = LoggerFactory.getLogger(MajorSubjectsDAOImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    private final StaffsService staffsService;
    private final SyllabusesService syllabusesService;
    private final ClassesService classesService;
    private final SubjectsService subjectsService;

    public MajorSubjectsDAOImpl(StaffsService staffsService, SyllabusesService syllabusesService,
                                ClassesService classesService, SubjectsService subjectsService) {
        this.staffsService = staffsService;
        this.syllabusesService = syllabusesService;
        this.classesService = classesService;
        this.subjectsService = subjectsService;
    }

    @Override
    public List<MajorSubjects> searchSubjects(String searchType, String keyword, int firstResult, int pageSize, Majors major) {
        if (keyword == null || keyword.trim().isEmpty() || pageSize <= 0 || major == null) {
            logger.warn("Invalid search parameters: searchType={}, keyword={}, pageSize={}, majorId={}",
                    searchType, keyword, pageSize, major != null ? major.getMajorId() : null);
            return List.of();
        }

        String queryString = "SELECT s FROM MajorSubjects s JOIN FETCH s.major JOIN FETCH s.creator JOIN FETCH s.curriculum " +
                "WHERE s.major = :major";

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
            queryString += " AND (" + nameCondition.toString() + ")";
        } else if ("id".equalsIgnoreCase(searchType)) {
            queryString += " AND LOWER(s.subjectId) = LOWER(:keyword)";
        } else {
            logger.warn("Invalid searchType: {}", searchType);
            return List.of();
        }

        try {
            TypedQuery<MajorSubjects> query = entityManager.createQuery(queryString, MajorSubjects.class)
                    .setParameter("major", major)
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
    public long countSearchResults(String searchType, String keyword, Majors major) {
        if (keyword == null || keyword.trim().isEmpty() || major == null) {
            logger.warn("Invalid count search parameters: searchType={}, keyword={}, majorId={}",
                    searchType, keyword, major != null ? major.getMajorId() : null);
            return 0L;
        }

        String queryString = "SELECT COUNT(s) FROM MajorSubjects s WHERE s.major = :major";

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
            queryString += " AND (" + nameCondition.toString() + ")";
        } else if ("id".equalsIgnoreCase(searchType)) {
            queryString += " AND LOWER(s.subjectId) = LOWER(:keyword)";
        } else {
            logger.warn("Invalid searchType: {}", searchType);
            return 0L;
        }

        try {
            TypedQuery<Long> query = entityManager.createQuery(queryString, Long.class)
                    .setParameter("major", major);

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
    public boolean existsBySubjectExcludingName(String subjectName, String subjectId) {
        if (subjectName == null || subjectName.trim().isEmpty()) {
            logger.warn("Invalid subjectName for existence check: {}", subjectName);
            return false;
        }

        String queryString = "SELECT s FROM MajorSubjects s WHERE s.subjectName = :name AND s.subjectId != :subjectId";
        try {
            List<MajorSubjects> subjects = entityManager.createQuery(queryString, MajorSubjects.class)
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
    public void addSubject(MajorSubjects subject) {
        try {
            if (subject.getCreator() == null) {
                subject.setCreator(staffsService.getStaff());
            }
            if (subject.getMajor() == null) {
                subject.setMajor(staffsService.getStaffMajor());
            }
            if (subject.getCurriculum() == null) {
                Curriculum defaultCurriculum = entityManager.createQuery("SELECT c FROM Curriculum c WHERE c.name = 'Default'", Curriculum.class)
                        .setMaxResults(1)
                        .getResultList().stream().findFirst().orElse(null);
                if (defaultCurriculum == null) {
                    throw new IllegalStateException("No default curriculum found.");
                }
                subject.setCurriculum(defaultCurriculum);
            }
            entityManager.persist(subject);
        } catch (Exception e) {
            logger.error("Error adding subject: {}", e.getMessage());
            throw new RuntimeException("Error adding subject: " + e.getMessage(), e);
        }
    }

    @Override
    public MajorSubjects getSubjectById(String subjectId) {
        if (subjectId == null || subjectId.trim().isEmpty()) {
            logger.warn("Invalid subject ID: {}", subjectId);
            throw new IllegalArgumentException("Subject ID cannot be null or empty");
        }
        try {
            MajorSubjects subject = entityManager.find(MajorSubjects.class, subjectId);
            if (subject != null) {
                entityManager.detach(subject);
            }
            return subject;
        } catch (Exception e) {
            logger.error("Error retrieving subject by ID {}: {}", subjectId, e.getMessage());
            throw new RuntimeException("Error retrieving subject by ID: " + e.getMessage(), e);
        }
    }

    @Override
    public MajorSubjects getSubjectByName(String subjectName) {
        if (subjectName == null || subjectName.trim().isEmpty()) {
            logger.warn("Invalid subject name: {}", subjectName);
            return null;
        }
        try {
            List<MajorSubjects> subjects = entityManager.createQuery(
                            "SELECT s FROM MajorSubjects s JOIN FETCH s.curriculum WHERE s.subjectName = :name", MajorSubjects.class)
                    .setParameter("name", subjectName.trim())
                    .getResultList();
            return subjects.isEmpty() ? null : subjects.get(0);
        } catch (Exception e) {
            logger.error("Error retrieving subject by name {}: {}", subjectName, e.getMessage());
            throw new RuntimeException("Error retrieving subject by name: " + e.getMessage(), e);
        }
    }

    @Override
    public MajorSubjects checkNameSubject(MajorSubjects subject) {
        try {
            return getSubjectByName(subject != null ? subject.getSubjectName() : null);
        } catch (Exception e) {
            logger.error("Error checking subject name: {}", e.getMessage());
            throw new RuntimeException("Error checking subject name: " + e.getMessage(), e);
        }
    }

    @Override
    public List<MajorSubjects> subjectsByMajor(Majors major) {
        if (major == null) {
            logger.warn("Invalid major for subjectsByMajor");
            return List.of();
        }
        try {
            return entityManager.createQuery(
                            "SELECT s FROM MajorSubjects s JOIN FETCH s.curriculum WHERE s.major = :major ORDER BY s.semester ASC",
                            MajorSubjects.class)
                    .setParameter("major", major)
                    .getResultList();
        } catch (Exception e) {
            logger.error("Error retrieving subjects by major {}: {}", major.getMajorId(), e.getMessage());
            throw new RuntimeException("Error retrieving subjects by major: " + e.getMessage(), e);
        }
    }

    @Override
    public List<MajorSubjects> AcceptedSubjectsByMajor(Majors major) {
        if (major == null) {
            logger.warn("Invalid major for AcceptedSubjectsByMajor");
            return List.of();
        }
        try {
            return entityManager.createQuery(
                            "SELECT s FROM MajorSubjects s JOIN FETCH s.curriculum WHERE s.major = :major ORDER BY s.semester ASC",
                            MajorSubjects.class)
                    .setParameter("major", major)
                    .getResultList();
        } catch (Exception e) {
            logger.error("Error retrieving accepted subjects by major {}: {}", major.getMajorId(), e.getMessage());
            throw new RuntimeException("Error retrieving accepted subjects by major: " + e.getMessage(), e);
        }
    }

    @Override
    public List<MajorSubjects> getSubjects() {
        try {
            return entityManager.createQuery("SELECT s FROM MajorSubjects s JOIN FETCH s.curriculum", MajorSubjects.class)
                    .getResultList();
        } catch (Exception e) {
            logger.error("Error retrieving all subjects: {}", e.getMessage());
            throw new RuntimeException("Error retrieving all subjects: " + e.getMessage(), e);
        }
    }

    @Override
    public MajorSubjects editSubject(String id, MajorSubjects subject) {
        try {
            MajorSubjects existingSubject = entityManager.find(MajorSubjects.class, id);
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
            if (subject.getCurriculum() != null) {
                existingSubject.setCurriculum(subject.getCurriculum());
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
            MajorSubjects subject = entityManager.find(MajorSubjects.class, id);
            if (subject == null) {
                logger.warn("Subject not found for ID: {}", id);
                throw new IllegalArgumentException("Subject with ID " + id + " not found");
            }
            syllabusesService.deleteSyllabusBySubject(subject);
            classesService.SetNullWhenDeletingSubject(subject);
            entityManager.remove(subject);
        } catch (Exception e) {
            logger.error("Error deleting subject with ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error deleting subject: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateUniqueSubjectId(String majorId, LocalDate createdDate) {
        String prefix = majorId != null ? majorId : "SUBGEN";
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
        MajorSubjects existingSubject = subjectId != null ? getSubjectById(subjectId) : null;
        if (existingSubject == null || !existingSubject.getSubjectName().equalsIgnoreCase(subjectName.trim())) {
            return subjectsService.existsBySubjectNameExcludingId(subjectName.trim(), subjectId != null ? subjectId : "");
        }
        return false;
    }

    @Override
    public Map<String, String> validateSubject(MajorSubjects subject, String curriculumId) {
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

        // Validate curriculum
        if (curriculumId == null || curriculumId.trim().isEmpty()) {
            errors.put("curriculumId", "Curriculum is required");
        } else {
            Curriculum curriculum = entityManager.find(Curriculum.class, curriculumId);
            if (curriculum == null) {
                errors.put("curriculumId", "Selected curriculum does not exist");
            }
        }

        // Validate staff and major
        if (staffsService.getStaff() == null) {
            errors.put("general", "Authenticated staff not found");
        } else if (staffsService.getStaffMajor() == null) {
            errors.put("general", "Staff major not found");
        }

        return errors;
    }

    @Override
    public List<MajorSubjects> getPaginatedSubjects(int firstResult, int pageSize, Majors major) {
        if (major == null || pageSize <= 0 || firstResult < 0) {
            logger.warn("Invalid pagination parameters: majorId={}, pageSize={}, firstResult={}",
                    major != null ? major.getMajorId() : null, pageSize, firstResult);
            return List.of();
        }
        try {
            return entityManager.createQuery(
                            "SELECT s FROM MajorSubjects s JOIN FETCH s.curriculum WHERE s.major = :major ORDER BY s.semester ASC",
                            MajorSubjects.class)
                    .setParameter("major", major)
                    .setFirstResult(firstResult)
                    .setMaxResults(pageSize)
                    .getResultList();
        } catch (Exception e) {
            logger.error("Error retrieving paginated subjects: {}", e.getMessage());
            throw new RuntimeException("Error retrieving paginated subjects: " + e.getMessage(), e);
        }
    }

    @Override
    public long numberOfSubjects(Majors major) {
        if (major == null) {
            logger.warn("Invalid major for numberOfSubjects");
            return 0L;
        }
        try {
            return entityManager.createQuery(
                            "SELECT COUNT(s) FROM MajorSubjects s WHERE s.major = :major",
                            Long.class)
                    .setParameter("major", major)
                    .getSingleResult();
        } catch (Exception e) {
            logger.error("Error counting subjects: {}", e.getMessage());
            throw new RuntimeException("Error counting subjects: " + e.getMessage(), e);
        }
    }

    private boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        String nameRegex = "^[\\p{L}0-9][\\p{L}0-9 .'-]{1,49}$";
        return name.matches(nameRegex);
    }
}