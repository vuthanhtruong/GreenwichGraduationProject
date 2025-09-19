package com.example.demo.lecturer.dao;

import com.example.demo.authenticator.service.AuthenticatorsService;
import com.example.demo.email_service.dto.LecturerEmailContext;
import com.example.demo.email_service.service.EmailServiceForLecturerService;
import com.example.demo.email_service.service.EmailServiceForStudentService;
import com.example.demo.lecturer.model.MajorLecturers;
import com.example.demo.major.model.Majors;
import com.example.demo.staff.model.Staffs;
import com.example.demo.staff.service.StaffsService;
import com.example.demo.person.service.PersonsService;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Transactional
public class LecturesDAOImpl implements LecturesDAO {
    private static final Logger logger = LoggerFactory.getLogger(LecturesDAOImpl.class);

    private final PersonsService personsService;
    private final StaffsService staffsService;
    private final EmailServiceForLecturerService emailServiceForLectureService;
    private final EmailServiceForStudentService emailServiceForStudentService;
    private final AuthenticatorsService authenticatorsService;

    @PersistenceContext
    private EntityManager entityManager;

    // Base directory and URL for avatar storage
    private static final String AVATAR_STORAGE_PATH = "avatars/";
    private static final String AVATAR_BASE_URL = "https://university.example.com/avatars/";

    public LecturesDAOImpl(PersonsService personsService, EmailServiceForLecturerService emailServiceForLectureService,
                           EmailServiceForStudentService emailServiceForStudentService,
                           StaffsService staffsService, AuthenticatorsService authenticatorsService) {
        this.personsService = personsService;
        this.authenticatorsService = authenticatorsService;
        if (emailServiceForLectureService == null || emailServiceForStudentService == null) {
            throw new IllegalArgumentException("Email services cannot be null");
        }
        this.emailServiceForLectureService = emailServiceForLectureService;
        this.emailServiceForStudentService = emailServiceForStudentService;
        this.staffsService = staffsService;

        // Ensure avatar storage directory exists
        try {
            Files.createDirectories(Paths.get(AVATAR_STORAGE_PATH));
        } catch (IOException e) {
            throw new RuntimeException("Failed to create avatar storage directory: " + AVATAR_STORAGE_PATH, e);
        }
    }

    private String saveAvatarAndGetPath(String lecturerId, byte[] avatarData) throws IOException {
        if (avatarData == null || avatarData.length == 0) {
            return null;
        }
        String fileName = lecturerId + "_" + System.currentTimeMillis() + ".jpg";
        Path filePath = Paths.get(AVATAR_STORAGE_PATH, fileName);
        Files.write(filePath, avatarData);
        return AVATAR_BASE_URL + fileName;
    }

    @Override
    public long minorLecturersCountByCampus(String campus) {
        if (campus == null || campus.trim().isEmpty()) {
            throw new IllegalArgumentException("Campus name must not be null or empty");
        }

        String jpql = "SELECT COUNT(l) FROM MinorLecturers l WHERE l.campus.id = :campusName";
        return entityManager.createQuery(jpql, Long.class)
                .setParameter("campusName", campus)
                .getSingleResult();
    }

