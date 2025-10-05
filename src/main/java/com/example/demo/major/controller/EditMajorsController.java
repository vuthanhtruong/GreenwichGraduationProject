package com.example.demo.major.controller;

import com.example.demo.admin.service.AdminsService;
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
import java.util.Map;

@Controller
@RequestMapping("/admin-home/majors-list")
public class EditMajorsController {
    private static final Logger logger = LoggerFactory.getLogger(EditMajorsController.class);
    private final MajorsService majorsService;
    private final AdminsService adminsService;

    public EditMajorsController(MajorsService majorsService, AdminsService adminsService) {
        this.majorsService = majorsService;
        this.adminsService = adminsService;
    }

    @PostMapping("/edit-major-form")
    public String showEditMajorForm(@RequestParam String id, Model model) {
        Majors major = majorsService.getMajorById(id);
        model.addAttribute("major", major);
        return "EditFormMajor";
    }

    @PostMapping("/edit-major")
    public String editMajor(
            @ModelAttribute("major") Majors major,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            RedirectAttributes redirectAttributes,
            Model model) {
        Map<String, String> errors = majorsService.validateMajor(major, avatarFile);
        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("major", major);
            model.addAttribute("majors", majorsService.getMajors());
            return "ListMajors";
        }
        try {
            majorsService.editMajor(major, avatarFile);
            redirectAttributes.addFlashAttribute("message", "Major updated successfully!");
        } catch (IOException e) {
            logger.error("IO error updating major: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error updating major: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error updating major: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error updating major: " + e.getMessage());
        }
        return "redirect:/admin-home/majors-list";
    }
}