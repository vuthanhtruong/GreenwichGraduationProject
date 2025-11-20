package com.example.demo.user.student.dao;

import com.example.demo.curriculum.model.Curriculum;
import com.example.demo.entity.Enums.Gender;
import com.example.demo.specialization.model.Specialization;
import com.example.demo.email_service.dto.StudentEmailContext;
import com.example.demo.email_service.service.EmailServiceForLecturerService;
import com.example.demo.email_service.service.EmailServiceForStudentService;
import com.example.demo.major.model.Majors;
import com.example.demo.user.person.model.Persons;
import com.example.demo.specialization.security.model.CustomOidcUserPrincipal;
import com.example.demo.specialization.security.model.DatabaseUserPrincipal;
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
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Transactional
public class StudentDAOImpl implements StudentsDAO {

    @Override
    public long totalStudentsAllCampus() {
        return entityManager.createQuery("SELECT COUNT(s) FROM Students s", Long.class)
                .getSingleResult();
    }

    @Override
    public long newStudentsThisYearAllCampus() {
        int currentYear = LocalDate.now().getYear();
        return entityManager.createQuery(
                        "SELECT COUNT(s) FROM Students s WHERE s.admissionYear = :year", Long.class)
                .setParameter("year", currentYear)
                .getSingleResult();
    }

    @Override
    public Map<String, Long> studentsByCampus() {
        List<Object[]> rows = entityManager.createQuery(
                        "SELECT c.campusName, COUNT(s) " +
                                "FROM Students s JOIN s.campus c " +
                                "GROUP BY c.campusId, c.campusName " +
                                "ORDER BY COUNT(s) DESC", Object[].class)
                .getResultList();

        return rows.stream()
                .collect(Collectors.toMap(
                        arr -> (String) arr[0],
                        arr -> (Long) arr[1],
                        (v1, v2) -> v1,
                        LinkedHashMap::new
                ));
    }

    @Override
    public Map<String, Long> studentsByMajor() {
        List<Object[]> rows = entityManager.createQuery(
                        "SELECT m.majorName, COUNT(s) " +
                                "FROM Students s JOIN s.specialization spec JOIN spec.major m " +
                                "GROUP BY m.majorId, m.majorName " +
                                "ORDER BY COUNT(s) DESC", Object[].class)
                .getResultList();

        return rows.stream()
                .collect(Collectors.toMap(
                        arr -> (String) arr[0],
                        arr -> (Long) arr[1],
                        (v1, v2) -> v1,
                        LinkedHashMap::new
                ));
    }

    @Override
    public Map<String, Long> studentsBySpecialization() {
        List<Object[]> rows = entityManager.createQuery(
                        "SELECT COALESCE(s.specialization.specializationName, 'Not Assigned'), COUNT(s) " +
                                "FROM Students s " +
                                "GROUP BY s.specialization.specializationId, s.specialization.specializationName " +
                                "ORDER BY COUNT(s) DESC", Object[].class)
                .getResultList();

        return rows.stream()
                .collect(Collectors.toMap(
                        arr -> (String) arr[0],
                        arr -> (Long) arr[1],
                        (v1, v2) -> v1,
                        LinkedHashMap::new
                ));
    }

    @Override
    public Map<String, Long> studentsByGender() {
        List<Object[]> rows = entityManager.createQuery(
                        "SELECT COALESCE(s.gender, 'OTHER'), COUNT(s) FROM Students s GROUP BY s.gender",
                        Object[].class)
                .getResultList();

        Map<String, Long> result = new LinkedHashMap<>();
        for (Object[] row : rows) {
            Gender g = (Gender) row[0];
            String label = switch (g) {
                case MALE -> "Male";
                case FEMALE -> "Female";
                case OTHER -> "Other";
            };
            result.put(label, (Long) row[1]);
        }
        return result;
    }

    @Override
    public Map<String, Long> studentsByAdmissionYear() {
        List<Object[]> rows = entityManager.createQuery(
                        "SELECT s.admissionYear, COUNT(s) " +
                                "FROM Students s WHERE s.admissionYear IS NOT NULL " +
                                "GROUP BY s.admissionYear " +
                                "ORDER BY s.admissionYear DESC", Object[].class)
                .getResultList();

        return rows.stream()
                .collect(Collectors.toMap(
                        arr -> String.valueOf(arr[0]),
                        arr -> (Long) arr[1],
                        (v1, v2) -> v1,
                        LinkedHashMap::new
                ));
    }

