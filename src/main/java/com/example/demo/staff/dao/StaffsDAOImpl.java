package com.example.demo.staff.dao;
import com.example.demo.admin.model.Admins;
import com.example.demo.admin.service.AdminsService;
import com.example.demo.campus.service.CampusesService;
import com.example.demo.classes.model.MajorClasses;
import com.example.demo.major.model.Majors;
import com.example.demo.person.model.Persons;
import com.example.demo.security.model.CustomOidcUserPrincipal;
import com.example.demo.security.model.DatabaseUserPrincipal;
import com.example.demo.security.model.OAuth2UserPrincipal;
import com.example.demo.staff.model.Staffs;
import com.example.demo.major.service.MajorsService;
import com.example.demo.person.service.PersonsService;
import jakarta.mail.MessagingException;
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
public class StaffsDAOImpl implements StaffsDAO {
    @Override
    public long countSearchResults(String searchType, String keyword) {
        try {
            String queryString = "SELECT COUNT(s) FROM Staffs s WHERE ";
            if ("name".equalsIgnoreCase(searchType)) {
                queryString += "LOWER(s.firstName) LIKE LOWER(:keyword) OR LOWER(s.lastName) LIKE LOWER(:keyword)";
            } else if ("id".equalsIgnoreCase(searchType)) {
                queryString += "s.id = :keyword";
            } else {
                return 0;
            }
            TypedQuery<Long> query = entityManager.createQuery(queryString, Long.class)
                    .setParameter("keyword", "id".equalsIgnoreCase(searchType) ? keyword : "%" + keyword + "%");
            return query.getSingleResult();
        } catch (Exception e) {
            logger.error("Error counting search results: {}", e.getMessage());
            throw new RuntimeException("Error counting search results: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Staffs> searchStaffs(String searchType, String keyword, int firstResult, int pageSize) {
        try {
            String queryString = "SELECT s FROM Staffs s WHERE ";
            if ("name".equalsIgnoreCase(searchType)) {
                queryString += "LOWER(s.firstName) LIKE LOWER(:keyword) OR LOWER(s.lastName) LIKE LOWER(:keyword)";
            } else if ("id".equalsIgnoreCase(searchType)) {
                queryString += "s.id = :keyword";
            } else {
                return new ArrayList<>();
            }
            TypedQuery<Staffs> query = entityManager.createQuery(queryString, Staffs.class)
                    .setParameter("keyword", "id".equalsIgnoreCase(searchType) ? keyword : "%" + keyword + "%")
                    .setFirstResult(firstResult)
                    .setMaxResults(pageSize);
            return query.getResultList();
        } catch (Exception e) {
            logger.error("Error searching staff: {}", e.getMessage());
            throw new RuntimeException("Error searching staff: " + e.getMessage(), e);
        }
    }


    public long numberOfStaffs() {
        return entityManager.createQuery(
                        "SELECT COUNT(s) FROM Staffs s", Long.class)
                .getSingleResult();
    }

    private static final Logger logger = LoggerFactory.getLogger(StaffsDAOImpl.class);
    private final PersonsService personsService;
    private final AdminsService adminsService;
    private final CampusesService campusesService;
    private final MajorsService majorsService;

    @PersistenceContext
    private EntityManager entityManager;

    public StaffsDAOImpl(PersonsService personsService, AdminsService adminsService, CampusesService campusesService, MajorsService majorsService) {
        this.personsService = personsService;
        this.adminsService = adminsService;
        this.campusesService = campusesService;
        this.majorsService = majorsService;
    }

    @Override
    public Staffs getStaff() {
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

        if (!(person instanceof Staffs staff)) {
            throw new IllegalStateException("Authenticated user is not a staff");
        }

        return staff;
    }

    @Override
    public Majors getStaffMajor() {
        try {
            Staffs staff = getStaff();
            Majors major = staff.getMajorManagement();
            return major;
        } catch (Exception e) {
            logger.error("Error retrieving staff major: {}", e.getMessage());
            throw new RuntimeException("Error retrieving staff major: " + e.getMessage(), e);
        }
    }

    @Override
    public List<MajorClasses> getClasses() {
        try {
            Staffs staff = getStaff();
            TypedQuery<MajorClasses> query = entityManager.createQuery(
                            "SELECT c FROM MajorClasses c WHERE c.creator = :staff OR c.subject.major = :major",
                            MajorClasses.class)
                    .setParameter("staff", staff)
                    .setParameter("major", staff.getMajorManagement());
            List<MajorClasses> classes = query.getResultList();
            logger.info("Retrieved {} classes for staff ID: {}", classes.size(), staff.getId());
            return classes;
        } catch (Exception e) {
            logger.error("Error retrieving classes: {}", e.getMessage());
            throw new RuntimeException("Error retrieving classes: " + e.getMessage(), e);
        }
    }

    @Override
    public void addStaff(Staffs staff, String randomPassword) {
        try {
            Admins currentAdmin = adminsService.getAdmin();
            staff.setCreator(currentAdmin);
            entityManager.merge(staff);
            logger.info("Added new staff with ID: {} by admin ID: {}", staff.getId(), currentAdmin.getId());
        } catch (Exception e) {
            logger.error("Error adding staff: {}", e.getMessage());
            throw new RuntimeException("Error adding staff: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Staffs> getStaffs() {
        try {
            TypedQuery<Staffs> query = entityManager.createQuery(
                            "SELECT s FROM Staffs s",
                            Staffs.class);
            List<Staffs> staffs = query.getResultList();
            return staffs;
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving staff list: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Staffs> getPaginatedStaffs(int firstResult, int pageSize) {
        try {
            TypedQuery<Staffs> query = entityManager.createQuery(
                            "SELECT s FROM Staffs s",
                            Staffs.class)
                    .setFirstResult(firstResult)
                    .setMaxResults(pageSize);
            List<Staffs> staffs = query.getResultList();
            return staffs;
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving paginated staff list: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteStaff(String id) {
        try {
            Staffs staff = entityManager.find(Staffs.class, id);
            entityManager.remove(staff);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting staff with ID " + id + ": " + e.getMessage(), e);
        }
    }

    @Override
    public Staffs getStaffById(String id) {
        try {
            Staffs staff = entityManager.find(Staffs.class, id);
            return staff;
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving staff by ID " + id + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void editStaff(Staffs staff, MultipartFile avatarFile) throws IOException, MessagingException {
        try {
            Staffs existingStaff = entityManager.find(Staffs.class, staff.getId());
            if (existingStaff == null) {
                throw new IllegalArgumentException("Staff with ID " + staff.getId() + " not found");
            }
            if (avatarFile != null && !avatarFile.isEmpty()) {
                staff.setAvatar(avatarFile.getBytes());
            } else {
                staff.setAvatar(existingStaff.getAvatar());
            }
            editStaffFields(existingStaff, staff);
            entityManager.merge(existingStaff);
            String subject = "Your Staff Account Information After Being Edited";
        } catch (IOException | org.springframework.messaging.MessagingException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error updating staff: " + e.getMessage(), e);
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
        return chars.stream().map(String::valueOf).collect(Collectors.joining());
    }

    @Override
    public String generateUniqueStaffId(String majorId, LocalDate createdDate) {
        String prefix = majorId != null ? majorId : "STF";
        String year = String.format("%02d", createdDate.getYear() % 100);
        String date = String.format("%02d%02d", createdDate.getMonthValue(), createdDate.getDayOfMonth());
        String staffId;
        SecureRandom random = new SecureRandom();
        do {
            String randomDigit = String.valueOf(random.nextInt(10));
            staffId = prefix + year + date + randomDigit;
        } while (personsService.existsPersonById(staffId));
        return staffId;
    }

    private void editStaffFields(Staffs existing, Staffs editd) {
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
        if (editd.getMajorManagement() != null) existing.setMajorManagement(editd.getMajorManagement());
        if (editd.getCampus() != null) existing.setCampus(editd.getCampus());
    }

    @Override
    public Map<String, String> validateStaff(Staffs staff, MultipartFile avatarFile, String majorId, String campusId) {
        Map<String, String> errors = new HashMap<>();

        if (staff.getFirstName() == null || !isValidName(staff.getFirstName())) {
            errors.put("firstName", "First name is not valid. Only letters, spaces, and standard punctuation are allowed.");
        }
        if (staff.getLastName() == null || !isValidName(staff.getLastName())) {
            errors.put("lastName", "Last name is not valid. Only letters, spaces, and standard punctuation are allowed.");
        }
        if (staff.getEmail() == null || !isValidEmail(staff.getEmail())) {
            errors.put("email", "Email is required and must be in a valid format.");
        }
        if (staff.getPhoneNumber() == null || !isValidPhoneNumber(staff.getPhoneNumber())) {
            errors.put("phoneNumber", "Phone number is required and must be 10-15 digits, optionally starting with '+'.");
        }
        if (staff.getBirthDate() != null && staff.getBirthDate().isAfter(LocalDate.now())) {
            errors.put("birthDate", "Date of birth must be in the past.");
        }
        if (staff.getEmail() != null && personsService.existsByEmailExcludingId(staff.getEmail(), staff.getId() != null ? staff.getId() : "")) {
            errors.put("email", "The email address is already associated with another account.");
        }
        if (staff.getPhoneNumber() != null && personsService.existsByPhoneNumberExcludingId(staff.getPhoneNumber(), staff.getId() != null ? staff.getId() : "")) {
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
        if (staff.getGender() == null) {
            errors.put("gender", "Gender is required to assign a default avatar.");
        }
        if (majorId == null || majorId.isEmpty() || majorsService.getByMajorId(majorId) == null) {
            errors.put("majorId", "A valid major must be selected.");
        }
        if (campusId == null || campusId.isEmpty() || campusesService.getCampusById(campusId) == null) {
            errors.put("campusId", "A valid campus must be selected.");
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

}