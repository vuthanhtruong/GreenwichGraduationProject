package com.example.demo.api.Add;

import com.example.demo.entity.Students;
import com.example.demo.service.LecturesService;
import com.example.demo.service.PersonsService;
import com.example.demo.service.StaffsService;
import com.example.demo.service.StudentsService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api//staff-home/students-list/")
public class AddStudentRestController {
    private final StaffsService staffsService;
    private final StudentsService studentsService;
    private final LecturesService lecturesService;
    private final ResourceLoader resourceLoader;
    private final PersonsService personsService;

    public AddStudentRestController(StaffsService staffsService, LecturesService lecturesService,
                                StudentsService studentsService, ResourceLoader resourceLoader,
                                PersonsService personsService) {
        this.staffsService = staffsService;
        this.studentsService = studentsService;
        this.lecturesService = lecturesService;
        this.resourceLoader = resourceLoader;
        this.personsService = personsService;
    }

    @GetMapping("/add-student")
    public ResponseEntity<Students> getAddStudentForm() {
        return ResponseEntity.ok(new Students());
    }

    @PostMapping("/add-student")
    public ResponseEntity<?> addStudent(
            @Valid @ModelAttribute("student") Students student,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            HttpSession session) {

        List<String> errors = new ArrayList<>();

        // Perform all validations
        validateStudent(student, errors, avatarFile);

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            String randomPassword = generateRandomPassword(12);
            student.setPassword(randomPassword);
            String studentId = generateUniqueStudentId(staffsService.getStaffMajor().getMajorId(),
                    student.getCreatedDate() != null ? student.getCreatedDate() : LocalDate.now());
            student.setId(studentId);

            // Handle avatar upload
            if (avatarFile != null && !avatarFile.isEmpty()) {
                byte[] avatarBytes = avatarFile.getBytes();
                student.setAvatar(avatarBytes);
            } else if (session.getAttribute("tempAvatar") != null) {
                // Use avatar from session if no new file
                student.setAvatar((byte[]) session.getAttribute("tempAvatar"));
            }

            studentsService.addStudents(student, randomPassword);
            // Clear temporary session data
            session.removeAttribute("tempAvatar");
            session.removeAttribute("tempAvatarName");

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Student added successfully with ID: " + studentId);
        } catch (IOException e) {
            errors.add("Failed to process avatar: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errors);
        } catch (Exception e) {
            errors.add("An error occurred while adding the student: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errors);
        }
    }

    private void validateStudent(Students student, List<String> errors, MultipartFile avatarFile) {
        // Custom validations
        if (!isValidName(student.getFirstName())) {
            errors.add("First name is not valid. Only letters, spaces, and standard punctuation are allowed.");
        }

        if (!isValidName(student.getLastName())) {
            errors.add("Last name is not valid. Only letters, spaces, and standard punctuation are allowed.");
        }

        if (student.getEmail() != null && personsService.existsByEmail(student.getEmail())) {
            errors.add("The email address is already associated with another account.");
        }

        if (student.getPhoneNumber() != null && personsService.existsByPhoneNumber(student.getPhoneNumber())) {
            errors.add("The phone number is already associated with another account.");
        }

        if (student.getEmail() != null && !isValidEmail(student.getEmail())) {
            errors.add("Invalid email format.");
        }

        if (student.getPhoneNumber() != null && !isValidPhoneNumber(student.getPhoneNumber())) {
            errors.add("Invalid phone number format.");
        }

        if (student.getBirthDate() != null && student.getBirthDate().isAfter(LocalDate.now())) {
            errors.add("Date of birth must be in the past.");
        }

        // Validate avatar file
        if (avatarFile != null && !avatarFile.isEmpty()) {
            String contentType = avatarFile.getContentType();
            if (!contentType.startsWith("image/")) {
                errors.add("Avatar must be an image file.");
            }
            if (avatarFile.getSize() > 5 * 1024 * 1024) { // 5MB limit
                errors.add("Avatar file size must not exceed 5MB.");
            }
        }

        // Ensure gender is provided for default avatar
        if (student.getGender() == null) {
            errors.add("Gender is required to assign a default avatar.");
        }
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

    private String generateRandomPassword(int length) {
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

    private String generateUniqueStudentId(String majorId, LocalDate createdDate) {
        String prefix;
        switch (majorId) {
            case "major001":
                prefix = "GBH";
                break;
            case "major002":
                prefix = "GCH";
                break;
            case "major003":
                prefix = "GDH";
                break;
            case "major004":
                prefix = "GKH";
                break;
            default:
                prefix = "GEN";
                break;
        }

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
}