package com.example.demo.user.deputyStaff.dao;

import com.example.demo.security.model.CustomOidcUserPrincipal;
import com.example.demo.security.model.DatabaseUserPrincipal;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import com.example.demo.user.admin.model.Admins;
import com.example.demo.user.admin.service.AdminsService;
import com.example.demo.campus.service.CampusesService;
import com.example.demo.user.majorLecturer.model.MajorLecturers;
import com.example.demo.user.person.model.Persons;
import com.example.demo.user.person.service.PersonsService;
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
public class DeputyStaffsDAOImpl implements DeputyStaffsDAO {
    @Override
    public DeputyStaffs getDeputyStaff() {
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

        if (!(person instanceof DeputyStaffs)) {
            throw new IllegalStateException("Authenticated user is not a student");
        }
        return entityManager.find(DeputyStaffs.class, person.getId());
    }

    private static final Logger logger = LoggerFactory.getLogger(DeputyStaffsDAOImpl.class);
    private final PersonsService personsService;
    private final AdminsService adminsService;
    private final CampusesService campusesService;

    @PersistenceContext
    private EntityManager entityManager;

    public DeputyStaffsDAOImpl(PersonsService personsService, AdminsService adminsService, CampusesService campusesService) {
        this.personsService = personsService;
        this.adminsService = adminsService;
        this.campusesService = campusesService;
    }

    @Override
    public List<DeputyStaffs> getDeputyStaffs() {
        try {
            TypedQuery<DeputyStaffs> query = entityManager.createQuery(
                    "SELECT s FROM DeputyStaffs s",
                    DeputyStaffs.class);
            List<DeputyStaffs> deputyStaffs = query.getResultList();
            logger.info("Retrieved {} deputy staffs", deputyStaffs.size());
            return deputyStaffs;
        } catch (Exception e) {
            logger.error("Error retrieving deputy staff list: {}", e.getMessage());
            throw new RuntimeException("Error retrieving deputy staff list: " + e.getMessage(), e);
        }
    }

    @Override
    public List<DeputyStaffs> getPaginatedDeputyStaffs(int firstResult, int pageSize) {
        try {
            TypedQuery<DeputyStaffs> query = entityManager.createQuery(
                            "SELECT s FROM DeputyStaffs s",
                            DeputyStaffs.class)
                    .setFirstResult(firstResult)
                    .setMaxResults(pageSize);
            List<DeputyStaffs> deputyStaffs = query.getResultList();
            logger.info("Retrieved {} deputy staffs for page starting at {}", deputyStaffs.size(), firstResult);
            return deputyStaffs;
        } catch (Exception e) {
            logger.error("Error retrieving paginated deputy staff list: {}", e.getMessage());
            throw new RuntimeException("Error retrieving paginated deputy staff list: " + e.getMessage(), e);
        }
    }

