package com.example.demo.scholarshipByYear.controller;

import com.example.demo.scholarshipByYear.model.ScholarshipByYear;
import com.example.demo.scholarship.model.Scholarships;
import com.example.demo.scholarshipByYear.service.ScholarshipByYearService;
import com.example.demo.scholarship.service.ScholarshipsService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/admin-home")
@PreAuthorize("hasRole('ADMIN')")
public class ScholarshipByYearController {

    private final ScholarshipsService scholarshipsService;
    private final ScholarshipByYearService scholarshipByYearService;

    @Autowired
    public ScholarshipByYearController(ScholarshipsService scholarshipsService, ScholarshipByYearService scholarshipByYearService) {
        this.scholarshipsService = scholarshipsService;
        this.scholarshipByYearService = scholarshipByYearService;
    }

    @GetMapping("/scholarship-by-year-list")
    public String showScholarshipByYearList(Model model, HttpSession session) {
        Integer admissionYear = (Integer) session.getAttribute("scholarshipAdmissionYear");
        return listScholarshipsByYear(model, admissionYear, session);
    }

    @PostMapping("/scholarship-by-year-list")
    public String listScholarshipsByYear(Model model, @RequestParam(value = "admissionYear", required = false) Integer admissionYear, HttpSession session) {
        try {
            if (admissionYear != null) {
                session.setAttribute("scholarshipAdmissionYear", admissionYear);
            }

            List<Integer> admissionYearsFromScholarships = scholarshipByYearService.getAllAdmissionYears();
            int currentYear = LocalDate.now().getYear();
            List<Integer> futureYears = IntStream.rangeClosed(currentYear, currentYear + 5)
                    .boxed()
                    .filter(year -> !admissionYearsFromScholarships.contains(year))
                    .collect(Collectors.toList());
            admissionYearsFromScholarships.addAll(futureYears);
            List<Integer> admissionYears = admissionYearsFromScholarships.stream()
                    .sorted(Comparator.reverseOrder())
                    .collect(Collectors.toList());

            Integer selectedYear = admissionYear != null ? admissionYear : currentYear;
            List<Scholarships> allScholarships = scholarshipsService.getAllScholarships();
            List<ScholarshipByYear> scholarshipByYears = scholarshipByYearService.getScholarshipsByYear(selectedYear);

            Map<String, ScholarshipByYear> scholarshipByYearMap = scholarshipByYears.stream()
                    .collect(Collectors.toMap(
                            s -> s.getId().getScholarshipId(),
                            s -> s,
                            (existing, replacement) -> existing
                    ));

            model.addAttribute("allScholarships", allScholarships != null ? allScholarships : List.of());
            model.addAttribute("admissionYears", admissionYears != null ? admissionYears : List.of());
            model.addAttribute("selectedYear", selectedYear);
            model.addAttribute("scholarshipByYearMap", scholarshipByYearMap != null ? scholarshipByYearMap : Map.of());
            model.addAttribute("scholarship", new Scholarships());

            return "AdminScholarshipByYearList";
        } catch (Exception e) {
            Map<String, String> errors = new HashMap<>();
            errors.put("general", "Failed to load scholarships: " + e.getMessage());
            model.addAttribute("editErrors", errors);
            model.addAttribute("allScholarships", List.of());
            model.addAttribute("admissionYears", List.of());
            model.addAttribute("selectedYear", LocalDate.now().getYear());
            model.addAttribute("scholarshipByYearMap", Map.of());
            model.addAttribute("scholarship", new Scholarships());
            return "AdminScholarshipByYearList";
        }
    }

    @PostMapping("/update-scholarship-by-year")
    public String updateScholarshipByYear(
            @RequestParam("admissionYear") Integer admissionYear,
            @RequestParam Map<String, String> params,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (entry.getKey().startsWith("amount_")) {
                    String scholarshipId = entry.getKey().substring("amount_".length());
                    Double amount = entry.getValue().isEmpty() ? null : Double.parseDouble(entry.getValue());
                    Double discountPercentage = params.get("discountPercentage_" + scholarshipId) != null
                            ? (params.get("discountPercentage_" + scholarshipId).isEmpty() ? null : Double.parseDouble(params.get("discountPercentage_" + scholarshipId)))
                            : null;
                    scholarshipByYearService.updateScholarshipByYear(scholarshipId, admissionYear, amount, discountPercentage);
                }
            }
            redirectAttributes.addFlashAttribute("successMessage", "Scholarship amounts updated successfully!");
        } catch (Exception e) {
            Map<String, String> errors = new HashMap<>();
            errors.put("general", "Failed to update scholarship amounts: " + e.getMessage());
            redirectAttributes.addFlashAttribute("editErrors", errors);
        }
        session.setAttribute("scholarshipAdmissionYear", admissionYear);
        return "redirect:/admin-home/scholarship-by-year-list";
    }

    @PostMapping("/add-scholarship")
    public String addScholarship(
            @Valid @ModelAttribute("scholarship") Scholarships scholarship,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        Map<String, String> errors = new HashMap<>(scholarshipsService.validateScholarship(scholarship));

        // Xử lý lỗi từ BindingResult
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> {
                String field = bindingResult.getFieldError() != null ? bindingResult.getFieldError().getField() : "general";
                errors.put(field, error.getDefaultMessage());
            });
        }

        Integer selectedYear = (Integer) session.getAttribute("scholarshipAdmissionYear");
        if (selectedYear == null) {
            selectedYear = LocalDate.now().getYear();
        }

        if (!errors.isEmpty()) {
            model.addAttribute("openAddOverlay", true);
            model.addAttribute("editErrors", errors);
            model.addAttribute("scholarship", scholarship);
            return listScholarshipsByYear(model, selectedYear, session);
        }

        try {
            String scholarshipId = scholarshipsService.generateUniqueScholarshipId();
            scholarship.setScholarshipId(scholarshipId);
            scholarship.setCreatedAt(LocalDateTime.now());
            scholarshipsService.addScholarship(scholarship);

            redirectAttributes.addFlashAttribute("successMessage", "Scholarship added successfully!");
            return "redirect:/admin-home/scholarship-by-year-list";
        } catch (Exception e) {
            errors.put("general", "Failed to add scholarship: " + e.getMessage());
            model.addAttribute("openAddOverlay", true);
            model.addAttribute("editErrors", errors);
            model.addAttribute("scholarship", scholarship);
            return listScholarshipsByYear(model, selectedYear, session);
        }
    }
}