    @Override
    public long lecturersCountByCampus(String campus) {
        String jpql = "SELECT COUNT(l) FROM MajorLecturers l WHERE l.campus.id = :campusName";
        return entityManager.createQuery(jpql, Long.class)
                .setParameter("campusName", campus)
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

        // Validate first name
        if (lecturer.getFirstName() == null || !isValidName(lecturer.getFirstName())) {
            errors.put("firstName", "First name is not valid. Only letters, spaces, and standard punctuation are allowed.");
        }

        // Validate last name
        if (lecturer.getLastName() == null || !isValidName(lecturer.getLastName())) {
            errors.put("lastName", "Last name is not valid. Only letters, spaces, and standard punctuation are allowed.");
        }

        // Validate email format
        if (lecturer.getEmail() != null && !isValidEmail(lecturer.getEmail())) {
            errors.put("email", "Invalid email format.");
        }

        // Validate phone number format
        if (lecturer.getPhoneNumber() != null && !isValidPhoneNumber(lecturer.getPhoneNumber())) {
            errors.put("phoneNumber", "Invalid phone number format. Must be 10-15 digits, optionally starting with '+'.");
        }

        // Validate birth date
        if (lecturer.getBirthDate() != null && lecturer.getBirthDate().isAfter(LocalDate.now())) {
            errors.put("birthDate", "Date of birth must be in the past.");
        }

        // Gender required
        if (lecturer.getGender() == null) {
            errors.put("gender", "Gender is required to assign a default avatar.");
        }

        // Validate avatar
        if (avatarFile != null && !avatarFile.isEmpty()) {
            String contentType = avatarFile.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                errors.put("avatarFile", "Avatar must be an image file.");
            }
            if (avatarFile.getSize() > 5 * 1024 * 1024) {
                errors.put("avatarFile", "Avatar file size must not exceed 5MB.");
            }
        }

        // Duplicate email check
        if (lecturer.getEmail() != null) {
            if (lecturer.getId() != null) {
                if (personsService.existsByEmailExcludingId(lecturer.getEmail(), lecturer.getId())) {
                    errors.put("email", "The email address is already associated with another account.");
                }
            } else {
                if (personsService.existsByEmail(lecturer.getEmail())) {
                    errors.put("email", "The email address is already associated with another account.");
                }
            }
        }

        // Duplicate phone number check
        if (lecturer.getPhoneNumber() != null &&
                personsService.existsByPhoneNumberExcludingId(lecturer.getPhoneNumber(), lecturer.getId() != null ? lecturer.getId() : "")) {
            errors.put("phoneNumber", "The phone number is already associated with another account.");
        }

