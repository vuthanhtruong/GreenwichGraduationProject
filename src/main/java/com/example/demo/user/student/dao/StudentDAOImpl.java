package com.example.demo.user.student.dao;

import com.example.demo.curriculum.model.Curriculum;
import com.example.demo.specialization.model.Specialization;
import com.example.demo.email_service.dto.StudentEmailContext;
import com.example.demo.email_service.service.EmailServiceForLecturerService;
import com.example.demo.email_service.service.EmailServiceForStudentService;
import com.example.demo.major.model.Majors;
import com.example.demo.user.person.model.Persons;
import com.example.demo.security.model.CustomOidcUserPrincipal;
import com.example.demo.security.model.DatabaseUserPrincipal;
import com.example.demo.user.staff.model.Staffs;
import com.example.demo.user.staff.service.StaffsService;
import com.example.demo.user.person.service.PersonsService;
import com.example.demo.user.student.model.Students;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Transactional
public class StudentDAOImpl implements StudentsDAO {
    @PersistenceContext
    private EntityManager entityManager;

    private final StaffsService staffsService;
    private final PersonsService personsService;
    private final EmailServiceForStudentService emailServiceForStudentService;

    public StudentDAOImpl(PersonsService personsService, EmailServiceForStudentService emailServiceForStudentService,
                          EmailServiceForLecturerService emailServiceForLectureService,
                          StaffsService staffsService) {
        this.personsService = personsService;
        if (emailServiceForStudentService == null || emailServiceForLectureService == null) {
            throw new IllegalArgumentException("Email services cannot be null");
        }
        this.emailServiceForStudentService = emailServiceForStudentService;
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
    public Map<String, String> StudentValidation(Students student, MultipartFile avatarFile) {
        Map<String, String> errors = new HashMap<>();

        if (student.getFirstName() == null || !isValidName(student.getFirstName())) {
            errors.put("firstName", "First name is not valid. Only letters, spaces, and standard punctuation are allowed.");
        }
        if (student.getLastName() == null || !isValidName(student.getLastName())) {
            errors.put("lastName", "Last name is not valid. Only letters, spaces, and standard punctuation are allowed.");
        }
        if (student.getEmail() != null && !isValidEmail(student.getEmail())) {
            errors.put("email", "Invalid email format.");
        }
        if (student.getPhoneNumber() != null && !isValidPhoneNumber(student.getPhoneNumber())) {
            errors.put("phoneNumber", "Invalid phone number format. Must be 10-15 digits, optionally starting with '+'.");
        }
        if (student.getBirthDate() != null && student.getBirthDate().isAfter(LocalDate.now())) {
            errors.put("birthDate", "Date of birth must be in the past.");
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
        if (student.getGender() == null) {
            errors.put("gender", "Gender is required to assign a default avatar.");
        }
        if (student.getEmail() != null && personsService.existsByEmailExcludingId(student.getEmail(), student.getId() != null ? student.getId() : "")) {
            errors.put("email", "The email address is already associated with another account.");
        }
        if (student.getPhoneNumber() != null && personsService.existsByPhoneNumberExcludingId(student.getPhoneNumber(), student.getId() != null ? student.getId() : "")) {
            errors.put("phoneNumber", "The phone number is already associated with another account.");
        }

        return errors;
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) return true;
        String phoneRegex = "^\\+?[1-9][0-9]{7,14}$";
        return phoneNumber.matches(phoneRegex);
    }

    private boolean isValidName(String name) {
        if (name == null) return false;
        name = name.trim();
        if (name.isEmpty()) return false;
        String nameRegex = "^(?=.{2,100}$)(\\p{L}+[\\p{L}'’\\-\\.]*)((\\s+\\p{L}+[\\p{L}'’\\-\\.]*)*)$";
        return name.matches(nameRegex);
    }

    @Override
    public Students getStudent() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) throw new IllegalStateException("No authenticated user");

