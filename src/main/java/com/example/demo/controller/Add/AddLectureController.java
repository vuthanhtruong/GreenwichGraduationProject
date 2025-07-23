package com.example.demo.controller.Add;

import com.example.demo.entity.Lecturers;
import com.example.demo.entity.Staffs;
import com.example.demo.service.LecturesService;
import com.example.demo.service.StaffsService;
import com.example.demo.service.StudentsService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/staff-home/lectures-list/")
public class AddLectureController {
    private final StaffsService staffsService;
    private final StudentsService studentsService;
    private final LecturesService lecturesService;

    public AddLectureController(StaffsService staffsService, LecturesService lecturesService, StudentsService studentsService) {
        this.staffsService = staffsService;
        this.studentsService=studentsService;
        this.lecturesService = lecturesService;
    }

    @GetMapping("/add-lecture")
    public String showAddlecturePage(Model model) {
        model.addAttribute("lecture", new Staffs());
        model.addAttribute("majors", staffsService.getMajors());
        return "AddLecture";
    }

    @PostMapping("/add-lecture")
    public String addlecture(
            @Valid @ModelAttribute("lecture") Lecturers lecture,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        List<String> errors = new ArrayList<>();

        // Perform all validations
        validatelecture(lecture, bindingResult, errors);

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("majors", staffsService.getMajors());
            return "Addlecture";
        }

        try {
            String randomPassword = generateRandomPassword(12);
            lecture.setPassword(randomPassword);
            String lectureId = generateUniquelectureId(staffsService.getMajors().getMajorId(), lecture.getCreatedDate());
            lecture.setId(lectureId);
            lecturesService.addLecturers(lecture, randomPassword);
            redirectAttributes.addFlashAttribute("successMessage", "lecture added successfully!");
            return "redirect:/staff-home/lectures-list";
        } catch (Exception e) {
            errors.add("An error occurred while adding the lecture: " + e.getMessage());
            model.addAttribute("errors", errors);
            model.addAttribute("majors", staffsService.getMajors());
            return "Addlecture";
        }
    }

    private void validatelecture(Lecturers lecture, BindingResult bindingResult, List<String> errors) {
        // Annotation-based validation errors
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));
        }

        // Custom validations
        if (!isValidName(lecture.getFirstName())) {
            errors.add("First name is not valid. Only letters, spaces, and standard punctuation are allowed.");
        }

        if (!isValidName(lecture.getLastName())) {
            errors.add("Last name is not valid. Only letters, spaces, and standard punctuation are allowed.");
        }

        if (lecture.getEmail() != null && staffsService.existsByEmail(lecture.getEmail())) {
            errors.add("The email address is already associated with another account.");
        }

        if (lecture.getPhoneNumber() != null && staffsService.existsByPhoneNumber(lecture.getPhoneNumber())) {
            errors.add("The phone number is already associated with another account.");
        }

        if (lecture.getEmail() != null && !isValidEmail(lecture.getEmail())) {
            errors.add("Invalid email format.");
        }

        if (lecture.getPhoneNumber() != null && !isValidPhoneNumber(lecture.getPhoneNumber())) {
            errors.add("Invalid phone number format.");
        }

        if (lecture.getBirthDate() != null && lecture.getBirthDate().isAfter(LocalDate.now())) {
            errors.add("Date of birth must be in the past.");
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

    private String generateUniquelectureId(String majorId, LocalDate createdDate) {
        String prefix;
        switch (majorId) {
            case "major001":
                prefix = "TBH";
                break;
            case "major002":
                prefix = "TCH";
                break;
            case "major003":
                prefix = "TDT";
                break;
            case "major004":
                prefix = "TKT";
                break;
            default:
                prefix = "TGN";
                break;
        }
        // Extract year (last two digits) and date (MMdd) from createdDate
        String year = String.format("%02d", createdDate.getYear() % 100); // e.g., 2025 -> 25
        String date = String.format("%02d%02d", createdDate.getMonthValue(), createdDate.getDayOfMonth()); // e.g., July 23 -> 0723

        String lectureId;
        SecureRandom random = new SecureRandom();
        do {
            // Generate 1 random digit to make total length 10 (3 prefix + 2 year + 4 date + 1 random)
            String randomDigit = String.valueOf(random.nextInt(10));
            lectureId = prefix + year + date + randomDigit;
        } while (staffsService.existsPersonById(lectureId));
        return lectureId;
    }
}
