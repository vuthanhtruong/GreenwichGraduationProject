package com.example.demo.user.majorLecturer.dao;

import com.example.demo.authenticator.service.AuthenticatorsService;
import com.example.demo.email_service.dto.LecturerEmailContext;
import com.example.demo.email_service.service.EmailServiceForLecturerService;
import com.example.demo.email_service.service.EmailServiceForStudentService;
import com.example.demo.user.majorLecturer.model.MajorLecturers;
import com.example.demo.user.minorLecturer.model.MinorLecturers;
import com.example.demo.major.model.Majors;
import com.example.demo.user.person.model.Persons;
import com.example.demo.security.model.CustomOidcUserPrincipal;
import com.example.demo.security.model.DatabaseUserPrincipal;
import com.example.demo.user.staff.model.Staffs;
import com.example.demo.user.staff.service.StaffsService;
import com.example.demo.user.person.service.PersonsService;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class MajorLecturersDAOImpl implements MajorLecturersDAO {
    // === THÊM: countSearchMajorLecturersByCampus ===
    @Override
    public long countSearchMajorLecturersByCampus(String campusId, String searchType, String keyword) {
        if (campusId == null || campusId.trim().isEmpty() || keyword == null || keyword.trim().isEmpty()) {
            return 0L;
        }

        String queryString = "SELECT COUNT(l) FROM MajorLecturers l WHERE l.campus.id = :campusId";

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

        return query.getSingleResult();
    }

    // === THÊM: getColleaguesByMajor ===
    @Override
    public List<MajorLecturers> getColleaguesByMajor(String majorId) {
        if (majorId == null || majorId.trim().isEmpty()) return List.of();

        return entityManager.createQuery(
                        "SELECT l FROM MajorLecturers l " +
                                "JOIN FETCH l.majorManagement m " +
                                "WHERE m.majorId = :majorId " +
                                "AND l.id != :currentId", MajorLecturers.class)
                .setParameter("majorId", majorId)
                .setParameter("currentId", getMajorLecturer().getId())
                .getResultList();
    }

    @Override
    public List<MajorLecturers> colleagueBycampusId(String campusId) {
        return entityManager.createQuery("from MajorLecturers s where s.campus.id=:campusId And s.id!=:id", MajorLecturers.class).setParameter("campusId", campusId).
                setParameter("id", getMajorLecturer().getId()).getResultList();
    }

    private static final Logger logger = LoggerFactory.getLogger(MajorLecturersDAOImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    private final PersonsService personsService;
    private final StaffsService staffsService;
    private final EmailServiceForLecturerService emailServiceForLectureService;

    public MajorLecturersDAOImpl(PersonsService personsService,
                                 EmailServiceForLecturerService emailServiceForLectureService,
                                 EmailServiceForStudentService emailServiceForStudentService,
                                 StaffsService staffsService,
                                 AuthenticatorsService authenticatorsService) {
        this.personsService = personsService;
        if (emailServiceForLectureService == null || emailServiceForStudentService == null) {
            throw new IllegalArgumentException("Email services cannot be null");
        }
        this.emailServiceForLectureService = emailServiceForLectureService;
        this.staffsService = staffsService;
    }

    @Override
    public MajorLecturers getMajorLecturer() {
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

        return entityManager.createQuery(
                        "SELECT l FROM MajorLecturers l " +
                                "JOIN FETCH l.majorManagement m " +
                                "JOIN FETCH l.campus c " +
                                "WHERE l.id = :id", MajorLecturers.class)
                .setParameter("id", person.getId())
                .getSingleResult();
    }

    @Override
    public long minorLecturersCountByCampus(String campusId) {
        if (campusId == null || campusId.trim().isEmpty()) {
            throw new IllegalArgumentException("Campus ID must not be null or empty");
        }
        String jpql = "SELECT COUNT(l) FROM MinorLecturers l WHERE l.campus.id = :campusId";
        return entityManager.createQuery(jpql, Long.class)
                .setParameter("campusId", campusId)
                .getSingleResult();
    }

    @Override
    public long lecturersCountByCampus(String campusId) {
        if (campusId == null || campusId.trim().isEmpty()) {
            throw new IllegalArgumentException("Campus ID must not be null or empty");
        }
        String jpql = "SELECT COUNT(l) FROM MajorLecturers l WHERE l.campus.id = :campusId";
        return entityManager.createQuery(jpql, Long.class)
                .setParameter("campusId", campusId)
                .getSingleResult();
    }

    @Override
    public String generateRandomPassword(int length) {
        if (length < 8) {
            throw new IllegalArgumentException("Password length must be at least 8 characters for security.");
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
        return chars.stream().map(String::valueOf).collect(Collectors.joining());
    }

    @Override
    public String generateUniqueLectureId(String majorId, LocalDate createdDate) {
        String prefix = majorId != null ? majorId : "TGN";
        String year = String.format("%02d", createdDate.getYear() % 100);
        String date = String.format("%02d%02d", createdDate.getMonthValue(), createdDate.getDayOfMonth());
        String lectureId;
        SecureRandom random = new SecureRandom();
        do {
            String randomDigit = String.valueOf(random.nextInt(10));
            lectureId = prefix + year + date + randomDigit;
        } while (personsService.existsPersonById(lectureId));
        return lectureId;
    }

    @Override
    public Map<String, String> lectureValidation(MajorLecturers lecturer, MultipartFile avatarFile) {
        Map<String, String> errors = new HashMap<>();

        if (lecturer.getFirstName() == null || !isValidName(lecturer.getFirstName())) {
            errors.put("firstName", "First name is not valid. Only letters, spaces, and standard punctuation are allowed.");
        }
        if (lecturer.getLastName() == null || !isValidName(lecturer.getLastName())) {
            errors.put("lastName", "Last name is not valid. Only letters, spaces, and standard punctuation are allowed.");
        }
        if (lecturer.getEmail() != null && !isValidEmail(lecturer.getEmail())) {
            errors.put("email", "Invalid email format.");
        }
        if (lecturer.getPhoneNumber() != null && !isValidPhoneNumber(lecturer.getPhoneNumber())) {
            errors.put("phoneNumber", "Invalid phone number format. Must be 10-15 digits, optionally starting with '+'.");
        }
        if (lecturer.getBirthDate() != null && lecturer.getBirthDate().isAfter(LocalDate.now())) {
            errors.put("birthDate", "Date of birth must be in the past.");
        }
        if (lecturer.getGender() == null) {
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

        if (lecturer.getEmail() != null) {
            String existingId = lecturer.getId() != null ? lecturer.getId() : "";
            if (personsService.existsByEmailExcludingId(lecturer.getEmail(), existingId)) {
                errors.put("email", "The email address is already associated with another account.");
            }
        }

        if (lecturer.getPhoneNumber() != null &&
                personsService.existsByPhoneNumberExcludingId(lecturer.getPhoneNumber(), lecturer.getId() != null ? lecturer.getId() : "")) {
            errors.put("phoneNumber", "The phone number is already associated with another account.");
        }

        return errors;
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) return true;
        String phoneRegex = "^\\+?[1-9][0-9]{7,14}$";
        return phoneNumber.matches(phoneRegex);
    }

    private boolean isValidName(String name) {
        if (name == null) return false;
        name = name.trim();
        if (name.isEmpty()) return false;
        String nameRegex = "^(?=.{2,100}$)(\\p{L}+[\\p{L}'’\\-\\.]*)((\\s+\\p{L}+[\\p{L}'’\\-\\.]*)*)$";
        return name.matches(nameRegex);
    }

    @Override
    public List<MajorLecturers> getLecturers() {
        return entityManager.createQuery("FROM MajorLecturers l", MajorLecturers.class).getResultList();
    }

    @Override
    public MajorLecturers addLecturers(MajorLecturers lecturer, String randomPassword) {
        Staffs staff = staffsService.getStaff();
        if (staff == null) throw new IllegalStateException("No authenticated staff found");

        lecturer.setCampus(staff.getCampus());
        lecturer.setMajorManagement(staff.getMajorManagement());
        lecturer.setCreator(staff);
        lecturer.setCreatedDate(LocalDate.now());
        MajorLecturers savedLecturer = entityManager.merge(lecturer);

        LecturerEmailContext context = new LecturerEmailContext(
                savedLecturer.getId(),
                savedLecturer.getFullName(),
                savedLecturer.getEmail(),
                savedLecturer.getPhoneNumber(),
                savedLecturer.getBirthDate(),
                savedLecturer.getGender() != null ? savedLecturer.getGender().toString() : null,
                savedLecturer.getFullAddress(),
                savedLecturer.getCampus() != null ? savedLecturer.getCampus().getCampusName() : null,
                savedLecturer.getMajorManagement() != null ? savedLecturer.getMajorManagement().getMajorName() : null,
                savedLecturer.getCreator() != null ? savedLecturer.getCreator().getFullName() : null,
                savedLecturer.getCreatedDate()
        );

        try {
            String subject = "Your Lecturer Account Information";
            emailServiceForLectureService.sendEmailToNotifyLoginInformation(savedLecturer.getEmail(), subject, context, randomPassword);
        } catch (Exception e) {
            logger.error("Failed to schedule email to {}: {}", savedLecturer.getEmail(), e.getMessage());
        }
        return savedLecturer;
    }

    @Override
    public long numberOfLecturersByCampus(String campusId) {
        if (campusId == null || campusId.trim().isEmpty()) return 0L;
        return (Long) entityManager.createQuery(
                        "SELECT COUNT(l) FROM MajorLecturers l WHERE l.campus.id = :campusId")
                .setParameter("campusId", campusId)
                .getSingleResult();
    }

    @Override
    @Transactional
    public void deleteLecturer(String id) {
        MajorLecturers lecturer = entityManager.find(MajorLecturers.class, id);
        if (lecturer == null) {
            throw new IllegalArgumentException("Lecturer with ID " + id + " not found");
        }

        entityManager.createQuery(
                        "DELETE FROM MajorLecturers_MajorClasses mlmc WHERE mlmc.id.lecturerId = :lecturerId")
                .setParameter("lecturerId", id)
                .executeUpdate();

        entityManager.createQuery(
                        "DELETE FROM MajorLecturers_SpecializedClasses mlsc WHERE mlsc.id.lecturerId = :lecturerId")
                .setParameter("lecturerId", id)
                .executeUpdate();

        entityManager.createQuery(
                        "DELETE FROM MajorLecturers_Specializations mls WHERE mls.id.lecturerId = :lecturerId")
                .setParameter("lecturerId", id)
                .executeUpdate();

        entityManager.remove(lecturer);
    }

    @Override
    public void updateLecturer(String id, MajorLecturers lecturer, MultipartFile avatarFile) throws MessagingException, IOException {
        MajorLecturers existingLecturer = entityManager.find(MajorLecturers.class, id);
        if (existingLecturer == null) {
            throw new IllegalArgumentException("Lecturer with ID " + id + " not found");
        }

        if (avatarFile != null && !avatarFile.isEmpty()) {
            lecturer.setAvatar(avatarFile.getBytes());
        } else {
            lecturer.setAvatar(existingLecturer.getAvatar());
        }

        updateLecturerFields(existingLecturer, lecturer);
        entityManager.merge(existingLecturer);

        LecturerEmailContext context = new LecturerEmailContext(
                existingLecturer.getId(),
                existingLecturer.getFullName(),
                existingLecturer.getEmail(),
                existingLecturer.getPhoneNumber(),
                existingLecturer.getBirthDate(),
                existingLecturer.getGender() != null ? existingLecturer.getGender().toString() : null,
                existingLecturer.getFullAddress(),
                existingLecturer.getCampus() != null ? existingLecturer.getCampus().getCampusName() : null,
                existingLecturer.getMajorManagement() != null ? existingLecturer.getMajorManagement().getMajorName() : null,
                existingLecturer.getCreator() != null ? existingLecturer.getCreator().getFullName() : null,
                existingLecturer.getCreatedDate()
        );

        String subject = "Your lecturer account information after being edited";
        emailServiceForLectureService.sendEmailToNotifyInformationAfterEditing(existingLecturer.getEmail(), subject, context);
    }

    @Override
    public MajorLecturers getLecturerById(String id) {
        return entityManager.find(MajorLecturers.class, id);
    }

    @Override
    public List<MajorLecturers> getPaginatedLecturersByCampus(String campusId, int firstResult, int pageSize) {
        if (campusId == null || campusId.trim().isEmpty()) return List.of();
        return entityManager.createQuery(
                        "SELECT l FROM MajorLecturers l WHERE l.campus.id = :campusId", MajorLecturers.class)
                .setParameter("campusId", campusId)
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public List<MajorLecturers> searchLecturersByCampus(String campusId, String searchType, String keyword, int firstResult, int pageSize) {
        if (campusId == null || campusId.trim().isEmpty() || keyword == null || keyword.trim().isEmpty() || pageSize <= 0) {
            return List.of();
        }

        String queryString = "SELECT l FROM MajorLecturers l JOIN FETCH l.campus JOIN FETCH l.majorManagement JOIN FETCH l.creator " +
                "WHERE l.campus.id = :campusId";

        if ("name".equalsIgnoreCase(searchType)) {
            keyword = keyword.toLowerCase().trim();
            String[] words = keyword.split("\\s+");
            StringBuilder nameCondition = new StringBuilder();
            for (int i = 0; i < words.length; i++) {
                if (i > 0) nameCondition.append(" AND ");
                nameCondition.append("(LOWER(l.firstName) LIKE :word").append(i).append(" OR LOWER(l.lastName) LIKE :word").append(i).append(")");
            }
            queryString += " AND (" + nameCondition.toString() + ")";
        } else if ("id".equalsIgnoreCase(searchType)) {
            queryString += " AND LOWER(l.id) = LOWER(:keyword)";
        } else {
            return List.of();
        }

        TypedQuery<MajorLecturers> query = entityManager.createQuery(queryString, MajorLecturers.class)
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

        return query.getResultList();
    }

    @Override
    public long countSearchResultsByCampus(String campusId, String searchType, String keyword) {
        if (campusId == null || campusId.trim().isEmpty() || keyword == null || keyword.trim().isEmpty()) {
            return 0L;
        }

        String queryString = "SELECT COUNT(l) FROM MajorLecturers l WHERE l.campus.id = :campusId";

        if ("name".equalsIgnoreCase(searchType)) {
            keyword = keyword.toLowerCase().trim();
            String[] words = keyword.split("\\s+");
            StringBuilder nameCondition = new StringBuilder();
            for (int i = 0; i < words.length; i++) {
                if (i > 0) nameCondition.append(" AND ");
                nameCondition.append("(LOWER(l.firstName) LIKE :word").append(i).append(" OR LOWER(l.lastName) LIKE :word").append(i).append(")");
            }
            queryString += " AND (" + nameCondition.toString() + ")";
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

        return query.getSingleResult();
    }

    @Override
    public long countLecturersByCampus(String campusId) {
        if (campusId == null || campusId.trim().isEmpty()) {
            throw new IllegalArgumentException("Campus ID must not be null or empty");
        }
        return entityManager.createQuery("SELECT COUNT(l) FROM MajorLecturers l WHERE l.campus.id = :campusId", Long.class)
                .setParameter("campusId", campusId)
                .getSingleResult();
    }

    @Override
    public List<MinorLecturers> getPaginatedMinorLecturersByCampus(String campusId, int firstResult, int pageSize) {
        if (campusId == null || campusId.trim().isEmpty()) {
            throw new IllegalArgumentException("Campus ID must not be null or empty");
        }
        return entityManager.createQuery("SELECT l FROM MinorLecturers l JOIN FETCH l.campus WHERE l.campus.id = :campusId", MinorLecturers.class)
                .setParameter("campusId", campusId)
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public MinorLecturers getMinorLecturerById(String id) {
        return entityManager.find(MinorLecturers.class, id);
    }

    @Override
    public List<MajorLecturers> searchMajorLecturersByCampus(String campusId, String searchType, String keyword, int firstResult, int pageSize) {
        return searchLecturersByCampus(campusId, searchType, keyword, firstResult, pageSize);
    }

    @Override
    public List<MinorLecturers> searchMinorLecturersByCampus(String campusId, String searchType, String keyword, int firstResult, int pageSize) {
        if (campusId == null || campusId.trim().isEmpty() || keyword == null || keyword.trim().isEmpty() || pageSize <= 0) {
            return List.of();
        }

        String queryString = "SELECT l FROM MinorLecturers l JOIN FETCH l.campus WHERE l.campus.id = :campusId";

        if ("name".equalsIgnoreCase(searchType)) {
            keyword = keyword.toLowerCase().trim();
            String[] words = keyword.split("\\s+");
            StringBuilder nameCondition = new StringBuilder();
            for (int i = 0; i < words.length; i++) {
                if (i > 0) nameCondition.append(" AND ");
                nameCondition.append("(LOWER(l.firstName) LIKE :word").append(i).append(" OR LOWER(l.lastName) LIKE :word").append(i).append(")");
            }
            queryString += " AND (" + nameCondition.toString() + ")";
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

        return query.getResultList();
    }

    @Override
    public long countMajorLecturersSearchResultsByCampus(String campusId, String searchType, String keyword) {
        return countSearchResultsByCampus(campusId, searchType, keyword);
    }

    @Override
    public long countMinorLecturersSearchResultsByCampus(String campusId, String searchType, String keyword) {
        if (campusId == null || campusId.trim().isEmpty() || keyword == null || keyword.trim().isEmpty()) {
            return 0L;
        }

        String queryString = "SELECT COUNT(l) FROM MinorLecturers l WHERE l.campus.id = :campusId";

        if ("name".equalsIgnoreCase(searchType)) {
            keyword = keyword.toLowerCase().trim();
            String[] words = keyword.split("\\s+");
            StringBuilder nameCondition = new StringBuilder();
            for (int i = 0; i < words.length; i++) {
                if (i > 0) nameCondition.append(" AND ");
                nameCondition.append("(LOWER(l.firstName) LIKE :word").append(i).append(" OR LOWER(l.lastName) LIKE :word").append(i).append(")");
            }
            queryString += " AND (" + nameCondition.toString() + ")";
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

        return query.getSingleResult();
    }

    private void updateLecturerFields(MajorLecturers existing, MajorLecturers updated) {
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
        if (updated.getCampus() != null) existing.setCampus(updated.getCampus());
        if (updated.getCreator() != null) existing.setCreator(updated.getCreator());
        if (updated.getAvatar() != null) existing.setAvatar(updated.getAvatar());
    }
}