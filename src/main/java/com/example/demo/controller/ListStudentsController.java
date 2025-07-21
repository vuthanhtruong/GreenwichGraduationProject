package com.example.demo.controller;
import com.example.demo.entity.Gender;
import com.example.demo.entity.Majors;
import com.example.demo.entity.Staffs;
import com.example.demo.entity.Students;
import com.example.demo.service.StaffsService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/staff-home")
public class ListStudentsController {
    private final StaffsService staffsService;

    public ListStudentsController(StaffsService staffsService) {
        this.staffsService = staffsService;
    }

    @GetMapping("/students-list")
    public String listStudents(
            Model model,
            HttpSession session,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize) {

        Staffs staffs=staffsService.getStaffs();

        if (pageSize == null) {
            pageSize = (Integer) session.getAttribute("pageSize");
            if (pageSize == null) {
                pageSize = 5; // Mặc định 5 nếu chưa có
            }
        }
        session.setAttribute("pageSize", pageSize);

        Long totalStudents = staffsService.numberOfStudents();

        if (totalStudents == 0) {
            model.addAttribute("students", new ArrayList<>());
            model.addAttribute("currentPage", 1);
            model.addAttribute("totalPages", 1);
            model.addAttribute("pageSize", pageSize);
            return "StudentsList";
        }

        int totalPages = (int) Math.ceil((double) totalStudents / pageSize);
        if (page < 1) page = 1;
        if (page > totalPages) page = totalPages;

        int firstResult = (page - 1) * pageSize;
        List<Students> students = staffsService.getAll();

        // Đưa dữ liệu lên giao diện
        model.addAttribute("students", students);
        model.addAttribute("currentPage", page); // Trang hiện tại
        model.addAttribute("totalPages", totalPages); // Tổng số trang
        model.addAttribute("pageSize", pageSize); // Kích thước trang
        return "StudentsList";
    }

    @GetMapping("/add-student")
    public String showAddStudentPage(Model model) {
        model.addAttribute("student", new Students()); // Add a new Students object to the model
        return "AddStudent";
    }

    @PostMapping("/add-student")
    public String addStudent(
            @Valid @ModelAttribute("student") Students student,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        List<String> errors = new ArrayList<>();

        // Perform all validations
        validateStudent(student, bindingResult, errors);

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            return "AddStudent";
        }

        try {
            student.setPassword(generateRandomPassword(9));
            String studentId = generateUniqueStudentId(student.getMajor().getMajorId());
            student.setId(studentId);
            staffsService.addStudents(student);
            redirectAttributes.addFlashAttribute("successMessage", "Student added successfully!");
            return "redirect:/staff-home/students-list";
        } catch (Exception e) {
            errors.add("An error occurred while adding the student: " + e.getMessage());
            model.addAttribute("errors", errors);
            return "AddStudent";
        }
    }

    private void validateStudent(Students student, BindingResult bindingResult, List<String> errors) {
        // Annotation-based validation errors
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));
        }

        // Custom validations
        if (!isValidName(student.getFirstName())) {
            errors.add("First name is not valid. Only letters, spaces, and standard punctuation are allowed.");
        }

        if (!isValidName(student.getLastName())) {
            errors.add("Last name is not valid. Only letters, spaces, and standard punctuation are allowed.");
        }

        if (student.getEmail() != null && staffsService.existsByEmail(student.getEmail())) {
            errors.add("The email address is already associated with another account.");
        }

        if (student.getPhoneNumber() != null && staffsService.existsByPhoneNumber(student.getPhoneNumber())) {
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

    private String generateUniqueStudentId(String majorId) {
        String prefix;
        switch (majorId) {
            case "major001":
                prefix = "GCH";
                break;
            case "major002":
                prefix = "GBH";
                break;
            case "major003":
                prefix = "GDT";
                break;
            default:
                prefix = "GEN";
                break;
        }

        String studentId;
        do {
            studentId = prefix + generateRandomDigits(7);
        } while (staffsService.existsPersonById(studentId));
        return studentId;
    }

    private String generateRandomDigits(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder digits = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            digits.append(random.nextInt(10));
        }
        return digits.toString();
    }

}
