package com.example.demo.lecturer.dao;

import com.example.demo.authenticator.service.AuthenticatorsService;
import com.example.demo.email_service.service.EmailServiceForLectureService;
import com.example.demo.email_service.service.EmailServiceForStudentService;
import com.example.demo.lecturer.model.MajorLecturers;
import com.example.demo.major.model.Majors;
import com.example.demo.Staff.model.Staffs;
import com.example.demo.Staff.service.StaffsService;
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
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Transactional
public class LecturesDAOImpl implements LecturesDAO {
    private static final Logger logger = LoggerFactory.getLogger(LecturesDAOImpl.class);

    private final PersonsService personsService;
    private final StaffsService staffsService;
    private final EmailServiceForLectureService emailServiceForLectureService;
    private final EmailServiceForStudentService emailServiceForStudentService;
    private final AuthenticatorsService authenticatorsService;

    @PersistenceContext
    private EntityManager entityManager;

    public LecturesDAOImpl(PersonsService personsService, EmailServiceForLectureService emailServiceForLectureService,
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
    public List<String> lectureValidation(MajorLecturers lecturer, MultipartFile avatarFile) {
        List<String> errors = new ArrayList<>();
        if (!isValidName(lecturer.getFirstName())) {
            errors.add("First name is not valid. Only letters, spaces, and standard punctuation are allowed.");
        }
        if (!isValidName(lecturer.getLastName())) {
            errors.add("Last name is not valid. Only letters, spaces, and standard punctuation are allowed.");
        }
        if (lecturer.getEmail() != null && !isValidEmail(lecturer.getEmail())) {
            errors.add("Invalid email format.");
        }
        if (lecturer.getPhoneNumber() != null && !isValidPhoneNumber(lecturer.getPhoneNumber())) {
            errors.add("Invalid phone number format.");
        }
        if (lecturer.getBirthDate() != null && lecturer.getBirthDate().isAfter(LocalDate.now())) {
            errors.add("Date of birth must be in the past.");
        }
        if (lecturer.getEmail() != null && personsService.existsByEmailExcludingId(lecturer.getEmail(), lecturer.getId())) {
            errors.add("The email address is already associated with another account.");
        }
        if (lecturer.getPhoneNumber() != null && personsService.existsByPhoneNumberExcludingId(lecturer.getPhoneNumber(),lecturer.getId())) {
            errors.add("The phone number is already associated with another account.");
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

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email != null && email.matches(emailRegex);
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        String phoneRegex = "^\\+?[0-9]{10,15}$";
        return phoneNumber != null && phoneNumber.matches(phoneRegex);
    }

    private boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        String nameRegex = "^[\\p{L}][\\p{L} .'-]{1,49}$";
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
        MajorLecturers savedLecturer = entityManager.merge(lecturer);
        try {
            String subject = "Your Lecturer Account Information";
            emailServiceForLectureService.sendEmailToNotifyLoginInformation(lecturer.getEmail(), subject, lecturer, randomPassword);
        } catch (Exception e) {
            logger.error("Failed to schedule email to {}: {}", lecturer.getEmail(), e.getMessage());
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
        entityManager.merge(existingLecturer);
        String subject = "Your lecturer account information after being edited";
        emailServiceForLectureService.sendEmailToNotifyInformationAfterEditing(existingLecturer.getEmail(), subject, existingLecturer);
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
                        "SELECT s FROM MajorLecturers s WHERE s.majorManagement = :staffmajor and s.campus=:campuses", MajorLecturers.class)
                .setParameter("staffmajor", majors)
                .setParameter("campuses", staff.getCampus())
                .setMaxResults(pageSize)
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