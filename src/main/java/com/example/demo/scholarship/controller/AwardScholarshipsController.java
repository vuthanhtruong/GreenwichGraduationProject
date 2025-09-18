package com.example.demo.scholarship.controller;

import com.example.demo.scholarship.model.Scholarships;
import com.example.demo.scholarship.model.Students_Scholarships;
import com.example.demo.scholarship.service.ScholarshipByYearService;
import com.example.demo.scholarship.service.ScholarshipsService;
import com.example.demo.scholarship.service.StudentScholarshipService;
import com.example.demo.student.service.StudentsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/staff-home/award-scholarships")
@PreAuthorize("hasRole('STAFF')")
public class AwardScholarshipsController {

    private final ScholarshipsService scholarshipsService;
    private final ScholarshipByYearService scholarshipByYearService;
    private final StudentScholarshipService studentScholarshipService;

    @Autowired
    public AwardScholarshipsController(ScholarshipsService scholarshipsService,
                                       ScholarshipByYearService scholarshipByYearService,
                                       StudentScholarshipService studentScholarshipService) {
        this.scholarshipsService = scholarshipsService;
        this.scholarshipByYearService = scholarshipByYearService;
        this.studentScholarshipService = studentScholarshipService;
    }

    @GetMapping("")
    public String showAwardScholarships(Model model, HttpSession session) {
        Integer admissionYear = (Integer) session.getAttribute("awardAdmissionYear");
        model.addAttribute("studentsScholarship", new Students_Scholarships());
        return listAwardedScholarships(model, admissionYear, session);
    }

    @PostMapping("")
    public String listAwardedScholarships(Model model,
                                          @RequestParam(value = "admissionYear", required = false) Integer admissionYear,
                                          HttpSession session) {
        try {
            if (admissionYear != null) {
                session.setAttribute("awardAdmissionYear", admissionYear);
            }

            List<Integer> admissionYears = scholarshipByYearService.getAllAdmissionYears();
            int currentYear = LocalDate.now().getYear();
            List<Integer> finalAdmissionYears = admissionYears;
            List<Integer> futureYears = IntStream.rangeClosed(currentYear, currentYear + 5)
                    .boxed()
                    .filter(year -> !finalAdmissionYears.contains(year))
                    .collect(Collectors.toList());

            admissionYears.addAll(futureYears);
            admissionYears = admissionYears.stream()
                    .sorted(Comparator.reverseOrder())
                    .collect(Collectors.toList());

            Integer selectedYear = admissionYear != null ? admissionYear : currentYear;
            List<Scholarships> availableScholarships = scholarshipsService.getAllScholarships();
            List<Students_Scholarships> awardedScholarships =
                    studentScholarshipService.getAwardedScholarshipsByYear(admissionYear);

            model.addAttribute("admissionYears", admissionYears);
            model.addAttribute("selectedYear", selectedYear);
            model.addAttribute("availableScholarships", availableScholarships);
            model.addAttribute("awardedScholarships", awardedScholarships);

            return "AwardScholarships";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Failed to load awarded scholarships: " + e.getMessage());
            model.addAttribute("admissionYears", List.of());
            model.addAttribute("selectedYear", LocalDate.now().getYear());
            model.addAttribute("availableScholarships", List.of());
            model.addAttribute("awardedScholarships", List.of());
            return "AwardScholarships";
        }
    }

    @PostMapping("/assign")
    public String assignScholarship(
            @RequestParam("studentId") String studentId,
            @RequestParam("scholarshipId") String scholarshipId,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        Integer selectedYear = (Integer) session.getAttribute("awardAdmissionYear");
        if (selectedYear == null) {
            selectedYear = LocalDate.now().getYear();
        }

        List<String> errors = studentScholarshipService.validateScholarshipAward(studentId, scholarshipId, selectedYear);
        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            return listAwardedScholarships(model, selectedYear, session);
        }

        try {
            studentScholarshipService.assignScholarship(studentId, scholarshipId, selectedYear);
            redirectAttributes.addFlashAttribute("successMessage", "Scholarship awarded successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to award scholarship: " + e.getMessage());
            return listAwardedScholarships(model, selectedYear, session);
        }

        return "redirect:/staff-home/award-scholarships";
    }

}
