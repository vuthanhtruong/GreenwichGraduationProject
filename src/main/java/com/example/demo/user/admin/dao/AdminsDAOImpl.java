package com.example.demo.user.admin.dao;

import com.example.demo.campus.model.Campuses;
import com.example.demo.email_service.dto.AdminEmailContext;
import com.example.demo.email_service.service.EmailServiceForAdminService;
import com.example.demo.user.admin.model.Admins;
import com.example.demo.user.person.model.Persons;
import com.example.demo.user.person.service.PersonsService;
import com.example.demo.security.model.CustomOidcUserPrincipal;
import com.example.demo.security.model.DatabaseUserPrincipal;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
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
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Transactional
public class AdminsDAOImpl implements AdminsDAO {

    private static final Logger logger = LoggerFactory.getLogger(AdminsDAOImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    private final PersonsService personsService;
    private final EmailServiceForAdminService emailServiceForAdminService;

    public AdminsDAOImpl(PersonsService personsService, EmailServiceForAdminService emailServiceForAdminService) {
        this.personsService = personsService;
        this.emailServiceForAdminService = emailServiceForAdminService;
    }

    @Override
    public Map<String, String> validateAdmin(Admins admin, MultipartFile avatarFile) {
        Map<String, String> errors = new HashMap<>();

        if (admin.getFirstName() == null || !isValidName(admin.getFirstName())) {
            errors.put("firstName", "First name is not valid. Only letters, spaces, and standard punctuation are allowed.");
        }
        if (admin.getLastName() == null || !isValidName(admin.getLastName())) {
            errors.put("lastName", "Last name is not valid. Only letters, spaces, and standard punctuation are allowed.");
        }
        if (admin.getEmail() == null || !isValidEmail(admin.getEmail())) {
            errors.put("email", "Email is required and must be in a valid format.");
        }
        if (admin.getPhoneNumber() != null && !isValidPhoneNumber(admin.getPhoneNumber())) {
            errors.put("phoneNumber", "Phone number must be 10-15 digits, optionally starting with '+'.");
        }
        if (admin.getBirthDate() != null && admin.getBirthDate().isAfter(LocalDate.now())) {
            errors.put("birthDate", "Birth date must be in the past.");
        }
        if (admin.getEmail() != null && personsService.existsByEmailExcludingId(admin.getEmail(), admin.getId())) {
            errors.put("email", "The email address is already associated with another account.");
        }
        if (admin.getPhoneNumber() != null && personsService.existsByPhoneNumberExcludingId(admin.getPhoneNumber(), admin.getId())) {
            errors.put("phoneNumber", "The phone number is already associated with another account.");
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
        if (admin.getGender() == null) {
            errors.put("gender", "Gender is required.");
        }

        return errors;
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
            return true;
        }
        String phoneRegex = "^\\+?[1-9][0-9]{7,14}$";
        return phoneNumber.matches(phoneRegex);
    }

    private boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        String nameRegex = "^(?=.{2,100}$)(\\p{L}+[\\p{L}'’\\-\\.]*)((\\s+\\p{L}+[\\p{L}'’\\-\\.]*)*)$";
        return name.matches(nameRegex);
    }

