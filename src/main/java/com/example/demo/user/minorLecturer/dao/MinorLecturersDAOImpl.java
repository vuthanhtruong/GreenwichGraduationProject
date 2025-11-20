package com.example.demo.user.minorLecturer.dao;

import com.example.demo.email_service.dto.MinorLecturerEmailContext;
import com.example.demo.email_service.service.EmailServiceForMinorLecturerService;
import com.example.demo.entity.Enums.Gender;
import com.example.demo.specialization.security.model.CustomOidcUserPrincipal;
import com.example.demo.specialization.security.model.DatabaseUserPrincipal;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import com.example.demo.user.minorLecturer.model.MinorLecturers;
import com.example.demo.user.deputyStaff.service.DeputyStaffsService;
import com.example.demo.user.person.model.Persons;
import com.example.demo.user.person.service.PersonsService;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Transactional
public class MinorLecturersDAOImpl implements MinorLecturersDAO {
    @Override
    public long totalMinorLecturersAllCampus() {
        return entityManager.createQuery("SELECT COUNT(l) FROM MinorLecturers l", Long.class)
                .getSingleResult();
    }

    @Override
    public long newMinorLecturersThisYearAllCampus() {
        int year = LocalDate.now().getYear();
        return entityManager.createQuery(
                        "SELECT COUNT(l) FROM MinorLecturers l WHERE YEAR(l.createdDate) = :year", Long.class)
                .setParameter("year", year)
                .getSingleResult();
    }

    @Override
    public Map<String, Long> minorLecturersByCampus() {
        List<Object[]> rows = entityManager.createQuery(
                        "SELECT c.campusName, COUNT(l) " +
                                "FROM MinorLecturers l JOIN l.campus c " +
                                "GROUP BY c.campusId, c.campusName " +
                                "ORDER BY COUNT(l) DESC", Object[].class)
                .getResultList();

        return rows.stream()
                .collect(Collectors.toMap(
                        arr -> (String) arr[0],
                        arr -> (Long) arr[1],
                        (v1, v2) -> v1,
                        LinkedHashMap::new
                ));
    }

    @Override
    public Map<String, Long> minorLecturersByGender() {
        List<Object[]> rows = entityManager.createQuery(
                        "SELECT COALESCE(l.gender, 'OTHER'), COUNT(l) FROM MinorLecturers l GROUP BY l.gender",
                        Object[].class)
                .getResultList();

        Map<String, Long> result = new LinkedHashMap<>();
        for (Object[] row : rows) {
            Gender g = (Gender) row[0];
            String label = switch (g) {
                case MALE -> "Male";
                case FEMALE -> "Female";
                case OTHER -> "Other";
            };
            result.put(label, (Long) row[1]);
        }
        return result;
    }

    @Override
    public Map<String, Long> minorLecturersByAgeGroup() {
        int currentYear = LocalDate.now().getYear();

        String jpql = """
        SELECT 
            CASE 
                WHEN l.birthDate IS NULL THEN 'Unknown'
                WHEN YEAR(l.birthDate) >= :currentYear - 29 THEN 'Under 30'
                WHEN YEAR(l.birthDate) >= :currentYear - 39 THEN '30-39'
                WHEN YEAR(l.birthDate) >= :currentYear - 49 THEN '40-49'
                ELSE '50 and above'
            END,
            COUNT(l)
        FROM MinorLecturers l
        GROUP BY 
            CASE 
                WHEN l.birthDate IS NULL THEN 'Unknown'
                WHEN YEAR(l.birthDate) >= :currentYear - 29 THEN 'Under 30'
                WHEN YEAR(l.birthDate) >= :currentYear - 39 THEN '30-39'
                WHEN YEAR(l.birthDate) >= :currentYear - 49 THEN '40-49'
                ELSE '50 and above'
            END
        ORDER BY 
            MIN(CASE WHEN l.birthDate IS NULL THEN 9999 ELSE YEAR(l.birthDate) END) DESC
        """;

        List<Object[]> rows = entityManager.createQuery(jpql, Object[].class)
                .setParameter("currentYear", currentYear)
                .getResultList();

        return rows.stream()
                .collect(Collectors.toMap(
                        arr -> (String) arr[0],
                        arr -> (Long) arr[1],
                        (v1, v2) -> v1,
                        LinkedHashMap::new
                ));
    }