        return errors;
    }


    /**
     * Validate email theo chuẩn RFC 5322 (gọn gàng, thực tế).
     * - Cho phép tên miền có dấu gạch ngang.
     * - Cho phép TLD >= 2 ký tự.
     * - Không chấp nhận ký tự đặc biệt lạ.
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String emailRegex =
                "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    /**
     * Validate số điện thoại quốc tế.
     * - Cho phép bắt đầu bằng +.
     * - Độ dài 8–15 số (theo chuẩn E.164).
     * - Không chứa khoảng trắng, dấu - hoặc ký hiệu khác.
     */
    private boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return true; // cho phép bỏ trống
        }
        String phoneRegex = "^\\+?[1-9][0-9]{7,14}$";
        return phoneNumber.matches(phoneRegex);
    }

    /**
     * Validate tên quốc tế thông minh.
     * - Hỗ trợ Unicode: tiếng Việt, Nhật, Ả Rập, Nga, Hy Lạp, …
     * - Cho phép khoảng trắng phân tách nhiều từ.
     * - Cho phép dấu gạch nối (-), nháy (’ '), chấm (.)
     * - Không cho phép số, emoji, ký tự lạ.
     * - Độ dài 2–100 ký tự.
     */
    private boolean isValidName(String name) {
        if (name == null) return false;
        name = name.trim();
        if (name.isEmpty()) return false;

        String nameRegex =
                "^(?=.{2,100}$)(\\p{L}+[\\p{L}'’\\-\\.]*)((\\s+\\p{L}+[\\p{L}'’\\-\\.]*)*)$";
        return name.matches(nameRegex);
    }



    @Override
    public List<MajorLecturers> getLecturers() {
        return entityManager.createQuery("FROM MajorLecturers l", MajorLecturers.class).getResultList();
    }

    @Override
    public MajorLecturers addLecturers(MajorLecturers lecturer, String randomPassword) {
        Staffs staff = staffsService.getStaff();
        lecturer.setCampus(staff.getCampus());
        lecturer.setMajorManagement(staff.getMajorManagement());
        lecturer.setCreator(staff);
        lecturer.setCreatedDate(LocalDate.now());
        MajorLecturers savedLecturer = entityManager.merge(lecturer);

        // Handle avatar
        String avatarPath = null;
        try {
            avatarPath = saveAvatarAndGetPath(savedLecturer.getId(), savedLecturer.getAvatar());
        } catch (IOException e) {
            logger.error("Failed to save avatar for lecturer {}: {}", savedLecturer.getId(), e.getMessage());
        }

        // Create LecturerEmailContext
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
                savedLecturer.getCreatedDate(),
                avatarPath
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
    public long numberOfLecturers() {
        Staffs staff = staffsService.getStaff();
        if (staff == null) {
            throw new IllegalArgumentException("Staff not found");
        }
        return (Long) entityManager.createQuery(
                        "SELECT COUNT(l) FROM MajorLecturers l WHERE l.majorManagement.id = :staffmajor")
                .setParameter("staffmajor", staff.getMajorManagement().getMajorId())
                .getSingleResult();
    }

    @Override
    public void deleteLecturer(String id) {
        MajorLecturers lecturer = entityManager.find(MajorLecturers.class, id);
        if (lecturer == null) {
            throw new IllegalArgumentException("Lecturer with ID " + id + " not found");
        }
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

        // Handle avatar
        String avatarPath = null;
        try {
            avatarPath = saveAvatarAndGetPath(existingLecturer.getId(), existingLecturer.getAvatar());
        } catch (IOException e) {
            logger.error("Failed to save avatar for lecturer {}: {}", existingLecturer.getId(), e.getMessage());
        }

        entityManager.merge(existingLecturer);

        // Create LecturerEmailContext
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
                existingLecturer.getCreatedDate(),
                avatarPath
        );

        String subject = "Your lecturer account information after being edited";
        emailServiceForLectureService.sendEmailToNotifyInformationAfterEditing(existingLecturer.getEmail(), subject, context);
    }

    @Override
    public MajorLecturers getLecturerById(String id) {
        return entityManager.find(MajorLecturers.class, id);
    }

    @Override
    public List<MajorLecturers> getPaginatedLecturers(int firstResult, int pageSize) {
        Staffs staff = staffsService.getStaff();
        Majors majors = staff.getMajorManagement();
        return entityManager.createQuery(
                        "SELECT s FROM MajorLecturers s WHERE s.majorManagement = :staffmajor AND s.campus = :campuses", MajorLecturers.class)
                .setParameter("staffmajor", majors)
                .setParameter("campuses", staff.getCampus())
                .setMaxResults(pageSize)
                .setFirstResult(firstResult)
                .getResultList();
    }

    private void validateLecturer(MajorLecturers lecturer) {
        if (lecturer.getEmail() == null || lecturer.getPhoneNumber() == null) {
            throw new IllegalArgumentException("Email and phone number are required");
        }
    }

    @Override
    public List<MajorLecturers> searchLecturers(String searchType, String keyword, int firstResult, int pageSize) {
        String queryString = "SELECT l FROM MajorLecturers l JOIN FETCH l.campus JOIN FETCH l.majorManagement JOIN FETCH l.creator WHERE ";
        if ("name".equals(searchType)) {
            queryString += "LOWER(l.firstName) LIKE LOWER(:keyword) OR LOWER(l.lastName) LIKE LOWER(:keyword)";
        } else {
            queryString += "l.id LIKE :keyword";
        }
        return entityManager.createQuery(queryString, MajorLecturers.class)
                .setParameter("keyword", "%" + keyword + "%")
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public long countSearchResults(String searchType, String keyword) {
        String queryString = "SELECT COUNT(l) FROM MajorLecturers l WHERE ";
        if ("name".equals(searchType)) {
            queryString += "LOWER(l.firstName) LIKE LOWER(:keyword) OR LOWER(l.lastName) LIKE LOWER(:keyword)";
        } else {
            queryString += "l.id LIKE :keyword";
        }
        return entityManager.createQuery(queryString, Long.class)
                .setParameter("keyword", "%" + keyword + "%")
                .getSingleResult();
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