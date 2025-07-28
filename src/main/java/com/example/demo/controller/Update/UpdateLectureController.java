package com.example.demo.controller.Update;

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

import java.time.LocalDate;
import java.util.ArrayList;
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
    public String handleEditLecturePost(@RequestParam String id, Model model) {
        Lecturers lecture = lecturesService.getLecturerById(id);
        model.addAttribute("lecture", lecture);
        model.addAttribute("majors", staffsService.getMajors());
        return "EditLectureForm";
    }

    @PutMapping("/edit-lecture-form")
    public String updateLecture(
            @Valid @ModelAttribute("lecture") Lecturers lecture,
            BindingResult bindingResult,
            @RequestParam(value = "avatar", required = false) MultipartFile avatarFile,
            RedirectAttributes redirectAttributes,
            ModelMap modelMap) {

        // Check if user is authenticated
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("error", "Unauthorized access.");
            return "redirect:/staff-home/lectures-list";
        }

        // Validate lecture data
        List<String> errors = new ArrayList<>();
        validateLecture(lecture, bindingResult, avatarFile, errors);

        if (!errors.isEmpty()) {
            modelMap.addAttribute("errors", errors);
            modelMap.addAttribute("majors", staffsService.getMajors());
            return "EditLectureForm";
        }

        try {
            // Check if lecture exists
            if (!personsService.existsPersonById(lecture.getId())) {
                redirectAttributes.addFlashAttribute("error", "Lecture with ID " + lecture.getId() + " not found.");
                return "redirect:/staff-home/lectures-list";
            }

            // Handle avatar update
            if (avatarFile != null && !avatarFile.isEmpty()) {
                lecture.setAvatar(avatarFile.getBytes());
            } else {
                // Retain existing avatar if no new file is uploaded
                Lecturers existingLecture = lecturesService.getLecturerById(lecture.getId());
                lecture.setAvatar(existingLecture.getAvatar());
            }

            // Update lecture
            lecturesService.updateLecturer(lecture.getId(), lecture);
            redirectAttributes.addFlashAttribute("successMessage", "Lecture updated successfully!");
        } catch (DataAccessException e) {
            redirectAttributes.addFlashAttribute("error", "Database error while updating lecture: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Unexpected error while updating lecture: " + e.getMessage());
        }

        return "redirect:/staff-home/lectures-list";
    }

    private void validateLecture(Lecturers lecture, BindingResult bindingResult, MultipartFile avatarFile, List<String> errors) {
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
            if (contentType == null || !contentType.startsWith("image/")) {
                errors.add("Avatar must be an image file (e.g., JPEG, PNG).");
            } else if (avatarFile.getSize() > 5 * 1024 * 1024) { // 5MB limit
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