    @Override
    public List<MinorLecturers> top5NewestMinorLecturers() {
        return entityManager.createQuery(
                        "SELECT l FROM MinorLecturers l ORDER BY l.createdDate DESC", MinorLecturers.class)
                .setMaxResults(5)
                .getResultList();
    }

    @Override
    public List<MinorLecturers> top5MostSeniorMinorLecturers() {
        return entityManager.createQuery(
                        "SELECT l FROM MinorLecturers l WHERE l.createdDate IS NOT NULL ORDER BY l.createdDate ASC", MinorLecturers.class)
                .setMaxResults(5)
                .getResultList();
    }

    @Override
    public long countCampusesWithoutMinorLecturer() {
        return entityManager.createQuery(
                        "SELECT COUNT(c) FROM Campuses c " +
                                "WHERE NOT EXISTS (SELECT 1 FROM MinorLecturers l WHERE l.campus = c)", Long.class)
                .getSingleResult();
    }

    // Trong MinorLecturersDAOImpl.java
    @Override
    public long totalMinorLecturersInMyCampus() {
        DeputyStaffs staff = deputyStaffsService.getDeputyStaff(); // hoặc staffsService.getStaff()
        if (staff == null || staff.getCampus() == null) return 0L;

        String campusId = staff.getCampus().getCampusId();
        return entityManager.createQuery(
                        "SELECT COUNT(l) FROM MinorLecturers l WHERE l.campus.campusId = :campusId", Long.class)
                .setParameter("campusId", campusId)
                .getSingleResult();
    }

    @Override
    public long newMinorLecturersThisYearInMyCampus() {
        DeputyStaffs staff = deputyStaffsService.getDeputyStaff();
        if (staff == null || staff.getCampus() == null) return 0L;

        int year = LocalDate.now().getYear();
        String campusId = staff.getCampus().getCampusId();

        return entityManager.createQuery(
                        "SELECT COUNT(l) FROM MinorLecturers l " +
                                "WHERE l.campus.campusId = :campusId AND YEAR(l.createdDate) = :year", Long.class)
                .setParameter("campusId", campusId)
                .setParameter("year", year)
                .getSingleResult();
    }

    @Override
    public List<Object[]> minorLecturersByGenderInMyCampus() {
        DeputyStaffs staff = deputyStaffsService.getDeputyStaff();
        if (staff == null || staff.getCampus() == null) return List.of();

        String campusId = staff.getCampus().getCampusId();

        return entityManager.createQuery(
                        "SELECT COALESCE(l.gender, 'OTHER'), COUNT(l) " +
                                "FROM MinorLecturers l WHERE l.campus.campusId = :campusId " +
                                "GROUP BY l.gender", Object[].class)
                .setParameter("campusId", campusId)
                .getResultList();
    }

    @Override
    public List<Object[]> minorLecturersByAgeGroupInMyCampus() {
        DeputyStaffs staff = deputyStaffsService.getDeputyStaff();
        if (staff == null || staff.getCampus() == null) return List.of();

        String campusId = staff.getCampus().getCampusId();

        String jpql = """
        SELECT 
            CASE 
                WHEN YEAR(l.birthDate) >= 1997 THEN 'Gen Z (≤28)'
                WHEN YEAR(l.birthDate) BETWEEN 1981 AND 1996 THEN 'Millennial (29-44)'
                WHEN YEAR(l.birthDate) BETWEEN 1965 AND 1980 THEN 'Gen X (45-60)'
                ELSE 'Boomer (>60)'
            END AS ageGroup,
            COUNT(l) AS lecturerCount
        FROM MinorLecturers l 
        WHERE l.campus.campusId = :campusId 
          AND l.birthDate IS NOT NULL
        GROUP BY 
            CASE 
                WHEN YEAR(l.birthDate) >= 1997 THEN 'Gen Z (≤28)'
                WHEN YEAR(l.birthDate) BETWEEN 1981 AND 1996 THEN 'Millennial (29-44)'
                WHEN YEAR(l.birthDate) BETWEEN 1965 AND 1980 THEN 'Gen X (45-60)'
                ELSE 'Boomer (>60)'
            END
        ORDER BY lecturerCount DESC
        """;

        return entityManager.createQuery(jpql, Object[].class)
                .setParameter("campusId", campusId)
                .getResultList();
    }