    @Override
    public long numberOfDeputyStaffs() {
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                    "SELECT COUNT(s) FROM DeputyStaffs s",
                    Long.class);
            long count = query.getSingleResult();
            logger.info("Total deputy staffs: {}", count);
            return count;
        } catch (Exception e) {
            logger.error("Error counting deputy staffs: {}", e.getMessage());
            throw new RuntimeException("Error counting deputy staffs: " + e.getMessage(), e);
        }
    }

    @Override
    public void addDeputyStaff(DeputyStaffs deputyStaff, String randomPassword) {
        try {
            Admins currentAdmin = adminsService.getAdmin();
            deputyStaff.setCreator(currentAdmin);
            entityManager.merge(deputyStaff);
            logger.info("Added new deputy staff with ID: {} by admin ID: {}", deputyStaff.getId(), currentAdmin.getId());
        } catch (Exception e) {
            logger.error("Error adding deputy staff: {}", e.getMessage());
            throw new RuntimeException("Error adding deputy staff: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteDeputyStaff(String id) {
        try {
            DeputyStaffs deputyStaff = entityManager.find(DeputyStaffs.class, id);
            if (deputyStaff != null) {
                entityManager.remove(deputyStaff);
                logger.info("Deleted deputy staff with ID: {}", id);
            } else {
                logger.warn("Deputy staff with ID {} not found for deletion", id);
            }
        } catch (Exception e) {
            logger.error("Error deleting deputy staff with ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error deleting deputy staff with ID " + id + ": " + e.getMessage(), e);
        }
    }

    @Override
    public DeputyStaffs getDeputyStaffById(String id) {
        try {
            DeputyStaffs deputyStaff = entityManager.find(DeputyStaffs.class, id);
            if (deputyStaff == null) {
                logger.warn("Deputy staff with ID {} not found", id);
            }
            return deputyStaff;
        } catch (Exception e) {
            logger.error("Error retrieving deputy staff by ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error retrieving deputy staff by ID " + id + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void editDeputyStaff(DeputyStaffs deputyStaff, MultipartFile avatarFile) throws IOException {
        try {
            DeputyStaffs existingDeputyStaff = entityManager.find(DeputyStaffs.class, deputyStaff.getId());
            if (existingDeputyStaff == null) {
                throw new IllegalArgumentException("Deputy staff with ID " + deputyStaff.getId() + " not found");
            }
            if (avatarFile != null && !avatarFile.isEmpty()) {
                deputyStaff.setAvatar(avatarFile.getBytes());
            } else {
                deputyStaff.setAvatar(existingDeputyStaff.getAvatar());
            }
            editDeputyStaffFields(existingDeputyStaff, deputyStaff);
            entityManager.merge(existingDeputyStaff);
            logger.info("Updated deputy staff with ID: {}", deputyStaff.getId());
        } catch (IOException e) {
            logger.error("IO error updating deputy staff with ID {}: {}", deputyStaff.getId(), e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error updating deputy staff with ID {}: {}", deputyStaff.getId(), e.getMessage());
            throw new RuntimeException("Unexpected error updating deputy staff: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateRandomPassword(int length) {
        if (length < 8) {
            throw new IllegalArgumentException("Password length must be at least 8 characters.");
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
        String generatedPassword = chars.stream().map(String::valueOf).collect(Collectors.joining());
        logger.info("Generated random password for deputy staff");
        return generatedPassword;
    }

    @Override
    public String generateUniqueDeputyStaffId(LocalDate createdDate) {
        String year = String.format("%02d", createdDate.getYear() % 100);
        String date = String.format("%02d%02d", createdDate.getMonthValue(), createdDate.getDayOfMonth());
        String deputyStaffId;
        SecureRandom random = new SecureRandom();
        do {
            String randomDigit = String.valueOf(random.nextInt(10));
            deputyStaffId = year + date + randomDigit;
        } while (personsService.existsPersonById(deputyStaffId));
        logger.info("Generated unique deputy staff ID: {}", deputyStaffId);
        return deputyStaffId;
    }

    @Override
    public Map<String, String> validateDeputyStaff(DeputyStaffs deputyStaff, MultipartFile avatarFile, String campusId) {
        Map<String, String> errors = new HashMap<>();

        if (deputyStaff.getFirstName() == null || !isValidName(deputyStaff.getFirstName())) {
            errors.put("firstName", "First name is not valid. Only letters, spaces, and standard punctuation are allowed.");
        }
        if (deputyStaff.getLastName() == null || !isValidName(deputyStaff.getLastName())) {
            errors.put("lastName", "Last name is not valid. Only letters, spaces, and standard punctuation are allowed.");
        }
        if (deputyStaff.getEmail() == null || !isValidEmail(deputyStaff.getEmail())) {
            errors.put("email", "Email is required and must be in a valid format.");
        }
        if (deputyStaff.getPhoneNumber() == null || !isValidPhoneNumber(deputyStaff.getPhoneNumber())) {
            errors.put("phoneNumber", "Phone number is required and must be 10-15 digits, optionally starting with '+'.");
        }
        if (deputyStaff.getBirthDate() != null && deputyStaff.getBirthDate().isAfter(LocalDate.now())) {
            errors.put("birthDate", "Date of birth must be in the past.");
        }
        if (deputyStaff.getEmail() != null && personsService.existsByEmailExcludingId(deputyStaff.getEmail(), deputyStaff.getId() != null ? deputyStaff.getId() : "")) {
            errors.put("email", "The email address is already associated with another account.");
        }
        if (deputyStaff.getPhoneNumber() != null && personsService.existsByPhoneNumberExcludingId(deputyStaff.getPhoneNumber(), deputyStaff.getId() != null ? deputyStaff.getId() : "")) {
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
        if (deputyStaff.getGender() == null) {
            errors.put("gender", "Gender is required to assign a default avatar.");
        }
        if (campusId == null || campusId.isEmpty() || campusesService.getCampusById(campusId) == null) {
            errors.put("campusId", "A valid campus must be selected.");
        }

        if (!errors.isEmpty()) {
            logger.warn("Validation errors for deputy staff: {}", errors);
        }
        return errors;
    }

    @Override
    public List<DeputyStaffs> searchDeputyStaffs(String searchType, String keyword, int firstResult, int pageSize) {
        try {
            String queryString = "SELECT s FROM DeputyStaffs s WHERE ";
            if ("name".equalsIgnoreCase(searchType)) {
                queryString += "LOWER(s.firstName) LIKE LOWER(:keyword) OR LOWER(s.lastName) LIKE LOWER(:keyword)";
            } else if ("id".equalsIgnoreCase(searchType)) {
                queryString += "s.id = :keyword";
            } else {
                logger.warn("Invalid search type: {}", searchType);
                return new ArrayList<>();
            }
            TypedQuery<DeputyStaffs> query = entityManager.createQuery(queryString, DeputyStaffs.class)
                    .setParameter("keyword", "id".equalsIgnoreCase(searchType) ? keyword : "%" + keyword + "%")
                    .setFirstResult(firstResult)
                    .setMaxResults(pageSize);
            List<DeputyStaffs> deputyStaffs = query.getResultList();
            logger.info("Found {} deputy staffs for search type: {}, keyword: {}", deputyStaffs.size(), searchType, keyword);
            return deputyStaffs;
        } catch (Exception e) {
            logger.error("Error searching deputy staff: {}", e.getMessage());
            throw new RuntimeException("Error searching deputy staff: " + e.getMessage(), e);
        }
    }

    @Override
    public long countSearchResults(String searchType, String keyword) {
        try {
            String queryString = "SELECT COUNT(s) FROM DeputyStaffs s WHERE ";
            if ("name".equalsIgnoreCase(searchType)) {
                queryString += "LOWER(s.firstName) LIKE LOWER(:keyword) OR LOWER(s.lastName) LIKE LOWER(:keyword)";
            } else if ("id".equalsIgnoreCase(searchType)) {
                queryString += "s.id = :keyword";
            } else {
                logger.warn("Invalid search type for count: {}", searchType);
                return 0;
            }
            TypedQuery<Long> query = entityManager.createQuery(queryString, Long.class)
                    .setParameter("keyword", "id".equalsIgnoreCase(searchType) ? keyword : "%" + keyword + "%");
            long count = query.getSingleResult();
            logger.info("Counted {} deputy staffs for search type: {}, keyword: {}", count, searchType, keyword);
            return count;
        } catch (Exception e) {
            logger.error("Error counting search results: {}", e.getMessage());
            throw new RuntimeException("Error counting search results: " + e.getMessage(), e);
        }
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


    private void editDeputyStaffFields(DeputyStaffs existing, DeputyStaffs edited) {
        if (edited.getFirstName() != null) existing.setFirstName(edited.getFirstName());
        if (edited.getLastName() != null) existing.setLastName(edited.getLastName());
        if (edited.getEmail() != null) existing.setEmail(edited.getEmail());
        if (edited.getPhoneNumber() != null) existing.setPhoneNumber(edited.getPhoneNumber());
        if (edited.getBirthDate() != null) existing.setBirthDate(edited.getBirthDate());
        if (edited.getGender() != null) existing.setGender(edited.getGender());
        if (edited.getCountry() != null) existing.setCountry(edited.getCountry());
        if (edited.getProvince() != null) existing.setProvince(edited.getProvince());
        if (edited.getCity() != null) existing.setCity(edited.getCity());
        if (edited.getDistrict() != null) existing.setDistrict(edited.getDistrict());
        if (edited.getWard() != null) existing.setWard(edited.getWard());
        if (edited.getStreet() != null) existing.setStreet(edited.getStreet());
        if (edited.getPostalCode() != null) existing.setPostalCode(edited.getPostalCode());
        if (edited.getAvatar() != null) existing.setAvatar(edited.getAvatar());
        if (edited.getCampus() != null) existing.setCampus(edited.getCampus());
        logger.info("Updated fields for deputy staff ID: {}", existing.getId());
    }
}