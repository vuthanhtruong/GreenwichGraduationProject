package com.example.demo.TuitionByYear.controller;

import com.example.demo.TuitionByYear.model.TuitionByYear;
import com.example.demo.TuitionByYear.service.TuitionByYearService;
import com.example.demo.campus.model.Campuses;
import com.example.demo.campus.service.CampusesService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/admin-home/subjects-list/reference")
@PreAuthorize("hasRole('ADMIN')")
@Transactional
public class TuitionController {

    private final TuitionByYearService tuitionByYearService;
    private final CampusesService campusesService;

    public TuitionController(TuitionByYearService tuitionByYearService, CampusesService campusesService) {
        this.tuitionByYearService = tuitionByYearService;
        this.campusesService = campusesService;
    }

    @GetMapping
    public String showTuitionReferencePage(Model model, HttpSession session) {
        List<Campuses> campuses = campusesService.getCampuses();
        List<Integer> admissionYears = tuitionByYearService.findAllAdmissionYears();

        model.addAttribute("Campuses", campuses);
        model.addAttribute("admissionYears", admissionYears);

        // Retrieve campusId and admissionYear from session if available
        String campusId = (String) session.getAttribute("selectedCampusId");
        Integer selectedYear = (Integer) session.getAttribute("selectedYear");

        if (campusId != null && selectedYear != null) {
            Campuses referenceCampus = campusesService.getCampusById(campusId);
            List<TuitionByYear> referenceTuitions = tuitionByYearService.tuitionFeesByCampus(campusId, selectedYear);
            model.addAttribute("referenceCampus", referenceCampus);
            model.addAttribute("referenceTuitions", referenceTuitions);
            model.addAttribute("selectedYear", selectedYear);
        }

        return "TuitionFeesByCampus";
    }

    @PostMapping
    public String filterTuitionFees(@RequestParam("campusId") String campusId,
                                    @RequestParam("admissionYear") Integer admissionYear,
                                    RedirectAttributes redirectAttributes,
                                    HttpSession session) {
        try {
            // Validate inputs
            if (campusId == null || campusId.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Please select a valid campus.");
                return "redirect:/admin-home/subjects-list/reference";
            }
            if (admissionYear == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Please select a valid admission year.");
                return "redirect:/admin-home/subjects-list/reference";
            }

            // Store selected values in session
            session.setAttribute("selectedCampusId", campusId);
            session.setAttribute("selectedYear", admissionYear);

            // Fetch data
            Campuses referenceCampus = campusesService.getCampusById(campusId);
            if (referenceCampus == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Selected campus not found.");
                return "redirect:/admin-home/subjects-list/reference";
            }

            List<TuitionByYear> referenceTuitions = tuitionByYearService.tuitionFeesByCampus(campusId, admissionYear);
            if (referenceTuitions.isEmpty()) {
                redirectAttributes.addFlashAttribute("warningMessage", "No tuition fees found for the selected campus and year.");
            }

            // Add data to model for rendering
            redirectAttributes.addFlashAttribute("referenceCampus", referenceCampus);
            redirectAttributes.addFlashAttribute("referenceTuitions", referenceTuitions);
            redirectAttributes.addFlashAttribute("selectedYear", admissionYear);

            return "redirect:/admin-home/subjects-list/reference";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin-home/subjects-list/reference";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while fetching tuition fees.");
            return "redirect:/admin-home/subjects-list/reference";
        }
    }
}