    @Override
    public List<Object[]> top5MostExperiencedMinorLecturersInMyCampus() {
        DeputyStaffs staff = deputyStaffsService.getDeputyStaff();
        if (staff == null || staff.getCampus() == null) return List.of();

        String campusId = staff.getCampus().getCampusId();

        return entityManager.createQuery(
                        "SELECT l.id, CONCAT(l.firstName, ' ', l.lastName), l.createdDate " +
                                "FROM MinorLecturers l " +
                                "WHERE l.campus.campusId = :campusId AND l.createdDate IS NOT NULL " +
                                "ORDER BY l.createdDate ASC", Object[].class)
                .setMaxResults(5)
                .setParameter("campusId", campusId)
                .getResultList();
    }

    @Override
    public List<MinorLecturers> colleaguesByCampusId(String campusId) { // ĐÃ SỬA
        return entityManager.createQuery(
                        "FROM MinorLecturers s WHERE s.campus.id = :campusId AND s.id != :id",
                        MinorLecturers.class)
                .setParameter("campusId", campusId)
                .setParameter("id", getMinorLecturer().getId())
                .getResultList();
    }

    @Override
    public List<MinorLecturers> searchMinorLecturersByCampus(
            String campusId, String searchType, String keyword, int firstResult, int pageSize) {
        try {
            if (campusId == null || campusId.trim().isEmpty() ||
                    keyword == null || keyword.trim().isEmpty() ||
                    pageSize <= 0 || firstResult < 0) {
                return List.of();
            }

            String queryString = "SELECT l FROM MinorLecturers l JOIN FETCH l.campus WHERE l.campus.id = :campusId";

            if ("name".equalsIgnoreCase(searchType)) {
                keyword = keyword.toLowerCase().trim();
                String[] words = keyword.split("\\s+");
                StringBuilder nameCondition = new StringBuilder();
                for (int i = 0; i < words.length; i++) {
                    if (i > 0) nameCondition.append(" AND ");
                    nameCondition.append("(LOWER(l.firstName) LIKE :word").append(i)
                            .append(" OR LOWER(l.lastName) LIKE :word").append(i).append(")");
                }
                queryString += " AND (" + nameCondition + ")";
            } else if ("id".equalsIgnoreCase(searchType)) {
                queryString += " AND LOWER(l.id) = LOWER(:keyword)";
            } else {
                return List.of();
            }

            TypedQuery<MinorLecturers> query = entityManager.createQuery(queryString, MinorLecturers.class)
                    .setParameter("campusId", campusId)
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

            List<MinorLecturers> result = query.getResultList();
            logger.info("Found {} minor lecturers in campus {} for search: {}", result.size(), campusId, keyword);
            return result;
        } catch (Exception e) {
            logger.error("Error searching minor lecturers by campus: {}", e.getMessage(), e);
            throw new RuntimeException("Error searching minor lecturers by campus", e);
        }
    }

