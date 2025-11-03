package com.example.demo.major.controller;

import com.example.demo.user.admin.service.AdminsService;
import com.example.demo.major.service.MajorsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin-home/majors-list")
public class DeleteMajorsController {
    private static final Logger logger = LoggerFactory.getLogger(DeleteMajorsController.class);
    private final MajorsService majorsService;

    public DeleteMajorsController(MajorsService majorsService) {
        this.majorsService = majorsService;
    }

    @PostMapping("/delete-major")
    public String deleteMajor(@RequestParam String id, RedirectAttributes redirectAttributes) {
        try {
            majorsService.deleteMajor(id);
            redirectAttributes.addFlashAttribute("message", "Major deleted successfully!");
        } catch (Exception e) {
            logger.error("Error deleting major: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error deleting major: " + e.getMessage());
        }
        return "redirect:/admin-home/majors-list";
    }
}