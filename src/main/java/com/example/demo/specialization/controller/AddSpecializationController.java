package com.example.demo.specialization.controller;

import com.example.demo.specialization.model.Specialization;
import com.example.demo.specialization.service.SpecializationService;
import com.example.demo.major.model.Majors;
import com.example.demo.major.service.MajorsService;
import com.example.demo.user.admin.model.Admins;
import com.example.demo.user.staff.service.StaffsService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin-home/majors-list/specializations-list")
public class AddSpecializationController {

    private final SpecializationService specializationService;
    private final MajorsService majorsService;
    private final StaffsService staffsService;

    @Autowired
    public AddSpecializationController(SpecializationService specializationService, MajorsService majorsService, StaffsService staffsService) {
        this.specializationService = specializationService;
        this.majorsService = majorsService;
        this.staffsService = staffsService;
    }

    @PostMapping("/add-specialization")
    public String addSpecialization(
            @Valid @ModelAttribute("newSpecialization") Specialization specialization,
            @RequestParam("majorId") String majorId,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        Majors major = majorsService.getMajorById(majorId);
        // Assuming the current admin is the creator; adjust based on authentication
        Admins creator = (Admins) session.getAttribute("currentAdmin"); // Placeholder; replace with auth logic
        if (creator == null) {
            creator = new Admins(); // Temporary placeholder
        }
        specialization.setCreator(creator);

        List<String> errors = new ArrayList<>(specializationService.specializationValidation(specialization).values());

        if (!errors.isEmpty()) {
            model.addAttribute("openAddOverlay", true);
            model.addAttribute("errors", errors);
            model.addAttribute("newSpecialization", specialization);
            model.addAttribute("major", major != null ? major : new Majors());
            model.addAttribute("specializations", specializationService.getPaginated(0,
                    (Integer) session.getAttribute("specializationPageSize") != null ?
                            (Integer) session.getAttribute("specializationPageSize") : 5, major));
            model.addAttribute("currentPage", session.getAttribute("specializationPage") != null ?
                    session.getAttribute("specializationPage") : 1);
            model.addAttribute("totalPages", session.getAttribute("specializationTotalPages") != null ?
                    session.getAttribute("specializationTotalPages") : 1);
            model.addAttribute("pageSize", session.getAttribute("specializationPageSize") != null ?
                    session.getAttribute("specializationPageSize") : 5);
            model.addAttribute("totalSpecializations", specializationService.numberOfSpecializations(major));
            return "SpecializationsList";
        }

        try {
            String specializationId = specializationService.generateUniqueId(majorId, LocalDate.now());
            specialization.setSpecializationId(specializationId);
            specialization.setMajor(major);
            specialization.setCreatedAt(LocalDateTime.now()); // Set to 12:36 AM +07, October 07, 2025

            specializationService.addSpecialization(specialization);

            redirectAttributes.addFlashAttribute("message", "Specialization added successfully!");
            session.setAttribute("currentMajorId", majorId);
            return "redirect:/admin-home/majors-list/specializations-list";
        } catch (Exception e) {
            errors.add("An error occurred while adding the specialization: " + e.getMessage());
            model.addAttribute("openAddOverlay", true);
            model.addAttribute("errors", errors);
            model.addAttribute("newSpecialization", specialization);
            model.addAttribute("major", major != null ? major : new Majors());
            model.addAttribute("specializations", specializationService.getPaginated(0,
                    (Integer) session.getAttribute("specializationPageSize") != null ?
                            (Integer) session.getAttribute("specializationPageSize") : 5, major));
            model.addAttribute("currentPage", session.getAttribute("specializationPage") != null ?
                    session.getAttribute("specializationPage") : 1);
            model.addAttribute("totalPages", session.getAttribute("specializationTotalPages") != null ?
                    session.getAttribute("specializationTotalPages") : 1);
            model.addAttribute("pageSize", session.getAttribute("specializationPageSize") != null ?
                    session.getAttribute("specializationPageSize") : 5);
            model.addAttribute("totalSpecializations", specializationService.numberOfSpecializations(major));
            return "SpecializationsList";
        }
    }
}