    @Override
    public long countSearchMinorLecturersByCampus(String campusId, String searchType, String keyword) {
        try {
            if (campusId == null || campusId.trim().isEmpty() ||
                    keyword == null || keyword.trim().isEmpty()) {
                return 0L;
            }

            String queryString = "SELECT COUNT(l) FROM MinorLecturers l WHERE l.campus.id = :campusId";

            if ("name".equalsIgnoreCase(searchType)) {
                keyword = keyword.toLowerCase().trim();
                String[] words = keyword.split("\\s+");
                StringBuilder nameCondition = new StringBuilder();
                for (int i = 0; i < words.length; i++) {
                    if (i > 0) nameCondition.append(" AND ");
                    nameCondition.append("(LOWER(l.firstName) LIKE :word").append(i)
                            .append(" OR LOWER(l.lastName) LIKE :word").append(i).append(")");
                }
                queryString += " AND (" + nameCondition + ")";
            } else if ("id".equalsIgnoreCase(searchType)) {
                queryString += " AND LOWER(l.id) = LOWER(:keyword)";
            } else {
                return 0L;
            }

            TypedQuery<Long> query = entityManager.createQuery(queryString, Long.class)
                    .setParameter("campusId", campusId);

            if ("name".equalsIgnoreCase(searchType)) {
                String[] words = keyword.split("\\s+");
                for (int i = 0; i < words.length; i++) {
                    query.setParameter("word" + i, "%" + words[i] + "%");
                }
            } else if ("id".equalsIgnoreCase(searchType)) {
                query.setParameter("keyword", keyword.trim());
            }

            long count = query.getSingleResult();
            logger.info("Counted {} minor lecturers in campus {} for search: {}", count, campusId, keyword);
            return count;
        } catch (Exception e) {
            logger.error("Error counting minor lecturers by campus: {}", e.getMessage(), e);
            throw new RuntimeException("Error counting minor lecturers by campus", e);
        }
    }
    @Override
    public List<MinorLecturers> colleagueBycampusId(String campusId) {
        return entityManager.createQuery("from MinorLecturers s where s.campus.id=:campusId And s.id!=:id", MinorLecturers.class).setParameter("campusId", campusId).
                setParameter("id", getMinorLecturer().getId()).getResultList();
    }

