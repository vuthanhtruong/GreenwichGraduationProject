package com.example.demo.student.dao;

import com.example.demo.accountBalance.service.AccountBalancesService;
import com.example.demo.authenticator.service.AuthenticatorsService;
import com.example.demo.email_service.dto.StudentEmailContext;
import com.example.demo.email_service.service.EmailServiceForLecturerService;
import com.example.demo.email_service.service.EmailServiceForStudentService;
import com.example.demo.major.model.Majors;
import com.example.demo.person.model.Persons;
import com.example.demo.security.model.DatabaseUserPrincipal;
import com.example.demo.security.model.OAuth2UserPrincipal;
import com.example.demo.staff.model.Staffs;
import com.example.demo.staff.service.StaffsService;
import com.example.demo.person.service.PersonsService;
import com.example.demo.student.model.Students;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Transactional
public class StudentDAOImpl implements StudentsDAO {
    @Override
    public Students findById(String studentId) {
        return entityManager.find(Students.class, studentId);
    }

    @Override
    public long totalStudentsByCampus(String campusId) {
        if (campusId == null || campusId.trim().isEmpty()) {
            throw new IllegalArgumentException("Campus ID must not be null or empty");
        }

        String jpql = "SELECT COUNT(s) FROM Students s WHERE s.campus.id = :campusId";
        return entityManager.createQuery(jpql, Long.class)
                .setParameter("campusId", campusId)
                .getSingleResult();
    }


    private final StaffsService staffsService;
    private final PersonsService personsService;
    @PersistenceContext
    private EntityManager entityManager;
    private final EmailServiceForStudentService emailServiceForStudentService;
    private final EmailServiceForLecturerService emailServiceForLectureService;
    private final AccountBalancesService accountBalancesService;
    private final AuthenticatorsService authenticatorsService;

    // Base directory and URL for avatar storage
    private static final String AVATAR_STORAGE_PATH = "avatars/";
    private static final String AVATAR_BASE_URL = "https://university.example.com/avatars/";

    public StudentDAOImpl(PersonsService personsService, EmailServiceForStudentService emailServiceForStudentService,
                          EmailServiceForLecturerService emailServiceForLectureService,
                          AccountBalancesService accountBalancesService,
                          StaffsService staffsService, AuthenticatorsService authenticatorsService) {
        this.personsService = personsService;
        this.accountBalancesService = accountBalancesService;
        this.authenticatorsService = authenticatorsService;
        if (emailServiceForStudentService == null || emailServiceForLectureService == null) {
            throw new IllegalArgumentException("Email services cannot be null");
        }
        this.emailServiceForStudentService = emailServiceForStudentService;
        this.emailServiceForLectureService = emailServiceForLectureService;
        this.staffsService = staffsService;

        // Ensure avatar storage directory exists
        try {
            Files.createDirectories(Paths.get(AVATAR_STORAGE_PATH));
        } catch (IOException e) {
            throw new RuntimeException("Failed to create avatar storage directory: " + AVATAR_STORAGE_PATH, e);
        }
    }

    private String saveAvatarAndGetPath(String studentId, byte[] avatarData) throws IOException {
        if (avatarData == null || avatarData.length == 0) {
            return null;
        }
        String fileName = studentId + "_" + System.currentTimeMillis() + ".jpg";
        Path filePath = Paths.get(AVATAR_STORAGE_PATH, fileName);
        Files.write(filePath, avatarData);
        return AVATAR_BASE_URL + fileName;
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
        return chars.stream()
                .map(String::valueOf)
                .collect(Collectors.joining());
    }

    @Override
    public String generateUniqueStudentId(String majorId, LocalDate createdDate) {
        String prefix = majorId != null ? majorId : "GEN";
        String year = String.format("%02d", createdDate.getYear() % 100);
        String date = String.format("%02d%02d", createdDate.getMonthValue(), createdDate.getDayOfMonth());
        String studentId;
        SecureRandom random = new SecureRandom();
        do {
            String randomDigit = String.valueOf(random.nextInt(10));
            studentId = prefix + year + date + randomDigit;
        } while (personsService.existsPersonById(studentId));
        return studentId;
    }

