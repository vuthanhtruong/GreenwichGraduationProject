package com.example.demo.campus.controller;

import com.example.demo.campus.service.CampusesService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin-home/campuses-list")
public class DeleleCampusesController {
    private final CampusesService campusesService;

    public DeleleCampusesController(CampusesService campusesService) {
        this.campusesService = campusesService;
    }

    @PostMapping("/delete-campus")
    public String deleteCampus(@RequestParam String id, RedirectAttributes redirectAttributes) {
        try {
            campusesService.deleteCampus(id);
            redirectAttributes.addFlashAttribute("message", "Campus deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting campus: " + e.getMessage());
        }
        return "redirect:/admin-home/campuses-list";
    }
}