        Object principal = auth.getPrincipal();
        Persons person = switch (principal) {
            case DatabaseUserPrincipal dbPrincipal -> dbPrincipal.getPerson();
            case CustomOidcUserPrincipal oidcPrincipal -> oidcPrincipal.getPerson();
            default -> throw new IllegalStateException("Unknown principal type: " + principal.getClass());
        };
        return entityManager.find(Students.class, person.getId());
    }

    @Override
    public Majors getStudentMajor() {
        return getStudent().getSpecialization().getMajor();
    }

    @Override
    public List<Students> getStudents() {
        return entityManager.createQuery("SELECT s FROM Students s JOIN FETCH s.campus JOIN FETCH s.specialization.major JOIN FETCH s.creator", Students.class)
                .getResultList();
    }

    @Override
    public Students addStudents(Students student, Curriculum curriculum, Specialization specialization, String randomPassword) {
        Staffs staff = staffsService.getStaff();
        if (staff == null) throw new IllegalStateException("No authenticated staff found");

        student.setCampus(staff.getCampus());
        student.setSpecialization(specialization);
        student.setCreator(staff);
        student.setCurriculum(curriculum);
        student.setCreatedDate(LocalDate.now());
        Students savedStudent = entityManager.merge(student);

        StudentEmailContext context = new StudentEmailContext(
                savedStudent.getId(),
                savedStudent.getFullName(),
                savedStudent.getEmail(),
                savedStudent.getPhoneNumber(),
                savedStudent.getBirthDate(),
                savedStudent.getGender() != null ? savedStudent.getGender().toString() : null,
                savedStudent.getFullAddress(),
                savedStudent.getCampus() != null ? savedStudent.getCampus().getCampusName() : null,
                savedStudent.getSpecialization().getMajor() != null ? savedStudent.getSpecialization().getMajor().getMajorName() : null,
                savedStudent.getCreator() != null ? savedStudent.getCreator().getFullName() : null,
                2025,
                savedStudent.getCreatedDate(),
                savedStudent.getCurriculum() != null ? savedStudent.getCurriculum().getName() : null
        );

        try {
            String subject = "Your Student Account Information";
            emailServiceForStudentService.sendEmailToNotifyLoginInformation(savedStudent.getEmail(), subject, context, randomPassword);
        } catch (Exception e) {
            System.err.println("Failed to schedule email to " + savedStudent.getEmail() + ": " + e.getMessage());
        }
        return savedStudent;
    }

    @Override
    public long numberOfStudentsByCampus(String campusId) {
        if (campusId == null || campusId.trim().isEmpty()) return 0L;
        return (Long) entityManager.createQuery(
                        "SELECT COUNT(s) FROM Students s WHERE s.campus.id = :campusId")
                .setParameter("campusId", campusId)
                .getSingleResult();
    }

    @Override
    @Transactional
    public void deleteStudent(String id) {
        Students student = entityManager.find(Students.class, id);
        if (student == null) {
            throw new IllegalArgumentException("Student with ID " + id + " not found");
        }

        entityManager.createQuery(
                        "DELETE FROM StudentRequiredMajorSubjects srms WHERE srms.id.studentId = :studentId")
                .setParameter("studentId", id)
                .executeUpdate();

        entityManager.createQuery(
                        "DELETE FROM StudentRequiredMinorSubjects srms WHERE srms.id.studentId = :studentId")
                .setParameter("studentId", id)
                .executeUpdate();

        entityManager.createQuery(
                        "DELETE FROM Students_MajorClasses smc WHERE smc.id.studentId = :studentId")
                .setParameter("studentId", id)
                .executeUpdate();

        entityManager.createQuery(
                        "DELETE FROM Students_SpecializedClasses smc WHERE smc.id.studentId = :studentId")
                .setParameter("studentId", id)
                .executeUpdate();

        entityManager.remove(student);
    }

    @Override
    public void editStudent(String id, Curriculum curriculum, Specialization specialization, Students student) throws MessagingException {
        if (student == null || id == null) {
            throw new IllegalArgumentException("Student object or ID cannot be null");
        }
        Students existingStudent = entityManager.createQuery(
                        "SELECT s FROM Students s WHERE s.id = :id", Students.class)
                .setParameter("id", id)
                .getSingleResult();
        if (existingStudent == null) {
            throw new IllegalArgumentException("Student with ID " + id + " not found");
        }

        editStudentFields(existingStudent, student);
        if (curriculum != null) existingStudent.setCurriculum(curriculum);
        if (specialization != null) existingStudent.setSpecialization(specialization);

        entityManager.merge(existingStudent);

        StudentEmailContext context = new StudentEmailContext(
                existingStudent.getId(),
                existingStudent.getFullName(),
                existingStudent.getEmail(),
                existingStudent.getPhoneNumber(),
                existingStudent.getBirthDate(),
                existingStudent.getGender() != null ? existingStudent.getGender().toString() : null,
                existingStudent.getFullAddress(),
                existingStudent.getCampus() != null ? existingStudent.getCampus().getCampusName() : null,
                existingStudent.getSpecialization().getMajor() != null ? existingStudent.getSpecialization().getMajor().getMajorName() : null,
                existingStudent.getCreator() != null ? existingStudent.getCreator().getFullName() : null,
                2025,
                existingStudent.getCreatedDate(),
                existingStudent.getCurriculum() != null ? existingStudent.getCurriculum().getName() : null
        );

        String subject = "Your student account information after being edited";
        emailServiceForStudentService.sendEmailToNotifyInformationAfterEditing(existingStudent.getEmail(), subject, context);
    }

    @Override
    public Students getStudentById(String id) {
        return entityManager.find(Students.class, id);
    }

    @Override
    public List<Students> getPaginatedStudentsByCampus(String campusId, int firstResult, int pageSize) {
        if (campusId == null || campusId.trim().isEmpty()) return List.of();
        return entityManager.createQuery(
                        "SELECT s FROM Students s WHERE s.campus.id = :campusId", Students.class)
                .setParameter("campusId", campusId)
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public List<Students> searchStudentsByCampus(String campusId, String searchType, String keyword, int firstResult, int pageSize) {
        if (campusId == null || campusId.trim().isEmpty() || keyword == null || keyword.trim().isEmpty() || pageSize <= 0) {
            return List.of();
        }

        String queryString = "SELECT s FROM Students s JOIN FETCH s.campus JOIN FETCH s.specialization.major JOIN FETCH s.creator " +
                "WHERE s.campus.id = :campusId";

        if ("name".equals(searchType)) {
            keyword = keyword.toLowerCase().trim();
            String[] words = keyword.split("\\s+");
            StringBuilder nameCondition = new StringBuilder();
            for (int i = 0; i < words.length; i++) {
                if (i > 0) nameCondition.append(" AND ");
                nameCondition.append("(LOWER(s.firstName) LIKE :word").append(i).append(" OR LOWER(s.lastName) LIKE :word").append(i).append(")");
            }
            queryString += " AND (" + nameCondition.toString() + ")";
        } else if ("id".equals(searchType)) {
            queryString += " AND LOWER(s.id) LIKE LOWER(:keyword)";
        } else {
            return List.of();
        }

        TypedQuery<Students> query = entityManager.createQuery(queryString, Students.class)
                .setParameter("campusId", campusId)
                .setFirstResult(firstResult)
                .setMaxResults(pageSize);

        if ("name".equals(searchType)) {
            String[] words = keyword.split("\\s+");
            for (int i = 0; i < words.length; i++) {
                query.setParameter("word" + i, "%" + words[i] + "%");
            }
        } else if ("id".equals(searchType)) {
            query.setParameter("keyword", "%" + keyword.trim() + "%");
        }

        return query.getResultList();
    }

    @Override
    public long countSearchResultsByCampus(String campusId, String searchType, String keyword) {
        if (campusId == null || campusId.trim().isEmpty() || keyword == null || keyword.trim().isEmpty()) {
            return 0L;
        }

        String queryString = "SELECT COUNT(s) FROM Students s WHERE s.campus.id = :campusId";
        if ("name".equals(searchType)) {
            keyword = keyword.toLowerCase().trim();
            String[] words = keyword.split("\\s+");
            StringBuilder nameCondition = new StringBuilder();
            for (int i = 0; i < words.length; i++) {
                if (i > 0) nameCondition.append(" AND ");
                nameCondition.append("(LOWER(s.firstName) LIKE :word").append(i).append(" OR LOWER(s.lastName) LIKE :word").append(i).append(")");
            }
            queryString += " AND (" + nameCondition.toString() + ")";
        } else if ("id".equals(searchType)) {
            queryString += " AND LOWER(s.id) LIKE LOWER(:keyword)";
        } else {
            return 0L;
        }

        TypedQuery<Long> query = entityManager.createQuery(queryString, Long.class)
                .setParameter("campusId", campusId);

        if ("name".equals(searchType)) {
            String[] words = keyword.split("\\s+");
            for (int i = 0; i < words.length; i++) {
                query.setParameter("word" + i, "%" + words[i] + "%");
            }
        } else if ("id".equals(searchType)) {
            query.setParameter("keyword", "%" + keyword.trim() + "%");
        }

        return query.getSingleResult();
    }

    @Override
    public List<Integer> getUniqueAdmissionYearsByCampus(String campusId) {
        if (campusId == null || campusId.trim().isEmpty()) return List.of();
        String jpql = "SELECT DISTINCT YEAR(s.admissionYear) FROM Students s " +
                "WHERE s.campus.id = :campusId " +
                "ORDER BY YEAR(s.admissionYear) ASC";
        return entityManager.createQuery(jpql, Integer.class)
                .setParameter("campusId", campusId)
                .getResultList().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

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

    private void editStudentFields(Students existing, Students edited) {
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
        if (edited.getCreator() != null) existing.setCreator(edited.getCreator());
        if (edited.getCurriculum() != null) existing.setCurriculum(edited.getCurriculum());
        if (edited.getSpecialization() != null) existing.setSpecialization(edited.getSpecialization());
    }
}