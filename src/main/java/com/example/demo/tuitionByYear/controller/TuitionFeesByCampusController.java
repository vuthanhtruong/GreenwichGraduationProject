package com.example.demo.tuitionByYear.controller;

import com.example.demo.tuitionByYear.model.TuitionByYear;
import com.example.demo.tuitionByYear.service.TuitionByYearService;
import com.example.demo.user.admin.service.AdminsService;
import com.example.demo.campus.model.Campuses;
import com.example.demo.campus.service.CampusesService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin-home/tuition-management/reference")
@PreAuthorize("hasRole('ADMIN')")
@Transactional
public class TuitionFeesByCampusController {

    private final TuitionByYearService tuitionByYearService;
    private final CampusesService campusesService;
    private final AdminsService adminsService;

    public TuitionFeesByCampusController(TuitionByYearService tuitionByYearService, CampusesService campusesService, AdminsService adminsService) {
        this.tuitionByYearService = tuitionByYearService;
        this.campusesService = campusesService;
        this.adminsService = adminsService;
    }

    @PostMapping
    public String filterTuitionFees(@RequestParam("campusId") String campusId,
                                    @RequestParam("admissionYear") Integer admissionYear,
                                    Model model) {
        try {
            // Validate inputs
            if (campusId == null || campusId.trim().isEmpty()) {
                model.addAttribute("errorMessage", "Please select a valid campus.");
                // Reload necessary data for the view
                model.addAttribute("Campuses", campusesService.listOfExceptionFieldsCampus());
                model.addAttribute("admissionYears", tuitionByYearService.findAllAdmissionYears());
                return "TuitionFeesByCampus";
            }
            if (admissionYear == null) {
                model.addAttribute("errorMessage", "Please select a valid admission year.");
                // Reload necessary data for the view
                model.addAttribute("Campuses", campusesService.listOfExceptionFieldsCampus());
                model.addAttribute("admissionYears", tuitionByYearService.findAllAdmissionYears());
                return "TuitionFeesByCampus";
            }
            // Fetch data
            Campuses referenceCampus = campusesService.getCampusById(campusId);
            if (referenceCampus == null) {
                model.addAttribute("errorMessage", "Selected campus not found.");
                // Reload necessary data for the view
                model.addAttribute("Campuses", campusesService.listOfExceptionFieldsCampus());
                model.addAttribute("admissionYears", tuitionByYearService.findAllAdmissionYears());
                return "TuitionFeesByCampus";
            }

            List<TuitionByYear> referenceTuitions = tuitionByYearService.tuitionFeesByCampus(campusId, admissionYear);
            if (referenceTuitions.isEmpty()) {
                model.addAttribute("warningMessage", "No tuition fees found for the selected campus and year.");
            }

            // Add data to model for rendering
            model.addAttribute("referenceCampus", referenceCampus);
            model.addAttribute("referenceTuitions", referenceTuitions);
            model.addAttribute("selectedYear", admissionYear);
            model.addAttribute("Campuses", campusesService.listOfExceptionFieldsCampus());
            model.addAttribute("admissionYears", tuitionByYearService.findAllAdmissionYears());
            model.addAttribute("mycampus", adminsService.getAdminCampus());

            return "TuitionFeesByCampus";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            // Reload necessary data for the view
            model.addAttribute("Campuses", campusesService.listOfExceptionFieldsCampus());
            model.addAttribute("admissionYears", tuitionByYearService.findAllAdmissionYears());
            return "TuitionFeesByCampus";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "An error occurred while fetching tuition fees.");
            // Reload necessary data for the view
            model.addAttribute("Campuses", campusesService.listOfExceptionFieldsCampus());
            model.addAttribute("admissionYears", tuitionByYearService.findAllAdmissionYears());
            return "TuitionFeesByCampus";
        }
    }
}