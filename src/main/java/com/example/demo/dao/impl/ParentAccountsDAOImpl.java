package com.example.demo.dao.impl;

import com.example.demo.dao.ParentAccountsDAO;
import com.example.demo.entity.ParentAccounts;
import com.example.demo.entity.Student_ParentAccounts;
import com.example.demo.entity.Students;
import com.example.demo.service.PersonsService;
import com.example.demo.service.StaffsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Transactional
public class ParentAccountsDAOImpl implements ParentAccountsDAO {

    @PersistenceContext
    private EntityManager entityManager;

    private final PersonsService personsService;
    private final StaffsService staffsService;

    @Autowired
    public ParentAccountsDAOImpl(PersonsService personsService, StaffsService staffsService) {
        this.personsService = personsService;
        this.staffsService = staffsService;
    }

    @Override
    public void addParentAccounts(ParentAccounts parent) {
        parent.setCreatedDate(LocalDate.now());
        entityManager.persist(parent);
    }

    @Override
    public ParentAccounts findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        try {
            return entityManager.createQuery("SELECT p FROM ParentAccounts p WHERE p.email = :email", ParentAccounts.class)
                    .setParameter("email", email)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Student_ParentAccounts linkStudentToParent(Students student, ParentAccounts parent, String relationshipToStudent) {
        if (student == null || parent == null) {
            throw new IllegalArgumentException("Student or Parent cannot be null");
        }
        // Kiểm tra relationshipToStudent nếu được cung cấp
        if (relationshipToStudent != null && !relationshipToStudent.trim().isEmpty() && !isValidRelationship(relationshipToStudent)) {
            throw new IllegalArgumentException("Invalid relationship to student. Allowed values: Father, Mother, Guardian.");
        }
        // Kiểm tra liên kết đã tồn tại
        Student_ParentAccounts existingLink = entityManager.createQuery(
                        "SELECT spa FROM Student_ParentAccounts spa WHERE spa.student.id = :studentId AND spa.parent.id = :parentId",
                        Student_ParentAccounts.class)
                .setParameter("studentId", student.getId())
                .setParameter("parentId", parent.getId())
                .setMaxResults(1)
                .getResultStream()
                .findFirst()
                .orElse(null);
        if (existingLink != null) {
            return existingLink; // Liên kết đã tồn tại
        }
        Student_ParentAccounts link = new Student_ParentAccounts(student, parent, staffsService.getStaff(), LocalDateTime.now(), relationshipToStudent);
        return entityManager.merge(link);
    }

    @Override
    public List<String> validateParent(ParentAccounts parent) {
        List<String> errors = new ArrayList<>();
        if (parent == null) {
            errors.add("Parent account cannot be null");
            return errors;
        }
        // First Name - OPTIONAL + format if present
        if (!isNullOrBlank(parent.getFirstName()) && !isValidName(parent.getFirstName())) {
            errors.add("Parent first name is not valid. Only letters, spaces, and standard punctuation are allowed.");
        }
        // Last Name - OPTIONAL + format if present
        if (!isNullOrBlank(parent.getLastName()) && !isValidName(parent.getLastName())) {
            errors.add("Parent last name is not valid. Only letters, spaces, and standard punctuation are allowed.");
        }
        // Email - OPTIONAL + format + uniqueness if present
        if (!isNullOrBlank(parent.getEmail())) {
            if (!isValidEmail(parent.getEmail())) {
                errors.add("Invalid parent email format.");
            } else if (personsService.existsByEmail(parent.getEmail())) {
                ParentAccounts existingParent = findByEmail(parent.getEmail());
                if (existingParent == null) {
                    errors.add("The email address is already associated with another account type.");
                }
            }
        }
        // Phone Number - OPTIONAL + format + uniqueness if present
        if (!isNullOrBlank(parent.getPhoneNumber())) {
            if (!isValidPhoneNumber(parent.getPhoneNumber())) {
                errors.add("Invalid parent phone number format.");
            } else if (personsService.existsByPhoneNumber(parent.getPhoneNumber())) {
                ParentAccounts existingParent = findByEmail(parent.getEmail());
                if (existingParent == null || !existingParent.getPhoneNumber().equals(parent.getPhoneNumber())) {
                    errors.add("The phone number is already associated with another account.");
                }
            }
        }
        // Birth Date - OPTIONAL + must be in the past if present
        if (parent.getBirthDate() != null && parent.getBirthDate().isAfter(LocalDate.now())) {
            errors.add("Date of birth must be in the past.");
        }
        return errors;
    }

    @Override
    public List<String> ParentValidation(ParentAccounts parent) {
        return validateParent(parent);
    }

    @Override
    public String generateUniqueParentId() {
        String prefix = "PAR";
        String year = String.format("%02d", LocalDate.now().getYear() % 100);
        String date = String.format("%02d%02d", LocalDate.now().getMonthValue(), LocalDate.now().getDayOfMonth());
        String parentId;
        SecureRandom random = new SecureRandom();
        do {
            String randomDigit = String.valueOf(random.nextInt(1000));
            parentId = prefix + year + date + String.format("%03d", Integer.parseInt(randomDigit));
        } while (personsService.existsPersonById(parentId));
        return parentId;
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

    private boolean isNullOrBlank(String s) {
        return s == null || s.trim().isEmpty();
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
            return true; // Cho phép tên trống vì là tùy chọn
        }
        String nameRegex = "^[\\p{L}][\\p{L} .'-]{1,49}$";
        return name.matches(nameRegex);
    }

    private boolean isValidRelationship(String relationship) {
        return relationship != null && (
                relationship.equalsIgnoreCase("Father") ||
                        relationship.equalsIgnoreCase("Mother") ||
                        relationship.equalsIgnoreCase("Guardian")
        );
    }
}