package com.example.demo.Specialization.controller;

import com.example.demo.Specialization.model.Specialization;
import com.example.demo.Specialization.service.SpecializationService;
import com.example.demo.major.model.Majors;
import com.example.demo.major.service.MajorsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin-home/majors-list/specializations-list")
@PreAuthorize("hasRole('ADMIN')")
public class ListSpecializationsController {

    private final SpecializationService specializationService;
    private final MajorsService majorsService;

    @Autowired
    public ListSpecializationsController(SpecializationService specializationService, MajorsService majorsService) {
        this.specializationService = specializationService;
        this.majorsService = majorsService;
    }

    @GetMapping("")
    public String listSpecializations(
            Model model,
            HttpSession session,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize) {
        try {
            String majorId = (String) session.getAttribute("currentMajorId");
            if (majorId == null) {
                model.addAttribute("errors", List.of("No major selected. Please select a major."));
                model.addAttribute("newSpecialization", new Specialization());
                model.addAttribute("major", new Majors());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", 5);
                model.addAttribute("totalSpecializations", 0);
                return "SpecializationsList";
            }

            Majors major = majorsService.getMajorById(majorId);
            if (major == null) {
                model.addAttribute("errors", List.of("Major not found for ID: " + majorId));
                model.addAttribute("newSpecialization", new Specialization());
                model.addAttribute("major", new Majors());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", 5);
                model.addAttribute("totalSpecializations", 0);
                return "SpecializationsList";
            }

            if (pageSize == null) {
                pageSize = (Integer) session.getAttribute("specializationPageSize");
                if (pageSize == null) {
                    pageSize = 20;
                }
            }
            session.setAttribute("specializationPageSize", pageSize);

            Long totalSpecializations = specializationService.numberOfSpecializations(major);
            int totalPages = Math.max(1, (int) Math.ceil((double) totalSpecializations / pageSize));
            page = Math.max(1, Math.min(page, totalPages));
            session.setAttribute("specializationPage", page);
            session.setAttribute("specializationTotalPages", totalPages);

            if (totalSpecializations == 0) {
                model.addAttribute("specializations", new ArrayList<>());
                model.addAttribute("newSpecialization", new Specialization());
                model.addAttribute("major", major);
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", pageSize);
                model.addAttribute("totalSpecializations", 0);
                model.addAttribute("message", "No specializations found for this major.");
                model.addAttribute("alertClass", "alert-warning");
                return "SpecializationsList";
            }

            int firstResult = (page - 1) * pageSize;
            List<Specialization> specializations = specializationService.getPaginated(firstResult, pageSize, major);

            model.addAttribute("specializations", specializations);
            model.addAttribute("newSpecialization", new Specialization());
            model.addAttribute("major", major);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalSpecializations", totalSpecializations);
            return "SpecializationsList";
        } catch (SecurityException e) {
            model.addAttribute("errors", List.of("Security error: " + e.getMessage()));
            model.addAttribute("newSpecialization", new Specialization());
            model.addAttribute("major", new Majors());
            model.addAttribute("currentPage", 1);
            model.addAttribute("totalPages", 1);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalSpecializations", 0);
            return "SpecializationsList";
        }
    }

    @PostMapping("/view-specializations")
    public String viewSpecializationsByMajor(@RequestParam("id") String majorId, Model model, HttpSession session) {
        Majors major = majorsService.getMajorById(majorId);
        if (major == null) {
            model.addAttribute("errors", List.of("Major not found"));
            return "redirect:/admin-home/majors-list";
        }

        session.setAttribute("currentMajorId", majorId);
        List<Specialization> specializations = specializationService.getPaginated(0, 5, major);
        model.addAttribute("specializations", specializations.isEmpty() ? new ArrayList<>() : specializations);
        model.addAttribute("major", major);
        model.addAttribute("newSpecialization", new Specialization());
        model.addAttribute("currentPage", 1);
        model.addAttribute("totalPages", Math.max(1, (int) Math.ceil((double) specializationService.numberOfSpecializations(major) / 5)));
        model.addAttribute("pageSize", 5);
        model.addAttribute("totalSpecializations", specializationService.numberOfSpecializations(major));
        return "SpecializationsList";
    }
    @PostMapping("/delete-specialization")
    public String deleteSpecialization(
            @RequestParam("specializationId") String specializationId,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        try {
            specializationService.deleteSpecialization(specializationId);
            redirectAttributes.addFlashAttribute("message", "Specialization deleted successfully!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errors", List.of("Error deleting specialization: " + e.getMessage()));
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }
        return "redirect:/admin-home/majors-list/specializations-list";
    }
}