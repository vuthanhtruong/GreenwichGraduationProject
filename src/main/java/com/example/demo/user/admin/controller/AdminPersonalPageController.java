package com.example.demo.user.admin.controller;

import com.example.demo.user.admin.model.Admins;
import com.example.demo.user.admin.service.AdminsService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin-home/personal-page")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPersonalPageController {
    private static final Logger logger = LoggerFactory.getLogger(AdminPersonalPageController.class);
    private final AdminsService adminsService;

    public AdminPersonalPageController(AdminsService adminsService) {
        this.adminsService = adminsService;
    }

    @GetMapping
    public String showAdminPersonalPage(Model model, HttpSession session) {
        try {
            Admins admin = adminsService.getAdmin();
            if (admin == null) {
                logger.warn("No authenticated admin found");
                model.addAttribute("errors", List.of("No authenticated admin found."));
                model.addAttribute("admin", new Admins());
                model.addAttribute("adminForm", new Admins());
                model.addAttribute("avatarUrl", "/DefaultAvatar/Admin_Male.png");
                return "AdminPersonalPage";
            }

            model.addAttribute("admin", admin);
            model.addAttribute("adminForm", admin);
            model.addAttribute("avatarUrl", admin.getAvatar() != null ? "/admin-home/personal-page/avatar/" + admin.getId() : admin.getDefaultAvatarPath());
            return "AdminPersonalPage";
        } catch (Exception e) {
            logger.error("Error loading personal page: {}", e.getMessage(), e);
            model.addAttribute("errors", List.of("Error loading personal page: " + e.getMessage()));
            model.addAttribute("admin", new Admins());
            model.addAttribute("adminForm", new Admins());
            model.addAttribute("avatarUrl", "/DefaultAvatar/Admin_Male.png");
            return "AdminPersonalPage";
        }
    }

    @PostMapping("/edit")
    public String editAdminPersonalPage(
            @ModelAttribute("adminForm") Admins adminForm,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        try {
            Admins admin = adminsService.getAdmin();
            if (admin == null) {
                logger.warn("No authenticated admin found for edit");
                redirectAttributes.addFlashAttribute("errors", List.of("No authenticated admin found."));
                return "redirect:/admin-home/personal-page";
            }

            if (!admin.getId().equals(adminForm.getId())) {
                logger.warn("Authenticated admin ID {} attempted to edit admin ID {}", admin.getId(), adminForm.getId());
                redirectAttributes.addFlashAttribute("errors", List.of("Not authorized to edit this profile."));
                return "redirect:/admin-home/personal-page";
            }

            Map<String, String> errors = adminsService.validateAdmin(adminForm, avatarFile);
            if (!errors.isEmpty()) {
                List<String> errorList = new ArrayList<>();
                errors.forEach((field, msg) -> errorList.add(field + ": " + msg));
                model.addAttribute("errors", errorList);
                model.addAttribute("admin", admin);
                model.addAttribute("adminForm", adminForm);
                model.addAttribute("avatarUrl", admin.getAvatar() != null ? "/admin-home/personal-page/avatar/" + admin.getId() : admin.getDefaultAvatarPath());
                return "AdminPersonalPage";
            }

            adminsService.editAdmin(adminForm, avatarFile);
            logger.info("Admin ID {} updated successfully", admin.getId());
            redirectAttributes.addFlashAttribute("message", "Profile updated successfully.");
            return "redirect:/admin-home/personal-page";
        } catch (IOException e) {
            logger.error("IO error updating admin profile: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errors", List.of("Error uploading avatar: " + e.getMessage()));
            return "redirect:/admin-home/personal-page";
        } catch (Exception e) {
            logger.error("Error updating admin profile: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errors", List.of("Error updating profile: " + e.getMessage()));
            return "redirect:/admin-home/personal-page";
        }
    }

    @GetMapping("/avatar/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getAdminAvatar(@PathVariable String id) {
        Admins admin = adminsService.getAdminById(id);
        if (admin != null && admin.getAvatar() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(admin.getAvatar());
        }
        return ResponseEntity.notFound().build();
    }
}