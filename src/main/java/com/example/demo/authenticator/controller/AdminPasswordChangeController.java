package com.example.demo.authenticator.controller;

import com.example.demo.user.admin.model.Admins;
import com.example.demo.user.admin.service.AdminsService;
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
@RequestMapping("/admin-home/personal-page")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPasswordChangeController {
    private static final Logger logger = LoggerFactory.getLogger(AdminPasswordChangeController.class);
    private final AdminsService adminsService;
    private final AuthenticatorsService authenticatorsService;

    public AdminPasswordChangeController(AdminsService adminsService, AuthenticatorsService authenticatorsService) {
        this.adminsService = adminsService;
        this.authenticatorsService = authenticatorsService;
    }

    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmNewPassword") String confirmNewPassword,
            RedirectAttributes redirectAttributes) {
        try {
            // Retrieve authenticated admin
            Admins admin = adminsService.getAdmin();
            if (admin == null) {
                logger.warn("No authenticated admin found for password change");
                redirectAttributes.addFlashAttribute("passwordErrors", List.of("No authenticated admin found."));
                return "redirect:/admin-home/personal-page";
            }

            // Validate password inputs using service
            Map<String, String> errors = authenticatorsService.validatePasswordChange(
                    admin.getId(), currentPassword, newPassword, confirmNewPassword);
            if (!errors.isEmpty()) {
                List<String> errorList = new ArrayList<>();
                errors.forEach((field, msg) -> errorList.add(field + ": " + msg));
                redirectAttributes.addFlashAttribute("passwordErrors", errorList);
                return "redirect:/admin-home/personal-page";
            }

            // Update password using service
            authenticatorsService.changePassword(admin.getId(), newPassword);
            logger.info("Password updated successfully for admin ID {}", admin.getId());
            redirectAttributes.addFlashAttribute("message", "Password updated successfully.");
            return "redirect:/admin-home/personal-page";
        } catch (Exception e) {
            logger.error("Error updating password for admin: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("passwordErrors", List.of("Error updating password: " + e.getMessage()));
            return "redirect:/admin-home/personal-page";
        }
    }
}