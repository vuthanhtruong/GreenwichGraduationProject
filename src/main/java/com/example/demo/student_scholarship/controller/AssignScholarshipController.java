package com.example.demo.student_scholarship.controller;

import com.example.demo.student_scholarship.service.StudentScholarshipService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/staff-home/award-scholarships")
@PreAuthorize("hasRole('STAFF')")
public class AssignScholarshipController {

    private final StudentScholarshipService studentScholarshipService;

    @Autowired
    public AssignScholarshipController(StudentScholarshipService studentScholarshipService) {
        this.studentScholarshipService = studentScholarshipService;
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
            model.addAttribute("studentId", studentId);
            model.addAttribute("scholarshipId", scholarshipId);
            model.addAttribute("selectedYear", selectedYear);
            model.addAttribute("awardedScholarships", studentScholarshipService.getAwardedScholarshipsByYear(selectedYear));
            return "AwardScholarships";
        }

        try {
            studentScholarshipService.assignScholarship(studentId, scholarshipId, selectedYear);
            redirectAttributes.addFlashAttribute("successMessage", "Scholarship awarded successfully!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Failed to award scholarship: " + e.getMessage());
            model.addAttribute("studentId", studentId);
            model.addAttribute("scholarshipId", scholarshipId);
            model.addAttribute("selectedYear", selectedYear);
            model.addAttribute("awardedScholarships", studentScholarshipService.getAwardedScholarshipsByYear(selectedYear));
            return "AwardScholarships";
        }
        return "redirect:/staff-home/award-scholarships";
    }
}