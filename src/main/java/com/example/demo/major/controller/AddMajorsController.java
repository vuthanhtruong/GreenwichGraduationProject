package com.example.demo.major.controller;

import com.example.demo.user.admin.model.Admins;
import com.example.demo.user.admin.service.AdminsService;
import com.example.demo.major.model.Majors;
import com.example.demo.major.service.MajorsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

@Controller
@RequestMapping("/admin-home")
public class AddMajorsController {
    private static final Logger logger = LoggerFactory.getLogger(AddMajorsController.class);
    private final MajorsService majorsService;
    private final AdminsService adminsService;

    public AddMajorsController(MajorsService majorsService, AdminsService adminsService) {
        this.majorsService = majorsService;
        this.adminsService = adminsService;
    }

    @PostMapping("/majors-list/add-major")
    public String addMajor(
            @ModelAttribute("major") Majors major,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            Model model,
            RedirectAttributes redirectAttributes) {
        Map<String, String> errors = majorsService.validateMajor(major, avatarFile);

        if (major.getMajorId() == null || major.getMajorId().isBlank()) {
            major.setMajorId(majorsService.generateUniqueMajorId(LocalDate.now()));
        }

        if (!errors.isEmpty()) {
            model.addAttribute("openAddOverlay", true);
            model.addAttribute("major", major);
            model.addAttribute("errors", errors);
            model.addAttribute("majors", majorsService.getMajors());
            return "ListMajors";
        }

        try {
            Admins currentAdmin = adminsService.getAdmin();
            major.setCreator(currentAdmin);
            if (avatarFile != null && !avatarFile.isEmpty()) {
                major.setAvatar(avatarFile.getBytes());
            }
            majorsService.addMajor(major);
            redirectAttributes.addFlashAttribute("message", "Major added successfully!");
            return "redirect:/admin-home/majors-list";
        } catch (IOException e) {
            logger.error("Failed to process avatar: {}", e.getMessage());
            errors.put("avatarFile", "Failed to process avatar: " + e.getMessage());
            model.addAttribute("openAddOverlay", true);
            model.addAttribute("major", major);
            model.addAttribute("errors", errors);
            model.addAttribute("majors", majorsService.getMajors());
            return "ListMajors";
        } catch (Exception e) {
            logger.error("Error adding major: {}", e.getMessage());
            errors.put("general", "An error occurred while adding the major: " + e.getMessage());
            model.addAttribute("openAddOverlay", true);
            model.addAttribute("major", major);
            model.addAttribute("errors", errors);
            model.addAttribute("majors", majorsService.getMajors());
            return "ListMajors";
        }
    }
}