    @Override
    public List<String> StudentValidation(Students student, MultipartFile avatarFile) {
        List<String> errors = new ArrayList<>();
        if (student.getFirstName() == null || !isValidName(student.getFirstName())) {
            errors.add("First name is not valid. Only letters, spaces, and standard punctuation are allowed.");
        }
        if (student.getLastName() == null || !isValidName(student.getLastName())) {
            errors.add("Last name is not valid. Only letters, spaces, and standard punctuation are allowed.");
        }
        if (student.getEmail() != null && !isValidEmail(student.getEmail())) {
            errors.add("Invalid email format.");
        }
        if (student.getPhoneNumber() != null && !isValidPhoneNumber(student.getPhoneNumber())) {
            errors.add("Invalid phone number format. Must be 10-15 digits, optionally starting with '+'.");
        }
        if (student.getBirthDate() != null && student.getBirthDate().isAfter(LocalDate.now())) {
            errors.add("Date of birth must be in the past.");
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
        if (student.getGender() == null) {
            errors.add("Gender is required to assign a default avatar.");
        }
        if (student.getEmail() != null && personsService.existsByEmailExcludingId(student.getEmail(), student.getId() != null ? student.getId() : "")) {
            errors.add("The email address is already associated with another account.");
        }
        if (student.getPhoneNumber() != null && personsService.existsByPhoneNumberExcludingId(student.getPhoneNumber(), student.getId() != null ? student.getId() : "")) {
            errors.add("The phone number is already associated with another account.");
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
    public Students getStudent() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new IllegalStateException("No authenticated user");
        }

        Object principal = auth.getPrincipal();

        Persons person = switch (principal) {
            case DatabaseUserPrincipal dbPrincipal -> dbPrincipal.getPerson();
            case OAuth2UserPrincipal oauthPrincipal -> oauthPrincipal.getPerson();
            default -> throw new IllegalStateException("Unknown principal type: " + principal.getClass());
        };

        if (!(person instanceof Students student)) {
            throw new IllegalStateException("Authenticated user is not a student");
        }

        return entityManager.find(Students.class, student.getId());
    }


    @Override
    public Majors getStudentMajor() {
        return getStudent().getMajor();
    }

    @Override
    public List<Students> getStudents() {
        return entityManager.createQuery("SELECT s FROM Students s JOIN FETCH s.campus JOIN FETCH s.major JOIN FETCH s.creator", Students.class)
                .getResultList();
    }

    @Override
    public Students addStudents(Students students, String randomPassword) {
        Staffs staff = staffsService.getStaff();
        students.setCampus(staff.getCampus());
        students.setMajor(staff.getMajorManagement());
        students.setCreator(staff);
        LocalDate admissionDate = LocalDate.of(Year.now().getValue(), 1, 1);
        students.setAdmissionYear(admissionDate);
        students.setCreatedDate(LocalDate.now());
        Students savedStudent = entityManager.merge(students);

        // Handle avatar
        String avatarPath = null;
        try {
            avatarPath = saveAvatarAndGetPath(savedStudent.getId(), savedStudent.getAvatar());
        } catch (IOException e) {
            System.err.println("Failed to save avatar for student " + savedStudent.getId() + ": " + e.getMessage());
        }

        // Create StudentEmailContext
        StudentEmailContext context = new StudentEmailContext(
                savedStudent.getId(),
                savedStudent.getFullName(),
                savedStudent.getEmail(),
                savedStudent.getPhoneNumber(),
                savedStudent.getBirthDate(),
                savedStudent.getGender() != null ? savedStudent.getGender().toString() : null,
                savedStudent.getFullAddress(),
                savedStudent.getCampus() != null ? savedStudent.getCampus().getCampusName() : null,
                savedStudent.getMajor() != null ? savedStudent.getMajor().getMajorName() : null,
                savedStudent.getCreator() != null ? savedStudent.getCreator().getFullName() : null,
                savedStudent.getAdmissionYear(),
                savedStudent.getCreatedDate(),
                savedStudent.getLearningProgramType() != null ? savedStudent.getLearningProgramType().toString() : null,
                avatarPath
        );

        try {
            String subject = "Your Student Account Information";
            emailServiceForStudentService.sendEmailToNotifyLoginInformation(students.getEmail(), subject, context, randomPassword);
        } catch (Exception e) {
            System.err.println("Failed to schedule email to " + students.getEmail() + ": " + e.getMessage());
        }
        return savedStudent;
    }

    @Override
    public long numberOfStudents() {
        Staffs staff = staffsService.getStaff();
        return (Long) entityManager.createQuery(
                        "SELECT COUNT(s) FROM Students s WHERE s.major = :staffmajor")
                .setParameter("staffmajor", staff.getMajorManagement())
                .getSingleResult();
    }

    @Override
    public void deleteStudent(String id) {
        Students student = entityManager.find(Students.class, id);
        if (student == null) {
            throw new IllegalArgumentException("Student with ID " + id + " not found");
        }
        entityManager.remove(student);
    }

    @Override
    public void editStudent(String id, Students student) throws MessagingException {
        if (student == null || id == null) {
            throw new IllegalArgumentException("Student object or ID cannot be null");
        }
        Students existingStudent = entityManager.createQuery(
                        "SELECT s FROM Students s JOIN FETCH s.campus JOIN FETCH s.major JOIN FETCH s.creator WHERE s.id = :id",
                        Students.class
                )
                .setParameter("id", id)
                .getSingleResult();
        if (existingStudent == null) {
            throw new IllegalArgumentException("Student with ID " + id + " not found");
        }
        editStudentFields(existingStudent, student);

        // Handle avatar
        String avatarPath = null;
        try {
            avatarPath = saveAvatarAndGetPath(existingStudent.getId(), existingStudent.getAvatar());
        } catch (IOException e) {
            System.err.println("Failed to save avatar for student " + existingStudent.getId() + ": " + e.getMessage());
        }

        entityManager.merge(existingStudent);

        // Create StudentEmailContext
        StudentEmailContext context = new StudentEmailContext(
                existingStudent.getId(),
                existingStudent.getFullName(),
                existingStudent.getEmail(),
                existingStudent.getPhoneNumber(),
                existingStudent.getBirthDate(),
                existingStudent.getGender() != null ? existingStudent.getGender().toString() : null,
                existingStudent.getFullAddress(),
                existingStudent.getCampus() != null ? existingStudent.getCampus().getCampusName() : null,
                existingStudent.getMajor() != null ? existingStudent.getMajor().getMajorName() : null,
                existingStudent.getCreator() != null ? existingStudent.getCreator().getFullName() : null,
                existingStudent.getAdmissionYear(),
                existingStudent.getCreatedDate(),
                existingStudent.getLearningProgramType() != null ? existingStudent.getLearningProgramType().toString() : null,
                avatarPath
        );

        String subject = "Your student account information after being edited";
        emailServiceForStudentService.sendEmailToNotifyInformationAfterEditing(existingStudent.getEmail(), subject, context);
    }

    @Override
    public Students getStudentById(String id) {
        return entityManager.find(Students.class, id);
    }

    @Override
    public List<Students> getPaginatedStudents(int firstResult, int pageSize) {
        Staffs staff = staffsService.getStaff();
        Majors majors = staff.getMajorManagement();
        return entityManager.createQuery(
                        "SELECT s FROM Students s  WHERE s.major = :staffmajor AND s.campus = :campuses",
                        Students.class)
                .setParameter("staffmajor", majors)
                .setParameter("campuses", staff.getCampus())
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public List<Students> searchStudents(String searchType, String keyword, int firstResult, int pageSize) {
        if (keyword == null || keyword.trim().isEmpty() || pageSize <= 0) {
            return List.of();
        }

        Staffs staff = staffsService.getStaff();
        if (staff == null || staff.getMajorManagement() == null || staff.getCampus() == null) {
            return List.of();
        }
        String queryString = "SELECT s FROM Students s JOIN FETCH s.campus JOIN FETCH s.major JOIN FETCH s.creator " +
                "WHERE s.major = :staffmajor AND s.campus = :campuses";

        if ("name".equals(searchType)) {
            queryString += " AND (LOWER(s.firstName) LIKE LOWER(:keyword) OR LOWER(s.lastName) LIKE LOWER(:keyword))";
        } else if ("id".equals(searchType)) {
            queryString += " AND s.id LIKE :keyword";
        } else {
            return List.of();
        }

        return entityManager.createQuery(queryString, Students.class)
                .setParameter("staffmajor", staff.getMajorManagement())
                .setParameter("campuses", staff.getCampus())
                .setParameter("keyword", "%" + keyword.trim() + "%")
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public long countSearchResults(String searchType, String keyword) {
        String queryString = "SELECT COUNT(s) FROM Students s WHERE ";
        if ("name".equals(searchType)) {
            queryString += "LOWER(s.firstName) LIKE LOWER(:keyword) OR LOWER(s.lastName) LIKE LOWER(:keyword)";
        } else {
            queryString += "s.id LIKE :keyword";
        }
        return entityManager.createQuery(queryString, Long.class)
                .setParameter("keyword", "%" + keyword + "%")
                .getSingleResult();
    }

    private void editStudentFields(Students existing, Students editd) {
        if (editd.getFirstName() != null) existing.setFirstName(editd.getFirstName());
        if (editd.getLastName() != null) existing.setLastName(editd.getLastName());
        if (editd.getEmail() != null) existing.setEmail(editd.getEmail());
        if (editd.getPhoneNumber() != null) existing.setPhoneNumber(editd.getPhoneNumber());
        if (editd.getBirthDate() != null) existing.setBirthDate(editd.getBirthDate());
        if (editd.getGender() != null) existing.setGender(editd.getGender());
        if (editd.getCountry() != null) existing.setCountry(editd.getCountry());
        if (editd.getProvince() != null) existing.setProvince(editd.getProvince());
        if (editd.getCity() != null) existing.setCity(editd.getCity());
        if (editd.getDistrict() != null) existing.setDistrict(editd.getDistrict());
        if (editd.getWard() != null) existing.setWard(editd.getWard());
        if (editd.getStreet() != null) existing.setStreet(editd.getStreet());
        if (editd.getPostalCode() != null) existing.setPostalCode(editd.getPostalCode());
        if (editd.getAvatar() != null) existing.setAvatar(editd.getAvatar());
        if (editd.getCampus() != null) existing.setCampus(editd.getCampus());
        if (editd.getCreator() != null) existing.setCreator(editd.getCreator());
    }
}