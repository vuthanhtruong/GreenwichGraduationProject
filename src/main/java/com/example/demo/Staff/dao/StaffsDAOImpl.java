package com.example.demo.Staff.dao;
import com.example.demo.Staff.service.StaffsService;
import com.example.demo.admin.model.Admins;
import com.example.demo.admin.service.AdminsService;
import com.example.demo.campus.service.CampusesService;
import com.example.demo.classes.model.MajorClasses;
import com.example.demo.major.model.Majors;
import com.example.demo.Staff.model.Staffs;
import com.example.demo.major.service.MajorsService;
import com.example.demo.security.model.CustomUserPrincipal;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Transactional
public class StaffsDAOImpl implements StaffsDAO {
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
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !(auth.getPrincipal() instanceof CustomUserPrincipal principal)) {
                throw new IllegalStateException("No authenticated principal");
            }
            return (Staffs) principal.getPerson();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving current staff: " + e.getMessage(), e);
        }
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
    public List<String> validateStaff(Staffs staff, MultipartFile avatarFile, String majorId, String campusId) {
        List<String> errors = new ArrayList<>();
        if (staff.getFirstName() == null || !isValidName(staff.getFirstName())) {
            errors.add("First name is not valid. Only letters, spaces, and standard punctuation are allowed.");
        }
        if (staff.getLastName() == null || !isValidName(staff.getLastName())) {
            errors.add("Last name is not valid. Only letters, spaces, and standard punctuation are allowed.");
        }
        if (staff.getEmail() == null || !isValidEmail(staff.getEmail())) {
            errors.add("Email is required and must be in a valid format.");
        }
        if (staff.getPhoneNumber() == null || !isValidPhoneNumber(staff.getPhoneNumber())) {
            errors.add("Phone number is required and must be 10-15 digits, optionally starting with '+'.");
        }
        if (staff.getBirthDate() != null && staff.getBirthDate().isAfter(LocalDate.now())) {
            errors.add("Date of birth must be in the past.");
        }
        if (staff.getEmail() != null && personsService.existsByEmailExcludingId(staff.getEmail(), staff.getId() != null ? staff.getId() : "")) {
            errors.add("The email address is already associated with another account.");
        }
        if (staff.getPhoneNumber() != null && personsService.existsByPhoneNumberExcludingId(staff.getPhoneNumber(), staff.getId() != null ? staff.getId() : "")) {
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
        if (staff.getGender() == null) {
            errors.add("Gender is required to assign a default avatar.");
        }
        if (majorId == null || majorId.isEmpty() || majorsService.getByMajorId(majorId) == null) {
            errors.add("A valid major must be selected.");
        }
        if (campusId == null || campusId.isEmpty() || campusesService.getCampusById(campusId) == null) {
            errors.add("A valid campus must be selected.");
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
}