    @Override
    public Map<String, Long> studentsByAgeGroup() {
        int currentYear = LocalDate.now().getYear();

        String jpql = """
        SELECT 
            CASE 
                WHEN s.birthDate IS NULL THEN 'Unknown'
                WHEN YEAR(s.birthDate) >= :currentYear - 19 THEN 'Under 20'
                WHEN YEAR(s.birthDate) >= :currentYear - 24 THEN '20-24'
                WHEN YEAR(s.birthDate) >= :currentYear - 29 THEN '25-29'
                ELSE '30+'
            END,
            COUNT(s)
        FROM Students s
        GROUP BY 
            CASE 
                WHEN s.birthDate IS NULL THEN 'Unknown'
                WHEN YEAR(s.birthDate) >= :currentYear - 19 THEN 'Under 20'
                WHEN YEAR(s.birthDate) >= :currentYear - 24 THEN '20-24'
                WHEN YEAR(s.birthDate) >= :currentYear - 29 THEN '25-29'
                ELSE '30+'
            END
        ORDER BY 
            MIN(CASE WHEN s.birthDate IS NULL THEN 9999 ELSE YEAR(s.birthDate) END) DESC
        """;

        List<Object[]> rows = entityManager.createQuery(jpql, Object[].class)
                .setParameter("currentYear", currentYear)
                .getResultList();

        return rows.stream()
                .collect(Collectors.toMap(
                        arr -> (String) arr[0],
                        arr -> (Long) arr[1],
                        (v1, v2) -> v1,
                        LinkedHashMap::new
                ));
    }

    @Override
    public List<Students> top10NewestStudents() {
        return entityManager.createQuery(
                        "SELECT s FROM Students s ORDER BY s.createdDate DESC", Students.class)
                .setMaxResults(10)
                .getResultList();
    }

