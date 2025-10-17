package com.example.demo.authenticator.controller;

import com.example.demo.authenticator.service.AuthenticatorsService;
import com.example.demo.user.majorLecturer.model.MajorLecturers;
import com.example.demo.user.majorLecturer.service.MajorLecturersService;
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
@RequestMapping("/major-lecturer-home/personal-page")
@PreAuthorize("hasRole('LECTURER')")
public class MajorLecturerPasswordChangeController {
    private static final Logger logger = LoggerFactory.getLogger(MajorLecturerPasswordChangeController.class);
    private final MajorLecturersService majorEmployeesService;
    private final AuthenticatorsService authenticatorsService;

    public MajorLecturerPasswordChangeController(MajorLecturersService majorEmployeesService, AuthenticatorsService authenticatorsService) {
        this.majorEmployeesService = majorEmployeesService;
        this.authenticatorsService = authenticatorsService;
    }

    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmNewPassword") String confirmNewPassword,
            RedirectAttributes redirectAttributes) {
        try {
            MajorLecturers lecturer = majorEmployeesService.getMajorLecturer();
            if (lecturer == null) {
                logger.warn("No authenticated lecturer found for password change");
                redirectAttributes.addFlashAttribute("passwordErrors", List.of("No authenticated lecturer found."));
                return "redirect:/major-lecturer-home/personal-page";
            }

            Map<String, String> errors = authenticatorsService.validatePasswordChange(
                    lecturer.getId(), currentPassword, newPassword, confirmNewPassword);
            if (!errors.isEmpty()) {
                List<String> errorList = new ArrayList<>();
                errors.forEach((field, msg) -> errorList.add(field + ": " + msg));
                redirectAttributes.addFlashAttribute("passwordErrors", errorList);
                return "redirect:/major-lecturer-home/personal-page";
            }

            authenticatorsService.changePassword(lecturer.getId(), newPassword);
            logger.info("Password updated successfully for lecturer ID {}", lecturer.getId());
            redirectAttributes.addFlashAttribute("message", "Password updated successfully.");
            return "redirect:/major-lecturer-home/personal-page";
        } catch (Exception e) {
            logger.error("Error updating password for lecturer: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("passwordErrors", List.of("Error updating password: " + e.getMessage()));
            return "redirect:/major-lecturer-home/personal-page";
        }
    }
}