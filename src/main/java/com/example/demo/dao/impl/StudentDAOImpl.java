
package com.example.demo.dao.impl;

import com.example.demo.dao.StudentsDAO;
import com.example.demo.entity.*;
import com.example.demo.service.*;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Transactional
public class StudentDAOImpl implements StudentsDAO {

    private final StaffsService staffsService;
    private final PersonsService personsService;
    @PersistenceContext
    private EntityManager entityManager;
    private final EmailServiceForStudentService emailServiceForStudentService;
    private final EmailServiceForLectureService emailServiceForLectureService;
    private final AccountBalancesService accountBalancesService;
    private final AuthenticatorsService authenticatorsService;

    public StudentDAOImpl(PersonsService personsService, EmailServiceForStudentService emailServiceForStudentService,
                          EmailServiceForLectureService emailServiceForLectureService,
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

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email != null && email.matches(emailRegex);
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return true;
        }
        String phoneRegex = "^\\+?[0-9]{10,15}$";
        return phoneNumber.matches(phoneRegex);
    }

    private boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        String nameRegex = "^[\\p{L}][\\p{L} .'-]{1,49}$";
        return name.matches(nameRegex);
    }

    @Override
    public Students getStudent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return entityManager.createQuery(
                        "SELECT s FROM Students s WHERE s.email = :username OR s.id = :username",
                        Students.class)
                .setParameter("username", authentication.getName())
                .setMaxResults(1)
                .getSingleResult();
    }

    @Override
    public Majors getStudentMajor() {
        return getStudent().getMajor();
    }

    @Override
    public List<Students> getStudents() {
        return entityManager.createQuery("FROM Students s", Students.class).getResultList();
    }

    @Override
    public Students addStudents(Students students, String randomPassword) {
        Staffs staff = staffsService.getStaff();
        students.setCampus(staff.getCampus());
        students.setMajor(staffsService.getStaffMajor());
        students.setCreator(staff);
        LocalDate admissionDate = LocalDate.of(Year.now().getValue(), 1, 1);
        students.setAdmissionYear(admissionDate);
        Students savedStudent = entityManager.merge(students);
        try {
            String subject = "Your Student Account Information";
            emailServiceForStudentService.sendEmailToNotifyLoginInformation(students.getEmail(), subject, students, randomPassword);
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
    public void updateStudent(String id, Students student) throws MessagingException {
        if (student == null || id == null) {
            throw new IllegalArgumentException("Student object or ID cannot be null");
        }
        Students existingStudent = entityManager.find(Students.class, id);
        if (existingStudent == null) {
            throw new IllegalArgumentException("Student with ID " + id + " not found");
        }
        updateStudentFields(existingStudent, student);
        entityManager.merge(existingStudent);
        String subject = "Your student account information after being edited";
        emailServiceForStudentService.sendEmailToNotifyInformationAfterEditing(existingStudent.getEmail(), subject, existingStudent);
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
                        "SELECT s FROM Students s WHERE s.major = :staffmajor", Students.class)
                .setParameter("staffmajor", majors)
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .getResultList();
    }

    private void updateStudentFields(Students existing, Students updated) {
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
        if (updated.getMisId() != null) existing.setMisId(updated.getMisId());
        if (updated.getCampus() != null) existing.setCampus(updated.getCampus());
        if (updated.getCreator() != null) existing.setCreator(updated.getCreator());
    }
}