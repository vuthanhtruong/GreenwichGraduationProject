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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
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
    public String addCampus(@Valid @ModelAttribute("campus") Campuses campus, BindingResult bindingResult,
                            RedirectAttributes redirectAttributes, Model model) {
        List<String> errors = campusesService.validateCampus(campus);
        if (bindingResult.hasErrors()) {
            errors.addAll(bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList()));
        }
        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("campuses", campusesService.getCampuses());
            model.addAttribute("randomCampusId", campusesService.generateUniqueCampusId(LocalDate.now()));
            return "ListCampuses";
        }
        try {
            Admins currentAdmin = adminsService.getAdmin();
            campus.setCampusId(campusesService.generateUniqueCampusId(LocalDate.now()));
            campus.setCreator(currentAdmin);
            campusesService.addCampus(campus);
            redirectAttributes.addFlashAttribute("message", "Campus added successfully!");
        } catch (Exception e) {
            logger.error("Error adding campus: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error adding campus: " + e.getMessage());
        }
        return "redirect:/admin-home/campuses-list";
    }
}
