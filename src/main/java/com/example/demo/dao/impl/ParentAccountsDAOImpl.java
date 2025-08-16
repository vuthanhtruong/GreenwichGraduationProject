package com.example.demo.dao.impl;

import com.example.demo.dao.ParentAccountsDAO;
import com.example.demo.entity.ParentAccounts;
import com.example.demo.entity.Student_ParentAccounts;
import com.example.demo.entity.Students;
import com.example.demo.service.PersonsService;
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

    @Autowired
    public ParentAccountsDAOImpl(PersonsService personsService) {
        this.personsService = personsService;
    }

    @Override
    public void addParentAccounts(ParentAccounts parent) {
        if (parent == null) {
            throw new IllegalArgumentException("Parent account cannot be null");
        }
        // Check if parent with the same email already exists
        ParentAccounts existingParent = findByEmail(parent.getEmail());
        if (existingParent != null) {
            // Update existing parent with new information (if needed)
            existingParent.setFirstName(parent.getFirstName());
            existingParent.setLastName(parent.getLastName());
            existingParent.setPhoneNumber(parent.getPhoneNumber());
            existingParent.setGender(parent.getGender());
            existingParent.setRelationshipToStudent(parent.getRelationshipToStudent());
            entityManager.merge(existingParent);
        } else {
            // Set created date for new parent
            parent.setCreatedDate(LocalDate.now());
            entityManager.merge(parent);
        }
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
    public Student_ParentAccounts linkStudentToParent(Students student, ParentAccounts parent) {
        if (student == null || parent == null) {
            throw new IllegalArgumentException("Student or Parent cannot be null");
        }
        // Check if the link already exists
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
            return existingLink; // Link already exists, return it
        }
        Student_ParentAccounts link = new Student_ParentAccounts(student, parent, null, LocalDateTime.now());
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
            errors.add("First name is not valid. Only letters, spaces, and standard punctuation are allowed.");
        }

        // Last Name - OPTIONAL + format if present
        if (!isNullOrBlank(parent.getLastName()) && !isValidName(parent.getLastName())) {
            errors.add("Last name is not valid. Only letters, spaces, and standard punctuation are allowed.");
        }

        // Email - OPTIONAL + format if present
        if (!isNullOrBlank(parent.getEmail()) && !isValidEmail(parent.getEmail())) {
            errors.add("Invalid email format.");
        }

        // Phone Number - OPTIONAL + format if present
        if (!isNullOrBlank(parent.getPhoneNumber()) && !isValidPhoneNumber(parent.getPhoneNumber())) {
            errors.add("Invalid phone number format.");
        }

        // Birth Date - OPTIONAL + must be in the past if present
        if (parent.getBirthDate() != null && parent.getBirthDate().isAfter(LocalDate.now())) {
            errors.add("Date of birth must be in the past.");
        }

        // Gender - OPTIONAL (không báo lỗi nếu để trống)
        // Relationship to Student - OPTIONAL (không báo lỗi nếu để trống)

        return errors;
    }
    private boolean isNullOrBlank(String s) {
        return s == null || s.trim().isEmpty();
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
            String randomDigit = String.valueOf(random.nextInt(1000)); // 3-digit random number
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
}