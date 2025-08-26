package com.example.demo.campus.controller;

import com.example.demo.admin.service.AdminsService;
import com.example.demo.campus.service.CampusesService;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.logging.Logger;

@Controller
@RequestMapping("/admin-home/campuses-list")
public class DeleleCampusesController {
    private final CampusesService campusesService;
    private final AdminsService adminsService;

    public DeleleCampusesController(CampusesService campusesService, AdminsService adminsService) {
        this.campusesService = campusesService;
        this.adminsService = adminsService;
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
