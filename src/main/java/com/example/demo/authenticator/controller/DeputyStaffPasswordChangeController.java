package com.example.demo.authenticator.controller;

import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import com.example.demo.user.deputyStaff.service.DeputyStaffsService;
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
@RequestMapping("/deputy-staff-home/personal-page")
@PreAuthorize("hasRole('DEPUTY_STAFF')")
public class DeputyStaffPasswordChangeController {
    private static final Logger logger = LoggerFactory.getLogger(DeputyStaffPasswordChangeController.class);
    private final DeputyStaffsService deputyStaffsService;
    private final AuthenticatorsService authenticatorsService;

    public DeputyStaffPasswordChangeController(DeputyStaffsService deputyStaffsService, AuthenticatorsService authenticatorsService) {
        this.deputyStaffsService = deputyStaffsService;
        this.authenticatorsService = authenticatorsService;
    }

    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmNewPassword") String confirmNewPassword,
            RedirectAttributes redirectAttributes) {
        try {
            DeputyStaffs deputyStaff = deputyStaffsService.getDeputyStaff();
            if (deputyStaff == null) {
                logger.warn("No authenticated deputy staff found for password change");
                redirectAttributes.addFlashAttribute("passwordErrors", List.of("No authenticated deputy staff found."));
                return "redirect:/deputy-staff-home/personal-page";
            }

            Map<String, String> errors = authenticatorsService.validatePasswordChange(
                    deputyStaff.getId(), currentPassword, newPassword, confirmNewPassword);
            if (!errors.isEmpty()) {
                List<String> errorList = new ArrayList<>();
                errors.forEach((field, msg) -> errorList.add(field + ": " + msg));
                redirectAttributes.addFlashAttribute("passwordErrors", errorList);
                return "redirect:/deputy-staff-home/personal-page";
            }

            authenticatorsService.changePassword(deputyStaff.getId(), newPassword);
            logger.info("Password updated successfully for deputy staff ID {}", deputyStaff.getId());
            redirectAttributes.addFlashAttribute("message", "Password updated successfully.");
            return "redirect:/deputy-staff-home/personal-page";
        } catch (Exception e) {
            logger.error("Error updating password for deputy staff: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("passwordErrors", List.of("Error updating password: " + e.getMessage()));
            return "redirect:/deputy-staff-home/personal-page";
        }
    }
}