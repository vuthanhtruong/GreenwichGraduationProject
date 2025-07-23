package com.example.demo.controller;
import com.example.demo.entity.Lecturers;
import com.example.demo.entity.Students;
import com.example.demo.service.LecturesService;
import com.example.demo.service.StaffsService;
import com.example.demo.service.StudentsService;
import jakarta.validation.Valid;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/staff-home/students-list")
public class UpdateStudentController {
    private final StaffsService staffsService;
    private final StudentsService studentsService;
    private final LecturesService lecturesService;

    public UpdateStudentController(StaffsService staffsService, LecturesService lecturesService, StudentsService studentsService) {
        this.staffsService = staffsService;
        this.studentsService=studentsService;
        this.lecturesService = lecturesService;
    }
    @PostMapping("/edit-student-form")
    public String handleEditStudentPost(@RequestParam String id, Model model) {
        Students student = studentsService.getStudentById(id);
        model.addAttribute("student", student);
        model.addAttribute("majors", staffsService.getMajors());
        return "EditStudentForm";
    }

    @PutMapping("/edit-student-form")
    public String updateStudent(
            @Valid @ModelAttribute("student") Students student,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes, ModelMap modelMap) {

        // Check if user is authenticated
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("error", "Unauthorized access.");
            return "redirect:/staff-home/students-list";
        }

        // Validate student data
        List<String> errors = new ArrayList<>();
        validateStudent(student, bindingResult, errors);

        if (!errors.isEmpty()) {
            modelMap.addAttribute("errors", errors);
            return "EditLectureForm";
        }

        try {
            // Check if student exists
            if (!staffsService.existsPersonById(student.getId())) {
                redirectAttributes.addFlashAttribute("error", "Student with ID " + student.getId() + " not found.");
                return "redirect:/staff-home/students-list";
            }
            // Update student
            studentsService.updateStudent(student.getId(), student);
            redirectAttributes.addFlashAttribute("successMessage", "Student updated successfully!");
        } catch (DataAccessException e) {
            redirectAttributes.addFlashAttribute("error", "Database error while updating student: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Unexpected error while updating student: " + e.getMessage());
        }

        return "redirect:/staff-home/students-list";
    }

    private void validateStudent(Students student, BindingResult bindingResult, List<String> errors) {
        // Annotation-based validation
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

        if (student.getEmail() != null && !isValidEmail(student.getEmail())) {
            errors.add("Invalid email format.");
        }

        if (student.getPhoneNumber() != null && !isValidPhoneNumber(student.getPhoneNumber())) {
            errors.add("Invalid phone number format.");
        }

        if (student.getBirthDate() != null && student.getBirthDate().isAfter(LocalDate.now())) {
            errors.add("Date of birth must be in the past.");
        }

        // Check for duplicate email/phone (excluding current student)
        if (student.getEmail() != null && staffsService.existsByEmailExcludingId(student.getEmail(), student.getId())) {
            errors.add("The email address is already associated with another account.");
        }

        if (student.getPhoneNumber() != null && staffsService.existsByPhoneNumberExcludingId(student.getPhoneNumber(), student.getId())) {
            errors.add("The phone number is already associated with another account.");
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
        String nameRegex = "^[\\p{L}][\\p{L} .'-]{0,49}$";
        return name.matches(nameRegex);
    }
}
