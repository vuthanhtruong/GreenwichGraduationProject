package com.example.demo.parentAccount.dao;

import com.example.demo.parentAccount.model.ParentAccounts;
import com.example.demo.entity.Student_ParentAccounts;
import com.example.demo.authenticator.model.Authenticators;
import com.example.demo.entity.Enums.RelationshipToStudent;
import com.example.demo.person.service.PersonsService;
import com.example.demo.staff.service.StaffsService;
import com.example.demo.student.service.StudentsService;
import com.example.demo.authenticator.service.AuthenticatorsService;
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
    private final StudentsService studentsService;
    private final AuthenticatorsService authenticatorsService;

    @Autowired
    public ParentAccountsDAOImpl(PersonsService personsService, StaffsService staffsService,
                                 StudentsService studentsService, AuthenticatorsService authenticatorsService) {
        this.personsService = personsService;
        this.staffsService = staffsService;
        this.studentsService = studentsService;
        this.authenticatorsService = authenticatorsService;
    }

    @Override
    public void addParentAccounts(ParentAccounts parent) {
        parent.setCreatedDate(LocalDate.now());
        entityManager.persist(parent);
    }

    @Override
    public void editParent(ParentAccounts parent) {
        entityManager.merge(parent);
    }

    @Override
    public void deleteParent(ParentAccounts parent) {
        entityManager.remove(entityManager.contains(parent) ? parent : entityManager.merge(parent));
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
    public Student_ParentAccounts getParentLinkByStudentId(String studentId) {
        try {
            return entityManager.createQuery(
                            "SELECT spa FROM Student_ParentAccounts spa WHERE spa.student.id = :studentId",
                            Student_ParentAccounts.class)
                    .setParameter("studentId", studentId)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Student_ParentAccounts> getParentLinksByStudentId(String studentId) {
        try {
            return entityManager.createQuery(
                            "SELECT spa FROM Student_ParentAccounts spa WHERE spa.student.id = :studentId",
                            Student_ParentAccounts.class)
                    .setParameter("studentId", studentId)
                    .getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public void removeParentLink(Student_ParentAccounts parentLink) {
        entityManager.remove(entityManager.contains(parentLink) ? parentLink : entityManager.merge(parentLink));
    }

    @Override
    public Student_ParentAccounts linkStudentToParent(Student_ParentAccounts studentParent) {
        if (studentParent == null || studentParent.getStudent() == null || studentParent.getParent() == null) {
            throw new IllegalArgumentException("Student_ParentAccounts, Student, or Parent cannot be null");
        }
        return entityManager.merge(studentParent);
    }

    @Override
    public void editParentLink(Student_ParentAccounts existingLink, RelationshipToStudent relationship, String supportPhoneNumber) {
        if (relationship != null) {
            existingLink.setRelationshipToStudent(relationship);
        }
        if (supportPhoneNumber != null) {
            existingLink.setSupportPhoneNumber(supportPhoneNumber);
        }
        entityManager.merge(existingLink);
    }

    @Override
    public long countLinkedStudents(String parentId, String excludeStudentId) {
        return entityManager.createQuery(
                        "SELECT COUNT(spa) FROM Student_ParentAccounts spa WHERE spa.parent.id = :parentId AND spa.student.id != :excludeStudentId",
                        Long.class)
                .setParameter("parentId", parentId)
                .setParameter("excludeStudentId", excludeStudentId)
                .getSingleResult();
    }

    @Override
    public List<String> validateParent(ParentAccounts parent) {
        List<String> errors = new ArrayList<>();
        if (parent == null) {
            errors.add("Parent account cannot be null");
            return errors;
        }
        if (!isNullOrBlank(parent.getFirstName()) && !isValidName(parent.getFirstName())) {
            errors.add("Parent first name is not valid. Only letters, spaces, and standard punctuation are allowed.");
        }
        if (!isNullOrBlank(parent.getLastName()) && !isValidName(parent.getLastName())) {
            errors.add("Parent last name is not valid. Only letters, spaces, and standard punctuation are allowed.");
        }
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
        if (!isNullOrBlank(parent.getPhoneNumber())) {
            if (!isValidPhoneNumber(parent.getPhoneNumber())) {
                errors.add("Invalid parent phone number format. Must be 10-15 digits, optionally starting with '+'.");
            } else if (personsService.existsByPhoneNumber(parent.getPhoneNumber())) {
                ParentAccounts existingParent = findByPhoneNumber(parent.getPhoneNumber());
                if (existingParent == null) {
                    errors.add("The phone number is already associated with another account.");
                }
            }
        }
        if (parent.getBirthDate() != null && parent.getBirthDate().isAfter(LocalDate.now())) {
            errors.add("Date of birth must be in the past.");
        }
        return errors;
    }

    @Override
    public List<String> validateParentLink(String email, String supportPhoneNumber, String relationship, String parentLabel) {
        List<String> errors = new ArrayList<>();
        // Skip validation if all fields are empty
        if (!isAnyFieldProvided(email, supportPhoneNumber, relationship)) {
            return errors;
        }
        // Email is required if any field is provided
        if (email == null || email.trim().isEmpty()) {
            errors.add(parentLabel + ": Email is required when other parent fields are provided.");
            return errors;
        }
        ParentAccounts parent = new ParentAccounts();
        parent.setEmail(email);
        List<String> parentErrors = validateParent(parent);
        parentErrors.forEach(error -> errors.add(parentLabel + ": " + error));
        if (supportPhoneNumber != null && !supportPhoneNumber.trim().isEmpty()) {
            if (!supportPhoneNumber.matches("^\\+?[0-9]{10,15}$")) {
                errors.add(parentLabel + ": Invalid support phone number format. Must be 10-15 digits, optionally starting with '+'.");
            }
        }
        if (relationship != null && !relationship.trim().isEmpty()) {
            try {
                RelationshipToStudent.valueOf(relationship.toUpperCase());
            } catch (IllegalArgumentException e) {
                errors.add(parentLabel + ": Invalid relationship to student. Allowed values: " +
                        String.join(", ", getRelationshipValues()));
            }
        }
        return errors;
    }

    @Override
    public String generateUniqueParentId() {
        String prefix = "PAR";
        String year = String.format("%02d", LocalDate.now().getYear() % 100);
        String date = String.format("%02d%02d", LocalDate.now().getMonthValue(), LocalDate.now().getDayOfMonth());
        String parentId;
        SecureRandom random = new SecureRandom();
        do {
            String randomDigit = String.format("%03d", random.nextInt(1000));
            parentId = prefix + year + date + randomDigit;
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

    @Override
    public void editOrCreateParentLink(String studentId, Student_ParentAccounts existingLink, String email, String supportPhoneNumber, String relationship) {
        // Skip if no parent fields are provided
        if (!isAnyFieldProvided(email, supportPhoneNumber, relationship)) {
            return;
        }
        RelationshipToStudent relationshipEnum = relationship != null && !relationship.trim().isEmpty() ?
                RelationshipToStudent.valueOf(relationship.toUpperCase()) : null;
        ParentAccounts parent = findByEmail(email);
        if (parent == null) {
            parent = new ParentAccounts();
            parent.setEmail(email);
            parent.setId(generateUniqueParentId());
            parent.setCreatedDate(LocalDate.now());
            addParentAccounts(parent);
            String parentPassword = generateRandomPassword(12);
            Authenticators parentAuth = new Authenticators();
            parentAuth.setPersonId(parent.getId());
            parentAuth.setPerson(personsService.getPersonById(parent.getId()));
            parentAuth.setPassword(parentPassword);
            authenticatorsService.createAuthenticator(parentAuth);
        }
        if (existingLink != null && existingLink.getParent().getEmail().equals(email)) {
            editParentLink(existingLink, relationshipEnum, supportPhoneNumber);
        } else {
            if (existingLink != null) {
                removeParentLink(existingLink);
                deleteIfUnlinked(existingLink.getParent(), studentId);
            }
            Student_ParentAccounts newLink = new Student_ParentAccounts(
                    studentsService.getStudentById(studentId),
                    parent,
                    staffsService.getStaff(),
                    LocalDateTime.now(),
                    relationshipEnum,
                    supportPhoneNumber
            );
            linkStudentToParent(newLink);
        }
    }

    @Override
    public void createParentLink(String studentId, String email, String supportPhoneNumber, String relationship) {
        // Skip if no parent fields are provided
        if (!isAnyFieldProvided(email, supportPhoneNumber, relationship)) {
            return;
        }
        RelationshipToStudent relationshipEnum = relationship != null && !relationship.trim().isEmpty() ?
                RelationshipToStudent.valueOf(relationship.toUpperCase()) : null;
        ParentAccounts parent = findByEmail(email);
        if (parent == null) {
            parent = new ParentAccounts();
            parent.setEmail(email);
            parent.setId(generateUniqueParentId());
            parent.setCreatedDate(LocalDate.now());
            addParentAccounts(parent);
            String parentPassword = generateRandomPassword(12);
            Authenticators parentAuth = new Authenticators();
            parentAuth.setPersonId(parent.getId());
            parentAuth.setPerson(personsService.getPersonById(parent.getId()));
            parentAuth.setPassword(parentPassword);
            authenticatorsService.createAuthenticator(parentAuth);
        }
        Student_ParentAccounts link = new Student_ParentAccounts(
                studentsService.getStudentById(studentId),
                parent,
                staffsService.getStaff(),
                LocalDateTime.now(),
                relationshipEnum,
                supportPhoneNumber
        );
        linkStudentToParent(link);
    }

    @Override
    public void deleteIfUnlinked(ParentAccounts parent, String excludeStudentId) {
        long linkedStudentsCount = countLinkedStudents(parent.getId(), excludeStudentId);
        if (linkedStudentsCount == 0) {
            authenticatorsService.deleteAuthenticatorByPersonId(parent.getId());
            deleteParent(parent);
        }
    }

    private ParentAccounts findByPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return null;
        }
        try {
            return entityManager.createQuery("SELECT p FROM ParentAccounts p WHERE p.phoneNumber = :phoneNumber", ParentAccounts.class)
                    .setParameter("phoneNumber", phoneNumber)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isNullOrBlank(String s) {
        return s == null || s.trim().isEmpty();
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
            return true;
        }
        String nameRegex = "^[\\p{L}][\\p{L} .'-]{0,49}$";
        return name.matches(nameRegex);
    }

    private String[] getRelationshipValues() {
        RelationshipToStudent[] values = RelationshipToStudent.values();
        String[] result = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = values[i].toString();
        }
        return result;
    }

    private boolean isAnyFieldProvided(String email, String phoneNumber, String relationship) {
        return (email != null && !email.trim().isEmpty()) ||
                (phoneNumber != null && !phoneNumber.trim().isEmpty()) ||
                (relationship != null && !relationship.trim().isEmpty());
    }
}