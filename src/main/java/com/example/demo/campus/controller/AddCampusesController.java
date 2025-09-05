package com.example.demo.campus.controller;
import com.example.demo.admin.model.Admins;
import com.example.demo.admin.service.AdminsService;
import com.example.demo.campus.model.Campuses;
import com.example.demo.campus.service.CampusesService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin-home/campuses-list")
public class AddCampusesController {
    private static final Logger logger = LoggerFactory.getLogger(ListCampusesController.class);
    private final CampusesService campusesService;
    private final AdminsService adminsService;

    public AddCampusesController(CampusesService campusesService, AdminsService adminsService) {
        this.campusesService = campusesService;
        this.adminsService = adminsService;
    }

    @PostMapping("/add-campus")
    public String addCampus(
            @Valid @ModelAttribute("campus") Campuses campus,
            BindingResult bindingResult,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            Model model,
            RedirectAttributes redirectAttributes) {
        List<String> errors = new ArrayList<>();
        errors.addAll(campusesService.validateCampus(campus, avatarFile));
        if (bindingResult.hasErrors()) {
            errors.addAll(bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList()));
        }

        // Generate campusId if not provided
        if (campus.getCampusId() == null || campus.getCampusId().isBlank()) {
            campus.setCampusId(campusesService.generateUniqueCampusId(LocalDate.now()));
        }

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("campuses", campusesService.getCampuses());
            model.addAttribute("campusCounts", campusesService.getCampusCounts());
            return "ListCampuses";
        }

        try {
            // Set creator (e.g., current admin)
            Admins currentAdmin = adminsService.getAdmin(); // Implement this method
            campus.setCreator(currentAdmin);

            if (avatarFile != null && !avatarFile.isEmpty()) {
                campus.setAvatar(avatarFile.getBytes());
            }
            campusesService.addCampus(campus);
            redirectAttributes.addFlashAttribute("message", "Campus added successfully!");
            return "redirect:/admin-home/campuses-list";
        } catch (IOException e) {
            logger.error("Failed to process avatar: {}", e.getMessage(), e);
            errors.add("Failed to process avatar: " + e.getMessage());
            model.addAttribute("errors", errors);
            model.addAttribute("campuses", campusesService.getCampuses());
            model.addAttribute("campusCounts", campusesService.getCampusCounts());
            return "ListCampuses";
        } catch (Exception e) {
            logger.error("Error adding campus: {}", e.getMessage(), e);
            errors.add("An error occurred while adding the campus: " + e.getMessage());
            model.addAttribute("errors", errors);
            model.addAttribute("campuses", campusesService.getCampuses());
            model.addAttribute("campusCounts", campusesService.getCampusCounts());
            return "ListCampuses";
        }
    }
}
