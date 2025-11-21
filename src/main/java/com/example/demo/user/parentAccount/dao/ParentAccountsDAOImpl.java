package com.example.demo.user.parentAccount.dao;

import com.example.demo.email_service.dto.ParentEmailContext;
import com.example.demo.email_service.service.EmailServiceForParentService;
import com.example.demo.specialization.security.model.CustomOidcUserPrincipal;
import com.example.demo.specialization.security.model.DatabaseUserPrincipal;
import com.example.demo.user.parentAccount.model.ParentAccounts;
import com.example.demo.user.parentAccount.model.Student_ParentAccounts;
import com.example.demo.authenticator.model.Authenticators;
import com.example.demo.entity.Enums.RelationshipToStudent;
import com.example.demo.user.person.model.Persons;
import com.example.demo.user.person.service.PersonsService;
import com.example.demo.user.staff.model.Staffs;
import com.example.demo.user.staff.service.StaffsService;
import com.example.demo.user.student.model.Students;
import com.example.demo.user.student.service.StudentsService;
import com.example.demo.authenticator.service.AuthenticatorsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
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
    private final EmailServiceForParentService emailServiceForParentService;

    @Autowired
    public ParentAccountsDAOImpl(PersonsService personsService,
                                 StaffsService staffsService,
                                 StudentsService studentsService,
                                 AuthenticatorsService authenticatorsService,
                                 EmailServiceForParentService emailServiceForParentService) {
        this.personsService = personsService;
        this.staffsService = staffsService;
        this.studentsService = studentsService;
        this.authenticatorsService = authenticatorsService;
        this.emailServiceForParentService = emailServiceForParentService;
    }

    // ==================== EMAIL HELPER METHODS ====================

    /**
     * Tạo ParentEmailContext từ ParentAccounts
     */
    private ParentEmailContext createEmailContext(ParentAccounts parent) {
        return new ParentEmailContext(
                parent.getId(),
                parent.getFullName(),
                parent.getEmail(),
                parent.getPhoneNumber(),
                parent.getBirthDate(),
                parent.getGender() != null ? parent.getGender().toString() : null,
                parent.getFullAddress(),
                parent.getCreator() != null ? parent.getCreator().getFullName() : null,
                parent.getCreatedDate()
        );
    }

    /**
     * Gửi email thông báo tạo tài khoản phụ huynh
     */
    private void sendParentCreationEmail(ParentAccounts parent, String password) {
        try {
            ParentEmailContext context = createEmailContext(parent);
            String subject = "Your Parent Account Information";
            emailServiceForParentService.sendEmailToNotifyLoginInformation(
                    parent.getEmail(),
                    subject,
                    context,
                    password
            );
        } catch (Exception e) {
            System.err.println("Failed to send parent creation email to " + parent.getEmail() + ": " + e.getMessage());
            // Don't throw exception - email failure shouldn't block account creation
        }
    }

    /**
     * Gửi email thông báo chỉnh sửa thông tin
     */
    private void sendParentEditEmail(ParentAccounts parent) {
        try {
            ParentEmailContext context = createEmailContext(parent);
            String subject = "Your Parent Account Information After Editing";
            emailServiceForParentService.sendEmailToNotifyInformationAfterEditing(
                    parent.getEmail(),
                    subject,
                    context
            );
        } catch (Exception e) {
            System.err.println("Failed to send parent edit email to " + parent.getEmail() + ": " + e.getMessage());
        }
    }

    /**
     * Gửi email thông báo liên kết với học sinh
     */
    private void sendStudentLinkEmail(ParentAccounts parent, Students student, String relationship) {
        try {
            ParentEmailContext context = createEmailContext(parent);
            String subject = "You've Been Linked to a Student";
            emailServiceForParentService.sendEmailToNotifyStudentLink(
                    parent.getEmail(),
                    subject,
                    context,
                    student.getFullName(),
                    student.getId(),
                    relationship != null ? relationship : "Not specified"
            );
        } catch (Exception e) {
            System.err.println("Failed to send student link email to " + parent.getEmail() + ": " + e.getMessage());
        }
    }

    /**
     * Gửi email thông báo xóa tài khoản
     */
    private void sendParentDeletionEmail(ParentAccounts parent) {
        try {
            ParentEmailContext context = createEmailContext(parent);
            String subject = "Parent Account Deletion Notice";
            emailServiceForParentService.sendEmailToNotifyParentDeletion(
                    parent.getEmail(),
                    subject,
                    context
            );
        } catch (Exception e) {
            System.err.println("Failed to send parent deletion email to " + parent.getEmail() + ": " + e.getMessage());
        }
    }

    // ==================== EXISTING METHODS WITH EMAIL INTEGRATION ====================

    @Override
    public List<Students> getStudentsByParentId(String parentId) {
        if (parentId == null || parentId.trim().isEmpty()) {
            return new ArrayList<>();
        }

        try {
            return entityManager.createQuery(
                            "SELECT spa.student FROM Student_ParentAccounts spa " +
                                    "WHERE spa.parent.id = :parentId " +
                                    "ORDER BY spa.createdAt DESC",
                            Students.class)
                    .setParameter("parentId", parentId)
                    .getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public ParentAccounts getParent() {
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
        return entityManager.find(ParentAccounts.class, person.getId());
    }

    @Override
    public Student_ParentAccounts findLinkByStudentAndParent(String studentId, String parentId) {
        try {
            return entityManager.createQuery(
                            "SELECT spa FROM Student_ParentAccounts spa " +
                                    "WHERE spa.student.id = :studentId AND spa.parent.id = :parentId",
                            Student_ParentAccounts.class)
                    .setParameter("studentId", studentId)
                    .setParameter("parentId", parentId)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void removeParentLinkByIds(String studentId, String parentId) {
        Student_ParentAccounts link = findLinkByStudentAndParent(studentId, parentId);
        if (link != null) {
            entityManager.remove(link);
            deleteIfUnlinked(link.getParent(), studentId);
        }
    }

    @Override
    public boolean isParentEmailAvailable(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        String normalizedEmail = email.trim().toLowerCase();

        Long parentCount = entityManager.createQuery(
                        "SELECT COUNT(p) FROM ParentAccounts p WHERE LOWER(p.email) = :email", Long.class)
                .setParameter("email", normalizedEmail)
                .getSingleResult();

        if (parentCount > 0) {
            return true;
        }

        Long otherCount = entityManager.createQuery(
                        "SELECT COUNT(p) FROM Persons p " +
                                "WHERE LOWER(p.email) = :email " +
                                "AND TYPE(p) IN (Students, Staffs, Admins, DeputyStaffs, MajorLecturers, MinorLecturers)", Long.class)
                .setParameter("email", normalizedEmail)
                .getSingleResult();

        return otherCount == 0;
    }

    @Override
    public void addParentAccounts(ParentAccounts parent) {
        parent.setCreatedDate(LocalDate.now());
        entityManager.persist(parent);
        // Note: Email sẽ được gửi ở nơi gọi method này với password
    }

    @Override
    public void editParent(ParentAccounts parent) {
        ParentAccounts merged = entityManager.merge(parent);

        // Gửi email thông báo chỉnh sửa
        sendParentEditEmail(merged);
    }

    @Override
    public void deleteParent(ParentAccounts parent) {
        // Gửi email thông báo xóa trước khi xóa
        sendParentDeletionEmail(parent);

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

        Student_ParentAccounts merged = entityManager.merge(studentParent);

        // Gửi email thông báo liên kết
        String relationship = merged.getRelationshipToStudent() != null
                ? merged.getRelationshipToStudent().toString()
                : null;
        sendStudentLinkEmail(merged.getParent(), merged.getStudent(), relationship);

        return merged;
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
    public Map<String, String> validateParent(ParentAccounts parent, MultipartFile avatarFile) {
        Map<String, String> errors = new HashMap<>();

        if (parent == null) {
            errors.put("general", "Parent data is required.");
            return errors;
        }

        if (isNullOrBlank(parent.getFirstName())) {
            errors.put("firstName", "First name is required.");
        } else if (!isValidName(parent.getFirstName())) {
            errors.put("firstName", "First name contains invalid characters.");
        }

        if (isNullOrBlank(parent.getLastName())) {
            errors.put("lastName", "Last name is required.");
        } else if (!isValidName(parent.getLastName())) {
            errors.put("lastName", "Last name contains invalid characters.");
        }

        if (isNullOrBlank(parent.getEmail())) {
            errors.put("email", "Email is required.");
        } else if (!isValidEmail(parent.getEmail())) {
            errors.put("email", "Invalid email format.");
        } else {
            String normalizedEmail = parent.getEmail().trim().toLowerCase();
            ParentAccounts existing = findByEmail(normalizedEmail);
            if (existing != null && !existing.getId().equals(parent.getId())) {
                errors.put("email", "This email is already used by another account.");
            }
        }

        if (!isNullOrBlank(parent.getPhoneNumber()) && !isValidPhoneNumber(parent.getPhoneNumber())) {
            errors.put("phoneNumber", "Invalid phone number format (10-15 digits, optional + prefix).");
        }

        if (parent.getBirthDate() != null && parent.getBirthDate().isAfter(LocalDate.now())) {
            errors.put("birthDate", "Birth date cannot be in the future.");
        }

        if (avatarFile != null && !avatarFile.isEmpty()) {
            if (avatarFile.getSize() > 5 * 1024 * 1024) {
                errors.put("avatar", "Avatar image must be less than 5MB.");
            }
            String contentType = avatarFile.getContentType();
            if (contentType == null || !contentType.matches("image/(jpeg|png|gif|webp)")) {
                errors.put("avatar", "Only JPG, PNG, GIF, WEBP images are allowed.");
            }
        }

        return errors;
    }

    @Override
    public void editParent(ParentAccounts parent, MultipartFile avatarFile) throws IOException {
        ParentAccounts existing = entityManager.find(ParentAccounts.class, parent.getId());
        if (existing == null) {
            throw new IllegalArgumentException("Parent not found with ID: " + parent.getId());
        }

        existing.setFirstName(parent.getFirstName().trim());
        existing.setLastName(parent.getLastName().trim());
        existing.setEmail(parent.getEmail().trim().toLowerCase());
        existing.setPhoneNumber(parent.getPhoneNumber() != null ? parent.getPhoneNumber().trim() : null);
        existing.setBirthDate(parent.getBirthDate());
        existing.setGender(parent.getGender());
        existing.setCountry(parent.getCountry());
        existing.setProvince(parent.getProvince());
        existing.setCity(parent.getCity());
        existing.setDistrict(parent.getDistrict());
        existing.setWard(parent.getWard());
        existing.setStreet(parent.getStreet());
        existing.setPostalCode(parent.getPostalCode());

        if (avatarFile != null && !avatarFile.isEmpty()) {
            existing.setAvatar(avatarFile.getBytes());
        }

        ParentAccounts merged = entityManager.merge(existing);

        // Gửi email thông báo chỉnh sửa
        sendParentEditEmail(merged);
    }

    @Override
    public Map<String, String> validateParent(ParentAccounts parent) {
        Map<String, String> errors = new HashMap<>();
        if (parent == null) {
            errors.put("general", "Parent account cannot be null");
            return errors;
        }
        if (!isNullOrBlank(parent.getFirstName()) && !isValidName(parent.getFirstName())) {
            errors.put("firstName", "Parent first name is not valid. Only letters, spaces, and standard punctuation are allowed.");
        }
        if (!isNullOrBlank(parent.getLastName()) && !isValidName(parent.getLastName())) {
            errors.put("lastName", "Parent last name is not valid. Only letters, spaces, and standard punctuation are allowed.");
        }
        if (!isNullOrBlank(parent.getEmail())) {
            if (!isValidEmail(parent.getEmail())) {
                errors.put("email", "Invalid parent email format.");
            } else if (personsService.existsByEmail(parent.getEmail())) {
                ParentAccounts existingParent = findByEmail(parent.getEmail());
                if (existingParent == null) {
                    errors.put("email", "The email address is already associated with another account type.");
                }
            }
        }
        if (!isNullOrBlank(parent.getPhoneNumber())) {
            if (!isValidPhoneNumber(parent.getPhoneNumber())) {
                errors.put("phoneNumber", "Invalid parent phone number format. Must be 10-15 digits, optionally starting with '+'.");
            } else if (personsService.existsByPhoneNumber(parent.getPhoneNumber())) {
                ParentAccounts existingParent = findByPhoneNumber(parent.getPhoneNumber());
                if (existingParent == null) {
                    errors.put("phoneNumber", "The phone number is already associated with another account.");
                }
            }
        }
        if (parent.getBirthDate() != null && parent.getBirthDate().isAfter(LocalDate.now())) {
            errors.put("birthDate", "Date of birth must be in the past.");
        }
        return errors;
    }

    @Override
    public Map<String, String> validateParentLink(String email, String supportPhoneNumber, String relationship, String parentLabel) {
        Map<String, String> errors = new HashMap<>();
        String prefix = parentLabel.toLowerCase().replace(" ", "_") + "_";

        if (!isAnyFieldProvided(email, supportPhoneNumber, relationship)) {
            return errors;
        }

        if (email == null || email.trim().isEmpty()) {
            errors.put(prefix + "email", parentLabel + ": Email is required when other parent fields are provided.");
            return errors;
        }

        ParentAccounts parent = new ParentAccounts();
        parent.setEmail(email);
        Map<String, String> parentErrors = validateParent(parent);
        parentErrors.forEach((key, value) -> errors.put(prefix + key, parentLabel + ": " + value));

        if (supportPhoneNumber != null && !supportPhoneNumber.trim().isEmpty()) {
            if (!supportPhoneNumber.matches("^\\+?[0-9]{10,15}$")) {
                errors.put(prefix + "supportPhoneNumber", parentLabel + ": Invalid support phone number format. Must be 10-15 digits, optionally starting with '+'.");
            }
        }

        if (relationship != null && !relationship.trim().isEmpty()) {
            try {
                RelationshipToStudent.valueOf(relationship.toUpperCase());
            } catch (IllegalArgumentException e) {
                errors.put(prefix + "relationship", parentLabel + ": Invalid relationship to student. Allowed values: " +
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
        if (!isAnyFieldProvided(email, supportPhoneNumber, relationship)) {
            return;
        }

        RelationshipToStudent relationshipEnum = relationship != null && !relationship.trim().isEmpty() ?
                RelationshipToStudent.valueOf(relationship.toUpperCase()) : null;

        ParentAccounts parent = findByEmail(email);
        boolean isNewParent = (parent == null);
        String parentPassword = null;

        if (isNewParent) {
            parent = new ParentAccounts();
            parent.setEmail(email);
            parent.setId(generateUniqueParentId());
            parent.setCreatedDate(LocalDate.now());

            Staffs currentStaff = staffsService.getStaff();
            if (currentStaff != null) {
                parent.setCreator(currentStaff);
            }

            addParentAccounts(parent);

            parentPassword = generateRandomPassword(12);
            Authenticators parentAuth = new Authenticators();
            parentAuth.setPersonId(parent.getId());
            parentAuth.setPerson(personsService.getPersonById(parent.getId()));
            parentAuth.setPassword(parentPassword);
            authenticatorsService.createAuthenticator(parentAuth);

            // Gửi email cho parent mới
            sendParentCreationEmail(parent, parentPassword);
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
        if (!isAnyFieldProvided(email, supportPhoneNumber, relationship)) {
            return;
        }

        RelationshipToStudent relationshipEnum = relationship != null && !relationship.trim().isEmpty() ?
                RelationshipToStudent.valueOf(relationship.toUpperCase()) : null;

        ParentAccounts parent = findByEmail(email);
        boolean isNewParent = (parent == null);
        String parentPassword = null;

        if (isNewParent) {
            parent = new ParentAccounts();
            parent.setEmail(email);
            parent.setId(generateUniqueParentId());
            parent.setCreatedDate(LocalDate.now());

            Staffs currentStaff = staffsService.getStaff();
            if (currentStaff != null) {
                parent.setCreator(currentStaff);
            }

            addParentAccounts(parent);

            parentPassword = generateRandomPassword(12);
            Authenticators parentAuth = new Authenticators();
            parentAuth.setPersonId(parent.getId());
            parentAuth.setPerson(personsService.getPersonById(parent.getId()));
            parentAuth.setPassword(parentPassword);
            authenticatorsService.createAuthenticator(parentAuth);

            // Gửi email cho parent mới
            sendParentCreationEmail(parent, parentPassword);
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

    // ==================== PRIVATE HELPER METHODS ====================

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