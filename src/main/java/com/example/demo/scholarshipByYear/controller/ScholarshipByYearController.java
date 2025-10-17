package com.example.demo.scholarshipByYear.controller;

import com.example.demo.user.admin.model.Admins;
import com.example.demo.user.admin.service.AdminsService;
import com.example.demo.entity.Enums.ActivityStatus;
import com.example.demo.entity.Enums.ContractStatus;
import com.example.demo.scholarship.model.Scholarships;
import com.example.demo.scholarshipByYear.model.ScholarshipByYear;
import com.example.demo.scholarshipByYear.model.ScholarshipByYearId;
import com.example.demo.scholarship.service.ScholarshipsService;
import com.example.demo.scholarshipByYear.service.ScholarshipByYearService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final AdminsService adminsService;

    @Autowired
    public ScholarshipByYearController(ScholarshipsService scholarshipsService,
                                       ScholarshipByYearService scholarshipByYearService,
                                       AdminsService adminsService) {
        this.scholarshipsService = scholarshipsService;
        this.scholarshipByYearService = scholarshipByYearService;
        this.adminsService = adminsService;
    }

    @GetMapping("/scholarship-by-year-list")
    public String showScholarshipByYearList(Model model, HttpSession session) {
        Integer admissionYear = (Integer) session.getAttribute("scholarshipAdmissionYear");
        return listScholarshipsByYear(model, admissionYear, session);
    }

    @PostMapping("/scholarship-by-year-list")
    public String listScholarshipsByYear(Model model,
                                         @RequestParam(value = "admissionYear", required = false) Integer admissionYear,
                                         HttpSession session) {
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
    @Transactional
    public String updateScholarshipByYear(
            @RequestParam("admissionYear") Integer admissionYear,
            @RequestParam Map<String, String> params,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        try {
            int currentYear = LocalDate.now().getYear();
            if (admissionYear == null || admissionYear < currentYear) {
                redirectAttributes.addFlashAttribute("errorMessage", "Cannot update scholarships for past years or invalid year.");
                return "redirect:/admin-home/scholarship-by-year-list";
            }

            Admins admin = adminsService.getAdmin();
            if (admin == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Admin not found.");
                return "redirect:/admin-home/scholarship-by-year-list";
            }

            List<String> errors = new ArrayList<>();
            boolean anyUpdate = false;

            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (entry.getKey().startsWith("amount_")) {
                    String scholarshipId = entry.getKey().substring("amount_".length());
                    String amountValue = entry.getValue();
                    String discountValue = params.get("discountPercentage_" + scholarshipId);

                    try {
                        Double amount = amountValue.isEmpty() ? null : Double.parseDouble(amountValue);
                        Double discountPercentage = discountValue != null && !discountValue.isEmpty() ?
                                Double.parseDouble(discountValue) : null;

                        if (amount != null && amount < 0) {
                            errors.add("Amount for scholarship " + scholarshipId + " cannot be negative.");
                            continue;
                        }
                        if (discountPercentage != null && (discountPercentage < 0 || discountPercentage > 100)) {
                            errors.add("Discount percentage for scholarship " + scholarshipId + " must be between 0 and 100.");
                            continue;
                        }

                        ScholarshipByYear existing = scholarshipByYearService.findById(
                                new ScholarshipByYearId(scholarshipId, admissionYear));
                        if (existing == null) {
                            errors.add("No existing scholarship record found for ID " + scholarshipId);
                            continue;
                        }
                        if (existing.getContractStatus() == ContractStatus.ACTIVE) {
                            errors.add("Cannot update scholarship " + scholarshipId + ": Contract is finalized.");
                            continue;
                        }
                        if (existing.getStatus() == ActivityStatus.DEACTIVATED) {
                            errors.add("Cannot update scholarship " + scholarshipId + ": Scholarship is deactivated.");
                            continue;
                        }

                        existing.setAmount(amount != null ? amount : existing.getAmount());
                        existing.setDiscountPercentage(discountPercentage != null ? discountPercentage : existing.getDiscountPercentage());
                        existing.setCreator(admin);
                        scholarshipByYearService.updateScholarshipByYear(existing);
                        anyUpdate = true;
                    } catch (NumberFormatException e) {
                        errors.add("Invalid format for scholarship " + scholarshipId + ": " + e.getMessage());
                    }
                }
            }

            if (!errors.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", String.join("; ", errors));
            }
            if (anyUpdate) {
                redirectAttributes.addFlashAttribute("successMessage", "Scholarship amounts updated successfully!");
            } else if (errors.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "No scholarship amounts were updated.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update scholarship amounts: " + e.getMessage());
        }
        session.setAttribute("scholarshipAdmissionYear", admissionYear);
        return "redirect:/admin-home/scholarship-by-year-list";
    }

    @PostMapping("/update-scholarship-by-year-without")
    @Transactional
    public String updateScholarshipByYearWithout(
            @RequestParam("admissionYear") Integer admissionYear,
            @RequestParam Map<String, String> params,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        try {
            int currentYear = LocalDate.now().getYear();
            if (admissionYear == null || admissionYear < currentYear) {
                redirectAttributes.addFlashAttribute("errorMessage", "Cannot update scholarships for past years or invalid year.");
                return "redirect:/admin-home/scholarship-by-year-list";
            }

            Admins admin = adminsService.getAdmin();
            if (admin == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Admin not found.");
                return "redirect:/admin-home/scholarship-by-year-list";
            }

            List<String> errors = new ArrayList<>();
            boolean anyUpdate = false;

            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (entry.getKey().startsWith("amount_")) {
                    String scholarshipId = entry.getKey().substring("amount_".length());
                    String amountValue = entry.getValue();
                    String discountValue = params.get("discountPercentage_" + scholarshipId);

                    try {
                        Double amount = amountValue.isEmpty() ? null : Double.parseDouble(amountValue);
                        Double discountPercentage = discountValue != null && !discountValue.isEmpty() ?
                                Double.parseDouble(discountValue) : null;

                        if (amount == null && discountPercentage == null) {
                            continue; // Bỏ qua nếu cả hai đều rỗng
                        }
                        if (amount != null && amount < 0) {
                            errors.add("Amount for scholarship " + scholarshipId + " cannot be negative.");
                            continue;
                        }
                        if (discountPercentage != null && (discountPercentage < 0 || discountPercentage > 100)) {
                            errors.add("Discount percentage for scholarship " + scholarshipId + " must be between 0 and 100.");
                            continue;
                        }

                        Scholarships scholarship = scholarshipsService.getScholarshipById(scholarshipId);
                        if (scholarship == null) {
                            errors.add("Scholarship not found for ID " + scholarshipId);
                            continue;
                        }

                        ScholarshipByYear newScholarshipByYear = new ScholarshipByYear(
                                scholarship,
                                admissionYear,
                                amount != null ? amount : 0.0,
                                discountPercentage,
                                admin,
                                ActivityStatus.ACTIVATED
                        );
                        newScholarshipByYear.setContractStatus(ContractStatus.DRAFT);
                        scholarshipByYearService.createScholarshipByYear(newScholarshipByYear);
                        anyUpdate = true;
                    } catch (NumberFormatException e) {
                        errors.add("Invalid format for scholarship " + scholarshipId + ": " + e.getMessage());
                    }
                }
            }

            if (!errors.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", String.join("; ", errors));
            }
            if (anyUpdate) {
                redirectAttributes.addFlashAttribute("successMessage", "Scholarship amounts created successfully!");
            } else if (errors.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "No scholarship amounts were created.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create scholarship amounts: " + e.getMessage());
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