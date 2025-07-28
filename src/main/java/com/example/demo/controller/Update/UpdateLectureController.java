package com.example.demo.controller.Update;

import com.example.demo.entity.Gender;
import com.example.demo.entity.Lecturers;
import com.example.demo.service.LecturesService;
import com.example.demo.service.PersonsService;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/staff-home/lectures-list")
public class UpdateLectureController {
    private final StaffsService staffsService;
    private final StudentsService studentsService;
    private final LecturesService lecturesService;
    private final PersonsService personsService;

    public UpdateLectureController(StaffsService staffsService, LecturesService lecturesService, StudentsService studentsService, PersonsService personsService) {
        this.staffsService = staffsService;
        this.studentsService = studentsService;
        this.lecturesService = lecturesService;
        this.personsService = personsService;
    }

    @PostMapping("/edit-lecture-form")
    public String handleEditlecturePost(@RequestParam String id, Model model) {
        Lecturers lecture = lecturesService.getLecturerById(id);
        model.addAttribute("lecture", lecture);
        model.addAttribute("majors", staffsService.getMajors());
        model.addAttribute("genders", Arrays.asList(Gender.values()));
        return "EditLectureForm";
    }

    @PutMapping("/edit-lecture-form")
    public String updatelecture(
            @Valid @ModelAttribute("lecture") Lecturers lecture,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            ModelMap modelMap) {

        List<String> errors = new ArrayList<>();
        validatelecture(lecture, bindingResult, avatarFile, errors);

        if (!errors.isEmpty()) {
            System.out.println("Validation errors: " + errors);
            modelMap.addAttribute("errors", errors);
            modelMap.addAttribute("majors", staffsService.getMajors());
            modelMap.addAttribute("genders", Arrays.asList(Gender.values()));
            return "EditLectureForm";
        }

        try {
            // Check if lecture exists
            if (!personsService.existsPersonById(lecture.getId())) {
                redirectAttributes.addFlashAttribute("error", "Lecture with ID " + lecture.getId() + " not found.");
                return "redirect:/staff-home/lectures-list";
            }
            // Handle avatar upload
            if (avatarFile != null && !avatarFile.isEmpty()) {
                byte[] avatarBytes = avatarFile.getBytes();
                lecture.setAvatar(avatarBytes);
                System.out.println("Avatar bytes set: " + avatarBytes.length);
            } else {
                // Retain existing avatar
                Lecturers existingLecture = lecturesService.getLecturerById(lecture.getId());
                lecture.setAvatar(existingLecture.getAvatar());
            }
            // Update lecture
            lecturesService.updateLecturer(lecture.getId(), lecture);
            redirectAttributes.addFlashAttribute("successMessage", "Lecture updated successfully!");
        } catch (IOException e) {
            System.err.println("IOException during avatar processing: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Failed to process avatar: " + e.getMessage());
        } catch (DataAccessException e) {
            System.err.println("Database error while updating lecture: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Database error while updating lecture: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error while updating lecture: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Unexpected error while updating lecture: " + e.getMessage());
        }

        return "redirect:/staff-home/lectures-list";
    }

    private void validatelecture(Lecturers lecture, BindingResult bindingResult, MultipartFile avatarFile, List<String> errors) {
        // Annotation-based validation
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

        if (lecture.getEmail() != null && !isValidEmail(lecture.getEmail())) {
            errors.add("Invalid email format.");
        }

        if (lecture.getPhoneNumber() != null && !isValidPhoneNumber(lecture.getPhoneNumber())) {
            errors.add("Invalid phone number format.");
        }

        if (lecture.getBirthDate() != null && lecture.getBirthDate().isAfter(LocalDate.now())) {
            errors.add("Date of birth must be in the past.");
        }
        // Check for duplicate email/phone (excluding current lecture)
        if (lecture.getEmail() != null && personsService.existsByEmailExcludingId(lecture.getEmail(), lecture.getId())) {
            errors.add("The email address is already associated with another account.");
        }

        if (lecture.getPhoneNumber() != null && personsService.existsByPhoneNumberExcludingId(lecture.getPhoneNumber(), lecture.getId())) {
            errors.add("The phone number is already associated with another account.");
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