    @Override
    public long countCampusesWithoutStudents() {
        return entityManager.createQuery(
                        "SELECT COUNT(c) FROM Campuses c " +
                                "WHERE NOT EXISTS (SELECT 1 FROM Students s WHERE s.campus = c)", Long.class)
                .getSingleResult();
    }

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
        if (student.getAdmissionYear() == null || student.getAdmissionYear() < 1900 || student.getAdmissionYear() > LocalDate.now().getYear() + 1) {
            errors.put("admissionYear", "Admission year must be between 1900 and next year.");
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
        return entityManager.createQuery("SELECT s FROM Students s", Students.class)
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
                savedStudent.getAdmissionYear(),
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
                        "SELECT COUNT(s) FROM Students s WHERE s.campus.campusId = :campusId")
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
                existingStudent.getAdmissionYear(),
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
                        "SELECT s FROM Students s WHERE s.campus.campusId = :campusId", Students.class)
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

        String queryString = "SELECT s FROM Students s " +
                "WHERE s.campus.campusId = :campusId";

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

        String queryString = "SELECT COUNT(s) FROM Students s WHERE s.campus.campusId = :campusId";
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
                "WHERE s.campus.campusId = :campusId " +
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
        String jpql = "SELECT COUNT(s) FROM Students s WHERE s.campus.campusId = :campusId";
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
    // src/main/java/com/example/demo/user/student/dao/StudentDAOImpl.java

    @Override
    public List<Students> getStudentsByCampusAndMajor(String campusId, String majorId) {
        if (campusId == null || campusId.isBlank() || majorId == null || majorId.isBlank()) {
            return List.of();
        }
        return entityManager.createQuery(
                        "SELECT s FROM Students s " +
                                "WHERE s.campus.campusId = :campusId AND s.specialization.major.majorId = :majorId ", Students.class)
                .setParameter("campusId", campusId)
                .setParameter("majorId", majorId)
                .getResultList();
    }

    @Override
    public List<Students> getPaginatedStudentsByCampusAndMajor(String campusId, String majorId, int firstResult, int pageSize) {
        return entityManager.createQuery(
                        "SELECT s FROM Students s " +
                                "WHERE s.campus.campusId = :campusId AND s.specialization.major.majorId = :majorId", Students.class)
                .setParameter("campusId", campusId)
                .setParameter("majorId", majorId)
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public List<Students> searchStudentsByCampusAndMajor(String campusId, String majorId, String searchType, String keyword, int firstResult, int pageSize) {
        if (campusId == null || campusId.isBlank() || majorId == null || majorId.isBlank() ||
                keyword == null || keyword.trim().isEmpty() || pageSize <= 0) {
            return List.of();
        }

        String baseQuery = "SELECT s FROM Students s " +
                "WHERE s.campus.campusId = :campusId AND s.specialization.major.majorId = :majorId";

        if ("name".equals(searchType)) {
            keyword = keyword.toLowerCase().trim();
            String[] words = keyword.split("\\s+");
            StringBuilder condition = new StringBuilder();
            for (int i = 0; i < words.length; i++) {
                if (i > 0) condition.append(" AND ");
                condition.append("(LOWER(s.firstName) LIKE :word").append(i)
                        .append(" OR LOWER(s.lastName) LIKE :word").append(i).append(")");
            }
            baseQuery += " AND (" + condition + ")";
        } else if ("id".equals(searchType)) {
            baseQuery += " AND LOWER(s.id) LIKE LOWER(:keyword)";
        } else {
            return List.of();
        }

        TypedQuery<Students> query = entityManager.createQuery(baseQuery, Students.class)
                .setParameter("campusId", campusId)
                .setParameter("majorId", majorId)
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
    public long countSearchResultsByCampusAndMajor(String campusId, String majorId, String searchType, String keyword) {
        if (campusId == null || campusId.isBlank() || majorId == null || majorId.isBlank() ||
                keyword == null || keyword.trim().isEmpty()) {
            return 0L;
        }

        String baseQuery = "SELECT COUNT(s) FROM Students s " +
                "WHERE s.campus.campusId = :campusId AND specialization.major.majorId = :majorId";

        if ("name".equals(searchType)) {
            keyword = keyword.toLowerCase().trim();
            String[] words = keyword.split("\\s+");
            StringBuilder condition = new StringBuilder();
            for (int i = 0; i < words.length; i++) {
                if (i > 0) condition.append(" AND ");
                condition.append("(LOWER(s.firstName) LIKE :word").append(i)
                        .append(" OR LOWER(s.lastName) LIKE :word").append(i).append(")");
            }
            baseQuery += " AND (" + condition + ")";
        } else if ("id".equals(searchType)) {
            baseQuery += " AND LOWER(s.id) LIKE LOWER(:keyword)";
        } else {
            return 0L;
        }

        TypedQuery<Long> query = entityManager.createQuery(baseQuery, Long.class)
                .setParameter("campusId", campusId)
                .setParameter("majorId", majorId);

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
    public long totalStudentsByCampusAndMajor(String campusId, String majorId) {
        if (campusId == null || campusId.isBlank() || majorId == null || majorId.isBlank()) {
            return 0L;
        }
        return entityManager.createQuery(
                        "SELECT COUNT(s) FROM Students s " +
                                "WHERE s.campus.campusId = :campusId AND s.specialization.major.majorId = :majorId", Long.class)
                .setParameter("campusId", campusId)
                .setParameter("majorId", majorId)
                .getSingleResult();
    }

    // ==================== DASHBOARD METHODS CHO STAFF HIỆN TẠI ====================

    @Override
    public long totalStudentsForCurrentStaff() {
        Staffs staff = staffsService.getStaff();
        if (staff == null || staff.getCampus() == null || staff.getMajorManagement() == null) return 0L;

        String campusId = staff.getCampus().getCampusId();
        String majorId = staff.getMajorManagement().getMajorId();

        return entityManager.createQuery(
                        "SELECT COUNT(s) FROM Students s " +
                                "WHERE s.campus.campusId = :campusId " +
                                "AND s.specialization.major.majorId = :majorId", Long.class)
                .setParameter("campusId", campusId)
                .setParameter("majorId", majorId)
                .getSingleResult();
    }

    @Override
    public long countNewStudentsLast30DaysForCurrentStaff() {
        Staffs staff = staffsService.getStaff();
        if (staff == null || staff.getCampus() == null || staff.getMajorManagement() == null) return 0L;

        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        String campusId = staff.getCampus().getCampusId();
        String majorId = staff.getMajorManagement().getMajorId();

        return entityManager.createQuery(
                        "SELECT COUNT(s) FROM Students s " +
                                "WHERE s.campus.campusId = :campusId " +
                                "AND s.specialization.major.majorId = :majorId " +
                                "AND s.createdDate >= :date", Long.class)
                .setParameter("campusId", campusId)
                .setParameter("majorId", majorId)
                .setParameter("date", thirtyDaysAgo)
                .getSingleResult();
    }

    @Override
    public List<Object[]> countStudentsBySpecializationForCurrentStaff() {
        Staffs staff = staffsService.getStaff();
        if (staff == null || staff.getCampus() == null || staff.getMajorManagement() == null) return List.of();

        String campusId = staff.getCampus().getCampusId();
        String majorId = staff.getMajorManagement().getMajorId();

        return entityManager.createQuery(
                        "SELECT s.specialization.specializationName, COUNT(s) " +
                                "FROM Students s " +
                                "WHERE s.campus.campusId = :campusId " +
                                "AND s.specialization.major.majorId = :majorId " +
                                "GROUP BY s.specialization.specializationId, s.specialization.specializationName " +
                                "ORDER BY COUNT(s) DESC", Object[].class)
                .setParameter("campusId", campusId)
                .setParameter("majorId", majorId)
                .getResultList();
    }

    @Override
    public List<Object[]> countStudentsByAdmissionYearForCurrentStaff() {
        Staffs staff = staffsService.getStaff();
        if (staff == null || staff.getCampus() == null || staff.getMajorManagement() == null) return List.of();

        String campusId = staff.getCampus().getCampusId();
        String majorId = staff.getMajorManagement().getMajorId();

        return entityManager.createQuery(
                        "SELECT s.admissionYear, COUNT(s) " +
                                "FROM Students s " +
                                "WHERE s.campus.campusId = :campusId " +
                                "AND s.specialization.major.majorId = :majorId " +
                                "AND s.admissionYear IS NOT NULL " +
                                "GROUP BY s.admissionYear " +
                                "ORDER BY s.admissionYear DESC", Object[].class)
                .setParameter("campusId", campusId)
                .setParameter("majorId", majorId)
                .getResultList();
    }

    @Override
    public List<Object[]> countStudentsByGenderForCurrentStaff() {
        Staffs staff = staffsService.getStaff();
        if (staff == null || staff.getCampus() == null || staff.getMajorManagement() == null) return List.of();

        String campusId = staff.getCampus().getCampusId();
        String majorId = staff.getMajorManagement().getMajorId();

        return entityManager.createQuery(
                        "SELECT COALESCE(s.gender, 'KHÁC'), COUNT(s) " +
                                "FROM Students s " +
                                "WHERE s.campus.campusId = :campusId " +
                                "AND s.specialization.major.majorId = :majorId " +
                                "GROUP BY s.gender", Object[].class)
                .setParameter("campusId", campusId)
                .setParameter("majorId", majorId)
                .getResultList();
    }

    @Override
    public List<Object[]> top5SpecializationsForCurrentStaff() {
        Staffs staff = staffsService.getStaff();
        if (staff == null || staff.getCampus() == null || staff.getMajorManagement() == null) return List.of();

        String campusId = staff.getCampus().getCampusId();
        String majorId = staff.getMajorManagement().getMajorId();

        return entityManager.createQuery(
                        "SELECT s.specialization.specializationName, COUNT(s) " +
                                "FROM Students s " +
                                "WHERE s.campus.campusId = :campusId " +
                                "AND s.specialization.major.majorId = :majorId " +
                                "GROUP BY s.specialization.specializationId, s.specialization.specializationName " +
                                "ORDER BY COUNT(s) DESC", Object[].class)
                .setMaxResults(5)
                .setParameter("campusId", campusId)
                .setParameter("majorId", majorId)
                .getResultList();
    }

    @Override
    public List<Object[]> monthlyStudentIntakeThisYearForCurrentStaff() {
        Staffs staff = staffsService.getStaff();
        if (staff == null || staff.getCampus() == null || staff.getMajorManagement() == null) return List.of();

        int currentYear = LocalDate.now().getYear();
        String campusId = staff.getCampus().getCampusId();
        String majorId = staff.getMajorManagement().getMajorId();

        return entityManager.createQuery(
                        "SELECT MONTH(s.createdDate), COUNT(s) " +
                                "FROM Students s " +
                                "WHERE YEAR(s.createdDate) = :year " +
                                "AND s.campus.campusId = :campusId " +
                                "AND s.specialization.major.majorId = :majorId " +
                                "GROUP BY MONTH(s.createdDate) " +
                                "ORDER BY MONTH(s.createdDate)", Object[].class)
                .setParameter("year", currentYear)
                .setParameter("campusId", campusId)
                .setParameter("majorId", majorId)
                .getResultList();
    }
}