    @Override
    public Admins getAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            logger.warn("No authenticated user found");
            throw new IllegalStateException("No authenticated user");
        }

        Object principal = auth.getPrincipal();
        Persons person = switch (principal) {
            case DatabaseUserPrincipal dbPrincipal -> dbPrincipal.getPerson();
            case CustomOidcUserPrincipal oidcPrincipal -> oidcPrincipal.getPerson();
            default -> throw new IllegalStateException("Unknown principal type: " + principal.getClass());
        };

        if (!(person instanceof Admins admin)) {
            logger.warn("Authenticated user is not an admin");
            throw new IllegalStateException("Authenticated user is not an admin");
        }

        Admins adminEntity = entityManager.find(Admins.class, admin.getId());
        if (adminEntity == null) {
            logger.warn("Admin with ID {} not found in database", admin.getId());
            throw new IllegalStateException("Admin not found");
        }
        return adminEntity;
    }

    @Override
    public Admins getAdminById(String id) {
        try {
            Admins admin = entityManager.find(Admins.class, id);
            if (admin == null) {
                logger.warn("Admin with ID {} not found", id);
            }
            return admin;
        } catch (Exception e) {
            logger.error("Error retrieving admin by ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error retrieving admin by ID: " + e.getMessage(), e);
        }
    }

    @Override
    public Admins getAdminByName(String name) {
        try {
            TypedQuery<Admins> query = entityManager.createQuery(
                    "SELECT a FROM Admins a WHERE LOWER(CONCAT(a.firstName, ' ', a.lastName)) LIKE LOWER(:name)",
                    Admins.class
            );
            query.setParameter("name", "%" + name + "%");
            return query.getSingleResult();
        } catch (NoResultException e) {
            logger.info("No admin found with name: {}", name);
            return null;
        } catch (Exception e) {
            logger.error("Error retrieving admin by name {}: {}", name, e.getMessage(), e);
            throw new RuntimeException("Error retrieving admin by name: " + e.getMessage(), e);
        }
    }

    @Override
    public Campuses getAdminCampus() {
        try {
            Admins admin = getAdmin();
            Campuses campus = admin.getCampus();
            if (campus == null) {
                logger.warn("No campus found for admin ID: {}", admin.getId());
            }
            return campus;
        } catch (Exception e) {
            logger.error("Error retrieving admin campus: {}", e.getMessage(), e);
            throw new RuntimeException("Error retrieving admin campus: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Admins> getAdmins() {
        try {
            TypedQuery<Admins> query = entityManager.createQuery(
                    "SELECT a FROM Admins a JOIN FETCH a.campus",
                    Admins.class
            );
            List<Admins> admins = query.getResultList();
            logger.info("Retrieved {} admins", admins.size());
            return admins;
        } catch (Exception e) {
            logger.error("Error retrieving list of admins: {}", e.getMessage(), e);
            throw new RuntimeException("Error retrieving list of admins: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Admins> getPaginatedAdmins(int firstResult, int pageSize) {
        if (pageSize <= 0 || firstResult < 0) {
            logger.warn("Invalid pagination parameters: firstResult={}, pageSize={}", firstResult, pageSize);
            return Collections.emptyList();
        }
        try {
            return entityManager.createQuery("SELECT a FROM Admins a JOIN FETCH a.campus", Admins.class)
                    .setFirstResult(firstResult)
                    .setMaxResults(pageSize)
                    .getResultList();
        } catch (Exception e) {
            logger.error("Error retrieving paginated admins: {}", e.getMessage(), e);
            throw new RuntimeException("Error retrieving paginated admins: " + e.getMessage(), e);
        }
    }

    @Override
    public long countAdmins() {
        try {
            return entityManager.createQuery("SELECT COUNT(a) FROM Admins a", Long.class)
                    .getSingleResult();
        } catch (Exception e) {
            logger.error("Error counting admins: {}", e.getMessage(), e);
            throw new RuntimeException("Error counting admins: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Admins> searchAdmins(String searchType, String keyword, int firstResult, int pageSize) {
        if (keyword == null || keyword.trim().isEmpty() || pageSize <= 0 || firstResult < 0) {
            logger.warn("Invalid search parameters: searchType={}, keyword={}, firstResult={}, pageSize={}",
                    searchType, keyword, firstResult, pageSize);
            return Collections.emptyList();
        }

        String queryString = "SELECT a FROM Admins a JOIN FETCH a.campus WHERE 1=1";
        if ("name".equalsIgnoreCase(searchType)) {
            keyword = keyword.toLowerCase().trim();
            String[] words = keyword.split("\\s+");
            StringBuilder nameCondition = new StringBuilder();
            for (int i = 0; i < words.length; i++) {
                if (i > 0) {
                    nameCondition.append(" AND ");
                }
                nameCondition.append("(LOWER(a.firstName) LIKE :word").append(i)
                        .append(" OR LOWER(a.lastName) LIKE :word").append(i).append(")");
            }
            queryString += " AND (" + nameCondition + ")";
        } else if ("id".equalsIgnoreCase(searchType)) {
            queryString += " AND LOWER(a.id) = LOWER(:keyword)";
        } else {
            logger.warn("Invalid search type: {}", searchType);
            return Collections.emptyList();
        }

        try {
            TypedQuery<Admins> query = entityManager.createQuery(queryString, Admins.class)
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
        } catch (Exception e) {
            logger.error("Error searching admins: {}", e.getMessage(), e);
            throw new RuntimeException("Error searching admins: " + e.getMessage(), e);
        }
    }

    @Override
    public long countSearchResults(String searchType, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            logger.warn("Empty keyword for search");
            return 0L;
        }

        String queryString = "SELECT COUNT(a) FROM Admins a WHERE 1=1";
        if ("name".equalsIgnoreCase(searchType)) {
            keyword = keyword.toLowerCase().trim();
            String[] words = keyword.split("\\s+");
            StringBuilder nameCondition = new StringBuilder();
            for (int i = 0; i < words.length; i++) {
                if (i > 0) {
                    nameCondition.append(" AND ");
                }
                nameCondition.append("(LOWER(a.firstName) LIKE :word").append(i)
                        .append(" OR LOWER(a.lastName) LIKE :word").append(i).append(")");
            }
            queryString += " AND (" + nameCondition + ")";
        } else if ("id".equalsIgnoreCase(searchType)) {
            queryString += " AND LOWER(a.id) = LOWER(:keyword)";
        } else {
            logger.warn("Invalid search type: {}", searchType);
            return 0L;
        }

        try {
            TypedQuery<Long> query = entityManager.createQuery(queryString, Long.class);
            if ("name".equalsIgnoreCase(searchType)) {
                String[] words = keyword.split("\\s+");
                for (int i = 0; i < words.length; i++) {
                    query.setParameter("word" + i, "%" + words[i] + "%");
                }
            } else if ("id".equalsIgnoreCase(searchType)) {
                query.setParameter("keyword", keyword.trim());
            }

            return query.getSingleResult();
        } catch (Exception e) {
            logger.error("Error counting search results: {}", e.getMessage(), e);
            throw new RuntimeException("Error counting search results: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateAdminId(LocalDate date) {
        if (date == null) {
            logger.warn("Date is null, using current date for admin ID generation");
            date = LocalDate.now();
        }
        String prefix = "ADM" + (date.getYear() % 100);
        String jpql = "SELECT COUNT(a) FROM Admins a WHERE a.id LIKE :prefix";
        try {
            long count = entityManager.createQuery(jpql, Long.class)
                    .setParameter("prefix", prefix + "%")
                    .getSingleResult();
            return prefix + String.format("%04d", count + 1);
        } catch (Exception e) {
            logger.error("Error generating admin ID: {}", e.getMessage(), e);
            throw new RuntimeException("Error generating admin ID: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateRandomPassword(int length) {
        if (length < 8) {
            logger.warn("Requested password length {} is less than minimum 8", length);
            throw new IllegalArgumentException("Password length must be at least 8 characters.");
        }
        String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String symbols = "!@#$%^&*()-_+=<>?";
        String allChars = upperCase + lowerCase + digits + symbols;
        Random random = new Random();
        StringBuilder password = new StringBuilder();

        // Ensure at least one of each required character type
        password.append(upperCase.charAt(random.nextInt(upperCase.length())));
        password.append(lowerCase.charAt(random.nextInt(lowerCase.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(symbols.charAt(random.nextInt(symbols.length())));

        for (int i = 4; i < length; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        // Shuffle the password to randomize character positions
        List<Character> chars = password.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.toList());
        Collections.shuffle(chars, random);
        String finalPassword = chars.stream().map(String::valueOf).collect(Collectors.joining());

        logger.info("Generated random password for admin");
        return finalPassword;
    }

    @Override
    public void addAdmin(Admins admin, String rawPassword) {
        try {
            Admins currentAdmin = getAdmin();
            admin.setCreatedDate(LocalDate.now().atStartOfDay());
            Admins saved = entityManager.merge(admin);

            // Send email
            AdminEmailContext context = new AdminEmailContext(
                    saved.getId(),
                    saved.getFullName(),
                    saved.getEmail(),
                    saved.getPhoneNumber(),
                    saved.getBirthDate(),
                    saved.getGender() != null ? saved.getGender().toString() : null,
                    saved.getFullAddress(),
                    saved.getCampus() != null ? saved.getCampus().getCampusName() : null,
                    currentAdmin.getFullName(),
                    saved.getCreatedDate()
            );

            String subject = "Your Admin Account Has Been Created";
            emailServiceForAdminService.sendEmailToNotifyLoginInformation(
                    saved.getEmail(), subject, context, rawPassword);

            logger.info("Added new admin ID: {} by {}", saved.getId(), currentAdmin.getId());
        } catch (Exception e) {
            logger.error("Error adding admin: {}", e.getMessage(), e);
            throw new RuntimeException("Error adding admin: " + e.getMessage(), e);
        }
    }

    @Override
    public void editAdmin(Admins admin, MultipartFile avatarFile) throws IOException {
        try {
            Admins existingAdmin = entityManager.find(Admins.class, admin.getId());
            if (existingAdmin == null) {
                logger.warn("Admin with ID {} not found for editing", admin.getId());
                throw new IllegalArgumentException("Admin with ID " + admin.getId() + " not found");
            }

            Admins authenticatedAdmin = getAdmin();
            if (!authenticatedAdmin.getId().equals(admin.getId())) {
                logger.warn("Authenticated admin ID {} attempted to edit admin ID {}",
                        authenticatedAdmin.getId(), admin.getId());
                throw new IllegalStateException("Not authorized to edit this admin profile");
            }

            if (admin.getFirstName() != null) existingAdmin.setFirstName(admin.getFirstName());
            if (admin.getLastName() != null) existingAdmin.setLastName(admin.getLastName());
            if (admin.getEmail() != null) existingAdmin.setEmail(admin.getEmail());
            if (admin.getPhoneNumber() != null) existingAdmin.setPhoneNumber(admin.getPhoneNumber());
            if (admin.getBirthDate() != null) existingAdmin.setBirthDate(admin.getBirthDate());
            if (admin.getGender() != null) existingAdmin.setGender(admin.getGender());
            if (admin.getCountry() != null) existingAdmin.setCountry(admin.getCountry());
            if (admin.getProvince() != null) existingAdmin.setProvince(admin.getProvince());
            if (admin.getCity() != null) existingAdmin.setCity(admin.getCity());
            if (admin.getDistrict() != null) existingAdmin.setDistrict(admin.getDistrict());
            if (admin.getWard() != null) existingAdmin.setWard(admin.getWard());
            if (admin.getStreet() != null) existingAdmin.setStreet(admin.getStreet());
            if (admin.getPostalCode() != null) existingAdmin.setPostalCode(admin.getPostalCode());
            if (avatarFile != null && !avatarFile.isEmpty()) {
                existingAdmin.setAvatar(avatarFile.getBytes());
            }

            entityManager.merge(existingAdmin);
            logger.info("Admin ID {} updated successfully", admin.getId());
        } catch (IOException e) {
            logger.error("IO error updating admin ID {}: {}", admin.getId(), e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Error updating admin ID {}: {}", admin.getId(), e.getMessage(), e);
            throw new RuntimeException("Error updating admin: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteAdmin(String id) {
        try {
            Admins admin = entityManager.find(Admins.class, id);
            if (admin == null) {
                logger.warn("Admin with ID {} not found for deletion", id);
                throw new IllegalArgumentException("Admin with ID " + id + " not found");
            }

            // Send deletion email
            AdminEmailContext context = new AdminEmailContext(
                    admin.getId(),
                    admin.getFullName(),
                    admin.getEmail(),
                    admin.getPhoneNumber(),
                    admin.getBirthDate(),
                    admin.getGender() != null ? admin.getGender().toString() : null,
                    admin.getFullAddress(),
                    admin.getCampus() != null ? admin.getCampus().getCampusName() : null,
                    admin.getCreator() != null ? admin.getCreator().getFullName() : null,
                    admin.getCreatedDate()
            );

            String subject = "Your Admin Account Has Been Deactivated";
            try {
                emailServiceForAdminService.sendEmailToNotifyLoginInformation(
                        admin.getEmail(), subject, context, null);
                logger.info("Sent deactivation email to {}", admin.getEmail());
            } catch (Exception e) {
                logger.warn("Failed to send deletion email to {}: {}", admin.getEmail(), e.getMessage());
            }

            entityManager.remove(admin);
            logger.info("Deleted admin ID: {}", id);
        } catch (Exception e) {
            logger.error("Error deleting admin ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error deleting admin: " + e.getMessage(), e);
        }
    }
}