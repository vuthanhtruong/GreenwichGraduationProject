// LecturerSpecializationController.java
package com.example.demo.majorLecturers_Specializations.controller;

import com.example.demo.Specialization.model.Specialization;
import com.example.demo.Specialization.service.SpecializationService;
import com.example.demo.staff.service.StaffsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/staff-home/lecturer-specialization")
@PreAuthorize("hasRole('STAFF')")
public class LecturerSpecializationController {

    private final SpecializationService specializationService;
    private final StaffsService staffsService;

    @Autowired
    public LecturerSpecializationController(SpecializationService specializationService, StaffsService staffsService) {
        this.specializationService = specializationService;
        this.staffsService = staffsService;
    }

    @GetMapping("")
    public String showLecturerSpecialization(Model model, HttpSession session) {
        try {
            var major = staffsService.getStaffMajor();
            if (major == null) {
                model.addAttribute("message", "No major found for this staff.");
                model.addAttribute("alertClass", "alert-warning");
                model.addAttribute("specializations", new ArrayList<>());
                return "LecturerSpecialization";
            }

            List<Specialization> specializations = specializationService.specializationsByMajor(major);
            model.addAttribute("specializations", specializations);
            model.addAttribute("major", major.getMajorName());
            model.addAttribute("staff", staffsService.getStaff());
            if (specializations.isEmpty()) {
                model.addAttribute("message", "No specializations found for this major.");
                model.addAttribute("alertClass", "alert-warning");
            }
            return "LecturerSpecialization";
        } catch (Exception e) {
            model.addAttribute("message", "An error occurred: " + e.getMessage());
            model.addAttribute("alertClass", "alert-danger");
            model.addAttribute("specializations", new ArrayList<>());
            return "LecturerSpecialization";
        }
    }
}