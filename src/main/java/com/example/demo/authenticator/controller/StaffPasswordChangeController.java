package com.example.demo.authenticator.controller;

import com.example.demo.user.staff.model.Staffs;
import com.example.demo.user.staff.service.StaffsService;
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
@RequestMapping("/staff-home/personal-page")
@PreAuthorize("hasRole('STAFF')")
public class StaffPasswordChangeController {
    private static final Logger logger = LoggerFactory.getLogger(StaffPasswordChangeController.class);
    private final StaffsService staffsService;
    private final AuthenticatorsService authenticatorsService;

    public StaffPasswordChangeController(StaffsService staffsService, AuthenticatorsService authenticatorsService) {
        this.staffsService = staffsService;
        this.authenticatorsService = authenticatorsService;
    }

    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmNewPassword") String confirmNewPassword,
            RedirectAttributes redirectAttributes) {
        try {
            // Retrieve authenticated staff
            Staffs staff = staffsService.getStaff();
            if (staff == null) {
                logger.warn("No authenticated staff found for password change");
                redirectAttributes.addFlashAttribute("passwordErrors", List.of("No authenticated staff found."));
                return "redirect:/staff-home/personal-page";
            }

            // Validate password inputs using service
            Map<String, String> errors = authenticatorsService.validatePasswordChange(
                    staff.getId(), currentPassword, newPassword, confirmNewPassword);
            if (!errors.isEmpty()) {
                List<String> errorList = new ArrayList<>();
                errors.forEach((field, msg) -> errorList.add(field + ": " + msg));
                redirectAttributes.addFlashAttribute("passwordErrors", errorList);
                return "redirect:/staff-home/personal-page";
            }

            // Update password using service
            authenticatorsService.changePassword(staff.getId(), newPassword);
            logger.info("Password updated successfully for staff ID {}", staff.getId());
            redirectAttributes.addFlashAttribute("message", "Password updated successfully.");
            return "redirect:/staff-home/personal-page";
        } catch (Exception e) {
            logger.error("Error updating password for staff: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("passwordErrors", List.of("Error updating password: " + e.getMessage()));
            return "redirect:/staff-home/personal-page";
        }
    }
}