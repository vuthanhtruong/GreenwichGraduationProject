package com.example.demo.authenticator.controller;

import com.example.demo.user.student.model.Students;
import com.example.demo.user.student.service.StudentsService;
import com.example.demo.authenticator.service.AuthenticatorsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/student-home/personal-page")
@PreAuthorize("hasRole('STUDENT')")
public class StudentPasswordChangeController {
    private static final Logger logger = LoggerFactory.getLogger(StudentPasswordChangeController.class);
    private final StudentsService studentsService;
    private final AuthenticatorsService authenticatorsService;

    public StudentPasswordChangeController(StudentsService studentsService, AuthenticatorsService authenticatorsService) {
        this.studentsService = studentsService;
        this.authenticatorsService = authenticatorsService;
    }

    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmNewPassword") String confirmNewPassword,
            RedirectAttributes redirectAttributes) {
        try {
            // Retrieve authenticated student
            Students student = studentsService.getStudent();
            if (student == null) {
                logger.warn("No authenticated student found for password change");
                redirectAttributes.addFlashAttribute("passwordErrors", List.of("No authenticated student found."));
                return "redirect:/student-home/personal-page";
            }

            // Validate password inputs using service
            Map<String, String> errors = authenticatorsService.validatePasswordChange(
                    student.getId(), currentPassword, newPassword, confirmNewPassword);
            if (!errors.isEmpty()) {
                List<String> errorList = new ArrayList<>();
                errors.forEach((field, msg) -> errorList.add(field + ": " + msg));
                redirectAttributes.addFlashAttribute("passwordErrors", errorList);
                return "redirect:/student-home/personal-page";
            }

            // Update password using service
            authenticatorsService.changePassword(student.getId(), newPassword);
            logger.info("Password updated successfully for student ID {}", student.getId());
            redirectAttributes.addFlashAttribute("message", "Password updated successfully.");
            return "redirect:/student-home/personal-page";
        } catch (Exception e) {
            logger.error("Error updating password for student: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("passwordErrors", List.of("Error updating password: " + e.getMessage()));
            return "redirect:/student-home/personal-page";
        }
    }
}