    @Override
    public MinorLecturers getMinorLecturer() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new IllegalStateException("No authenticated user");
        }

        Object principal = auth.getPrincipal();
        Persons person = switch (principal) {
            case DatabaseUserPrincipal dbPrincipal -> dbPrincipal.getPerson();
            case CustomOidcUserPrincipal oidcPrincipal -> oidcPrincipal.getPerson();
            default -> throw new IllegalStateException("Unknown principal type: " + principal.getClass());
        };

        if (!(person instanceof MinorLecturers minorLecturers)) {
            throw new IllegalStateException("Authenticated user is not a student");
        }

        return entityManager.find(MinorLecturers.class,minorLecturers.getId());
    }

    private static final Logger logger = LoggerFactory.getLogger(MinorLecturersDAOImpl.class);
    private final PersonsService personsService;
    private final DeputyStaffsService deputyStaffsService;
    private final EmailServiceForMinorLecturerService emailServiceForMinorLecturerService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public MinorLecturersDAOImpl(PersonsService personsService, DeputyStaffsService deputyStaffsService,
                                 EmailServiceForMinorLecturerService emailServiceForMinorLecturerService) {
        this.personsService = personsService;
        this.deputyStaffsService = deputyStaffsService;
        this.emailServiceForMinorLecturerService = emailServiceForMinorLecturerService;
    }

    @Override
    public List<MinorLecturers> getMinorLecturers() {
        try {
            TypedQuery<MinorLecturers> query = entityManager.createQuery(
                    "SELECT l FROM MinorLecturers l JOIN FETCH l.campus JOIN FETCH l.creator",
                    MinorLecturers.class);
            List<MinorLecturers> minorLecturers = query.getResultList();
            logger.info("Retrieved {} minor lecturers", minorLecturers.size());
            return minorLecturers;
        } catch (Exception e) {
            logger.error("Error retrieving minor lecturers: {}", e.getMessage(), e);
            throw new RuntimeException("Error retrieving minor lecturers: " + e.getMessage(), e);
        }
    }

    @Override
    public MinorLecturers addMinorLecturers(MinorLecturers minorLecturer, String randomPassword) {
        try {
            minorLecturer.setCreatedDate(LocalDate.now());
            minorLecturer.setCreator(deputyStaffsService.getDeputyStaff());
            minorLecturer.setCampus(deputyStaffsService.getDeputyStaff().getCampus());
            MinorLecturers savedMinorLecturer = entityManager.merge(minorLecturer);

            MinorLecturerEmailContext context = new MinorLecturerEmailContext(
                    savedMinorLecturer.getId(),
                    savedMinorLecturer.getFullName(),
                    savedMinorLecturer.getEmail(),
                    savedMinorLecturer.getPhoneNumber(),
                    savedMinorLecturer.getBirthDate(),
                    savedMinorLecturer.getGender() != null ? savedMinorLecturer.getGender().toString() : null,
                    savedMinorLecturer.getFullAddress(),
                    savedMinorLecturer.getCampus() != null ? savedMinorLecturer.getCampus().getCampusName() : null,
                    savedMinorLecturer.getCreator() != null ? savedMinorLecturer.getCreator().getFullName() : null,
                    savedMinorLecturer.getCreatedDate()
            );

            try {
                String subject = "Your Minor Lecturer Account Information";
                emailServiceForMinorLecturerService.sendEmailToNotifyLoginInformation(savedMinorLecturer.getEmail(), subject, context, randomPassword);
            } catch (Exception e) {
                logger.error("Failed to send email to {}: {}", savedMinorLecturer.getEmail(), e.getMessage());
            }

            logger.info("Added new minor lecturer with ID: {}", savedMinorLecturer.getId());
            return savedMinorLecturer;
        } catch (Exception e) {
            logger.error("Error adding minor lecturer: {}", e.getMessage(), e);
            throw new RuntimeException("Error adding minor lecturer: " + e.getMessage(), e);
        }
    }

    @Override
    public long numberOfMinorLecturers() {
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                    "SELECT COUNT(l) FROM MinorLecturers l",
                    Long.class);
            long count = query.getSingleResult();
            logger.info("Total minor lecturers: {}", count);
            return count;
        } catch (Exception e) {
            logger.error("Error counting minor lecturers: {}", e.getMessage(), e);
            throw new RuntimeException("Error counting minor lecturers: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteMinorLecturer(String id) {
        try {
            MinorLecturers minorLecturer = entityManager.find(MinorLecturers.class, id);
            if (minorLecturer == null) {
                logger.warn("Minor lecturer with ID {} not found for deletion", id);
                return;
            }

            entityManager.remove(minorLecturer);
            logger.info("Deleted minor lecturer with ID: {}", id);

            try {
                MinorLecturerEmailContext context = new MinorLecturerEmailContext(
                        minorLecturer.getId(),
                        minorLecturer.getFullName(),
                        minorLecturer.getEmail(),
                        minorLecturer.getPhoneNumber(),
                        minorLecturer.getBirthDate(),
                        minorLecturer.getGender() != null ? minorLecturer.getGender().toString() : null,
                        minorLecturer.getFullAddress(),
                        minorLecturer.getCampus() != null ? minorLecturer.getCampus().getCampusName() : null,
                        minorLecturer.getCreator() != null ? minorLecturer.getCreator().getFullName() : null,
                        minorLecturer.getCreatedDate()
                );
                String subject = "Your Minor Lecturer Account Has Been Deactivated";
                emailServiceForMinorLecturerService.sendEmailToNotifyInformationAfterEditing(
                        minorLecturer.getEmail(), subject, context);
                logger.info("Sent deletion email to minor lecturer: {}", minorLecturer.getEmail());
            } catch (Exception e) {
                logger.warn("Failed to send deletion email to {}: {}", minorLecturer.getEmail(), e.getMessage());
            }
        } catch (Exception e) {
            logger.error("Error deleting minor lecturer with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error deleting minor lecturer with ID " + id + ": " + e.getMessage(), e);
        }
    }

    @Override
    public MinorLecturers getMinorLecturerById(String id) {
        try {
            MinorLecturers minorLecturer = entityManager.find(MinorLecturers.class, id);
            if (minorLecturer == null) {
                logger.warn("Minor lecturer with ID {} not found", id);
            }
            return minorLecturer;
        } catch (Exception e) {
            logger.error("Error retrieving minor lecturer by ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error retrieving minor lecturer by ID " + id + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void updateMinorLecturer(String id, MinorLecturers minorLecturer, MultipartFile avatarFile) throws MessagingException, IOException {
        try {
            MinorLecturers existingMinorLecturer = entityManager.find(MinorLecturers.class, id);
            if (existingMinorLecturer == null) {
                throw new IllegalArgumentException("Minor lecturer with ID " + id + " not found");
            }
            if (avatarFile != null && !avatarFile.isEmpty()) {
                minorLecturer.setAvatar(avatarFile.getBytes());
            } else {
                minorLecturer.setAvatar(existingMinorLecturer.getAvatar());
            }

            updateMinorLecturerFields(existingMinorLecturer, minorLecturer);
            entityManager.merge(existingMinorLecturer);
            logger.info("Updated minor lecturer with ID: {}", id);

            try {
                MinorLecturerEmailContext context = new MinorLecturerEmailContext(
                        existingMinorLecturer.getId(),
                        existingMinorLecturer.getFullName(),
                        existingMinorLecturer.getEmail(),
                        existingMinorLecturer.getPhoneNumber(),
                        existingMinorLecturer.getBirthDate(),
                        existingMinorLecturer.getGender() != null ? existingMinorLecturer.getGender().toString() : null,
                        existingMinorLecturer.getFullAddress(),
                        existingMinorLecturer.getCampus() != null ? existingMinorLecturer.getCampus().getCampusName() : null,
                        existingMinorLecturer.getCreator() != null ? existingMinorLecturer.getCreator().getFullName() : null,
                        existingMinorLecturer.getCreatedDate()
                );
                String subject = "Your Minor Lecturer Account Information After Being Edited";
                emailServiceForMinorLecturerService.sendEmailToNotifyInformationAfterEditing(
                        existingMinorLecturer.getEmail(), subject, context);
                logger.info("Sent update email to minor lecturer: {}", existingMinorLecturer.getEmail());
            } catch (Exception e) {
                logger.warn("Failed to send update email to {}: {}", existingMinorLecturer.getEmail(), e.getMessage());
            }
        } catch (Exception e) {
            logger.error("Unexpected error updating minor lecturer with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Unexpected error updating minor lecturer: " + e.getMessage(), e);
        }
    }

    @Override
    public List<MinorLecturers> getPaginatedMinorLecturers(int firstResult, int pageSize) {
        try {
            TypedQuery<MinorLecturers> query = entityManager.createQuery(
                            "SELECT l FROM MinorLecturers l JOIN FETCH l.campus JOIN FETCH l.creator",
                            MinorLecturers.class)
                    .setFirstResult(firstResult)
                    .setMaxResults(pageSize);
            List<MinorLecturers> minorLecturers = query.getResultList();
            logger.info("Retrieved {} minor lecturers for page starting at {}", minorLecturers.size(), firstResult);
            return minorLecturers;
        } catch (Exception e) {
            logger.error("Error retrieving paginated minor lecturers: {}", e.getMessage(), e);
            throw new RuntimeException("Error retrieving paginated minor lecturers: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, String> minorLecturerValidation(MinorLecturers minorLecturer, MultipartFile avatarFile) {
        Map<String, String> errors = new HashMap<>();

        if (minorLecturer.getFirstName() == null || !isValidName(minorLecturer.getFirstName())) {
            errors.put("firstName", "First name is not valid. Only letters, spaces, and standard punctuation are allowed.");
        }

        if (minorLecturer.getLastName() == null || !isValidName(minorLecturer.getLastName())) {
            errors.put("lastName", "Last name is not valid. Only letters, spaces, and standard punctuation are allowed.");
        }

        if (minorLecturer.getEmail() == null || !isValidEmail(minorLecturer.getEmail())) {
            errors.put("email", "Email is required and must be in a valid format.");
        }

        if (minorLecturer.getPhoneNumber() != null && !isValidPhoneNumber(minorLecturer.getPhoneNumber())) {
            errors.put("phoneNumber", "Invalid phone number format. Must be 10-15 digits, optionally starting with '+'.");
        }

        if (minorLecturer.getBirthDate() != null && minorLecturer.getBirthDate().isAfter(LocalDate.now())) {
            errors.put("birthDate", "Date of birth must be in the past.");
        }

        if (minorLecturer.getGender() == null) {
            errors.put("gender", "Gender is required to assign a default avatar.");
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

        if (minorLecturer.getEmail() != null) {
            if (minorLecturer.getId() != null) {
                if (personsService.existsByEmailExcludingId(minorLecturer.getEmail(), minorLecturer.getId())) {
                    errors.put("email", "The email address is already associated with another account.");
                }
            } else {
                if (personsService.existsByEmail(minorLecturer.getEmail())) {
                    errors.put("email", "The email address is already associated with another account.");
                }
            }
        }

        if (minorLecturer.getPhoneNumber() != null && personsService.existsByPhoneNumberExcludingId(minorLecturer.getPhoneNumber(), minorLecturer.getId() != null ? minorLecturer.getId() : "")) {
            errors.put("phoneNumber", "The phone number is already associated with another account.");
        }

        return errors;
    }

    @Override
    public List<MinorLecturers> searchMinorLecturers(String searchType, String keyword, int firstResult, int pageSize) {
        try {
            if (keyword == null || keyword.trim().isEmpty() || pageSize <= 0 || firstResult < 0) {
                return List.of();
            }

            String queryString = "SELECT l FROM MinorLecturers l JOIN FETCH l.campus JOIN FETCH l.creator WHERE ";

            if ("name".equalsIgnoreCase(searchType)) {
                keyword = keyword.toLowerCase().trim();
                String[] words = keyword.split("\\s+");
                StringBuilder nameCondition = new StringBuilder();
                for (int i = 0; i < words.length; i++) {
                    if (i > 0) {
                        nameCondition.append(" AND ");
                    }
                    nameCondition.append("(LOWER(l.firstName) LIKE :word").append(i).append(" OR LOWER(l.lastName) LIKE :word").append(i).append(")");
                }
                queryString += "(" + nameCondition.toString() + ")";
            } else if ("id".equalsIgnoreCase(searchType)) {
                queryString += "LOWER(l.id) = LOWER(:keyword)";
            } else {
                return List.of();
            }

            TypedQuery<MinorLecturers> query = entityManager.createQuery(queryString, MinorLecturers.class)
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

            List<MinorLecturers> minorLecturers = query.getResultList();
            logger.info("Found {} minor lecturers for search type: {}, keyword: {}", minorLecturers.size(), searchType, keyword);
            return minorLecturers;
        } catch (Exception e) {
            logger.error("Error searching minor lecturers: {}", e.getMessage(), e);
            throw new RuntimeException("Error searching minor lecturers: " + e.getMessage(), e);
        }
    }

    @Override
    public long countSearchResults(String searchType, String keyword) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return 0L;
            }

            String queryString = "SELECT COUNT(l) FROM MinorLecturers l WHERE ";

            if ("name".equalsIgnoreCase(searchType)) {
                keyword = keyword.toLowerCase().trim();
                String[] words = keyword.split("\\s+");
                StringBuilder nameCondition = new StringBuilder();
                for (int i = 0; i < words.length; i++) {
                    if (i > 0) {
                        nameCondition.append(" AND ");
                    }
                    nameCondition.append("(LOWER(l.firstName) LIKE :word").append(i).append(" OR LOWER(l.lastName) LIKE :word").append(i).append(")");
                }
                queryString += "(" + nameCondition.toString() + ")";
            } else if ("id".equalsIgnoreCase(searchType)) {
                queryString += "LOWER(l.id) = LOWER(:keyword)";
            } else {
                logger.warn("Invalid search type for count: {}", searchType);
                return 0;
            }

            TypedQuery<Long> query = entityManager.createQuery(queryString, Long.class);

            if ("name".equalsIgnoreCase(searchType)) {
                String[] words = keyword.split("\\s+");
                for (int i = 0; i < words.length; i++) {
                    query.setParameter("word" + i, "%" + words[i] + "%");
                }
            } else if ("id".equalsIgnoreCase(searchType)) {
                query.setParameter("keyword", keyword.trim());
            }

            long count = query.getSingleResult();
            logger.info("Counted {} minor lecturers for search type: {}, keyword: {}", count, searchType, keyword);
            return count;
        } catch (Exception e) {
            logger.error("Error counting search results for minor lecturers: {}", e.getMessage(), e);
            throw new RuntimeException("Error counting search results for minor lecturers: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateRandomPassword(int length) {
        if (length < 8) {
            throw new IllegalArgumentException("Password length must be at least 8 characters.");
        }
        String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String symbols = "!@#$%^&*()-_+=<>?";
        String allChars = upperCase + lowerCase + digits + symbols;
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();
        password.append(upperCase.charAt(random.nextInt(upperCase.length())));
        password.append(lowerCase.charAt(random.nextInt(lowerCase.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(symbols.charAt(random.nextInt(symbols.length())));
        for (int i = 4; i < length; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }
        List<Character> chars = password.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.toList());
        Collections.shuffle(chars, random);
        String generatedPassword = chars.stream().map(String::valueOf).collect(Collectors.joining());
        logger.info("Generated random password for minor lecturer");
        return generatedPassword;
    }

    @Override
    public String generateUniqueMinorLectureId(LocalDate createdDate) {
        String year = String.format("%02d", createdDate.getYear() % 100);
        String date = String.format("%02d%02d", createdDate.getMonthValue(), createdDate.getDayOfMonth());
        String minorLecturerId;
        SecureRandom random = new SecureRandom();
        do {
            String randomDigit = String.valueOf(random.nextInt(10));
            minorLecturerId = "ML" + year + date + randomDigit; // Prefix "ML" for Minor Lecturer
        } while (personsService.existsPersonById(minorLecturerId));
        logger.info("Generated unique minor lecturer ID: {}", minorLecturerId);
        return minorLecturerId;
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return true; // Optional, matching lecturer system
        }
        String phoneRegex = "^\\+?[1-9][0-9]{7,14}$";
        return phoneNumber.matches(phoneRegex);
    }

    private boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) return false;
        String nameRegex = "^(?=.{2,100}$)(\\p{L}+[\\p{L}'’\\-\\.]*)((\\s+\\p{L}+[\\p{L}'’\\-\\.]*)*)$";
        return name.matches(nameRegex);
    }

    private void updateMinorLecturerFields(MinorLecturers existing, MinorLecturers updated) {
        if (updated.getFirstName() != null) existing.setFirstName(updated.getFirstName());
        if (updated.getLastName() != null) existing.setLastName(updated.getLastName());
        if (updated.getEmail() != null) existing.setEmail(updated.getEmail());
        if (updated.getPhoneNumber() != null) existing.setPhoneNumber(updated.getPhoneNumber());
        if (updated.getBirthDate() != null) existing.setBirthDate(updated.getBirthDate());
        if (updated.getGender() != null) existing.setGender(updated.getGender());
        if (updated.getCountry() != null) existing.setCountry(updated.getCountry());
        if (updated.getProvince() != null) existing.setProvince(updated.getProvince());
        if (updated.getCity() != null) existing.setCity(updated.getCity());
        if (updated.getDistrict() != null) existing.setDistrict(updated.getDistrict());
        if (updated.getWard() != null) existing.setWard(updated.getWard());
        if (updated.getStreet() != null) existing.setStreet(updated.getStreet());
        if (updated.getPostalCode() != null) existing.setPostalCode(updated.getPostalCode());
        if (updated.getAvatar() != null) existing.setAvatar(updated.getAvatar());
        if (updated.getEmploymentTypes() != null) existing.setEmploymentTypes(updated.getEmploymentTypes());
        if (updated.getCreator() != null) existing.setCreator(updated.getCreator());
    }
}