package com.example.demo.tuitionByYear.controller;

import com.example.demo.tuitionByYear.model.TuitionByYear;
import com.example.demo.tuitionByYear.service.TuitionByYearService;
import com.example.demo.campus.service.CampusesService;
import com.example.demo.scholarship.model.Scholarships;
import com.example.demo.scholarship.service.ScholarshipsService;
import com.example.demo.scholarshipByYear.model.ScholarshipByYear;
import com.example.demo.scholarshipByYear.service.ScholarshipByYearService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/admin-home/contracts-list")
@PreAuthorize("hasRole('ADMIN')")
public class ContractsListController {

    private final TuitionByYearService tuitionService;
    private final ScholarshipsService scholarshipsService;
    private final ScholarshipByYearService scholarshipByYearService;
    private final CampusesService campusService;

    public ContractsListController(TuitionByYearService tuitionService,
                                   ScholarshipsService scholarshipsService,
                                   ScholarshipByYearService scholarshipByYearService,
                                   CampusesService campusService) {
        this.tuitionService = tuitionService;
        this.scholarshipsService = scholarshipsService;
        this.scholarshipByYearService = scholarshipByYearService;
        this.campusService = campusService;
    }

    @GetMapping
    public String showContractsList(Model model, HttpSession session) {
        Integer admissionYear = (Integer) session.getAttribute("admissionYear");
        return listContracts(model, admissionYear, session);
    }

    @PostMapping
    public String listContracts(Model model,
                                @RequestParam(value = "admissionYear", required = false) Integer admissionYear,
                                HttpSession session) {
        if (admissionYear != null) {
            session.setAttribute("admissionYear", admissionYear);
        }

        // Lấy tất cả admission years
        List<Integer> admissionYearsFromTuition = tuitionService.findAllAdmissionYears();
        List<Integer> admissionYearsFromScholarships = scholarshipByYearService.getAllAdmissionYears();
        List<Integer> admissionYears = admissionYearsFromTuition.stream()
                .filter(admissionYearsFromScholarships::contains)
                .collect(Collectors.toList());

        int currentYear = LocalDate.now().getYear();
        List<Integer> finalAdmissionYears = admissionYears;
        List<Integer> futureYears = IntStream.rangeClosed(currentYear, currentYear + 5)
                .boxed()
                .filter(year -> !finalAdmissionYears.contains(year))
                .toList();
        admissionYears.addAll(futureYears);
        admissionYears = admissionYears.stream()
                .sorted(Comparator.reverseOrder())
                .toList();

        Integer selectedYear = admissionYear != null ? admissionYear : currentYear;

        // Lấy danh sách TuitionByYear với tuition > 0
        List<TuitionByYear> tuitionsWithFee = tuitionService.getTuitionsWithFeeByYear(selectedYear);
        List<TuitionByYear> tuitionsWithReStudyFee = tuitionService.getTuitionsWithReStudyFeeByYear(selectedYear);
        List<TuitionByYear> tuitionsWithoutReStudyFee = tuitionService.getTuitionsWithoutReStudyFeeByYear(selectedYear);

        // Lấy danh sách học bổng
        List<Scholarships> allScholarships = scholarshipsService.getAllScholarships();
        List<ScholarshipByYear> scholarshipByYears = scholarshipByYearService.getScholarshipsByYear(selectedYear);

        Map<String, ScholarshipByYear> scholarshipByYearMap = scholarshipByYears.stream()
                .collect(Collectors.toMap(
                        s -> s.getId().getScholarshipId(),
                        s -> s,
                        (existing, replacement) -> existing
                ));

        model.addAttribute("admissionYears", admissionYears);
        model.addAttribute("selectedYear", selectedYear);
        model.addAttribute("tuitionsWithFee", tuitionsWithFee);
        model.addAttribute("tuitionsWithReStudyFee", tuitionsWithReStudyFee);
        model.addAttribute("tuitionsWithoutReStudyFee", tuitionsWithoutReStudyFee);
        model.addAttribute("allScholarships", allScholarships);
        model.addAttribute("scholarshipByYearMap", scholarshipByYearMap);
        model.addAttribute("Campuses", campusService.getCampuses());

        return "AdminContractsList";
    }

    @PostMapping("/finalize-contracts")
    public String finalizeContracts(@RequestParam("admissionYear") Integer admissionYear,
                                    RedirectAttributes redirectAttributes,
                                    HttpSession session) {
        try {
            int currentYear = LocalDate.now().getYear();
            if (admissionYear == null || admissionYear < currentYear) {
                redirectAttributes.addFlashAttribute("errorMessage", "Cannot finalize contracts for past years or invalid year.");
                return "redirect:/admin-home/contracts-list";
            }

            // Kiểm tra dữ liệu học phí
            List<TuitionByYear> tuitions = tuitionService.getTuitionsWithFeeByYear(admissionYear);
            if (tuitions.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "No tuition fees found for the selected year.");
                return "redirect:/admin-home/contracts-list";
            }

            List<String> tuitionErrors = new ArrayList<>();
            for (TuitionByYear tuition : tuitions) {
                if (tuition.getTuition() == null || tuition.getTuition() <= 0) {
                    tuitionErrors.add("Tuition fee for subject " + tuition.getSubject().getSubjectId() + " is missing or invalid.");
                }
                if (tuition.getReStudyTuition() == null || tuition.getReStudyTuition() <= 0) {
                    tuitionErrors.add("Re-study fee for subject " + tuition.getSubject().getSubjectId() + " is missing or invalid.");
                }
            }

            // Kiểm tra dữ liệu học bổng
            List<ScholarshipByYear> scholarshipByYears = scholarshipByYearService.getScholarshipsByYear(admissionYear);
            List<String> scholarshipErrors = new ArrayList<>();
            for (ScholarshipByYear scholarship : scholarshipByYears) {
                if (scholarship.getAmount() == null || scholarship.getAmount() <= 0) {
                    scholarshipErrors.add("Amount for scholarship " + scholarship.getId().getScholarshipId() + " is missing or invalid.");
                }
                if (scholarship.getDiscountPercentage() == null || scholarship.getDiscountPercentage() <= 0) {
                    scholarshipErrors.add("Discount percentage for scholarship " + scholarship.getId().getScholarshipId() + " is missing or invalid.");
                }
            }

            // Kết hợp lỗi và kiểm tra
            List<String> allErrors = new ArrayList<>();
            allErrors.addAll(tuitionErrors);
            allErrors.addAll(scholarshipErrors);

            if (!allErrors.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Cannot finalize contracts. Please ensure all tuition and scholarship records have valid fees: " + String.join("; ", allErrors));
                return "redirect:/admin-home/contracts-list";
            }

            // Chốt hợp đồng cho học phí
            tuitionService.finalizeContracts(admissionYear);

            // Chốt hợp đồng cho học bổng
            scholarshipByYearService.finalizeScholarshipContracts(admissionYear);

            redirectAttributes.addFlashAttribute("successMessage", "Contracts finalized successfully for " + admissionYear);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error finalizing contracts: " + e.getMessage());
        }
        session.setAttribute("admissionYear", admissionYear);
        return "redirect:/admin-home/contracts-list";
    }
}