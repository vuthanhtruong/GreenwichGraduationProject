// src/main/java/com/example/demo/majorLecturers_Specializations/controller/StaffLecturerSpecializationController.java
package com.example.demo.majorLecturers_Specializations.controller;

import com.example.demo.majorLecturers_Specializations.model.MajorLecturers_Specializations;
import com.example.demo.majorLecturers_Specializations.service.MajorLecturersSpecializationsService;
import com.example.demo.specialization.model.Specialization;
import com.example.demo.specialization.service.SpecializationService;
import com.example.demo.user.majorLecturer.model.MajorLecturers;
import com.example.demo.user.majorLecturer.service.MajorLecturersService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/staff-specializations/lecturer")
@PreAuthorize("hasRole('STAFF')")
public class StaffLecturerSpecializationController {

    private final MajorLecturersService lecturersService;
    private final MajorLecturersSpecializationsService majorLecturersSpecializationsService;

    public StaffLecturerSpecializationController(
            MajorLecturersService lecturersService, MajorLecturersSpecializationsService majorLecturersSpecializationsService) {
        this.lecturersService = lecturersService;
        this.majorLecturersSpecializationsService = majorLecturersSpecializationsService;
    }

    @PostMapping
    public String showByLecturerPost(@RequestParam String lecturerId, HttpSession session) {
        session.setAttribute("view_lecturerId", lecturerId);
        return "redirect:/staff-specializations/lecturer";
    }

    @GetMapping
    public String showByLecturerGet(Model model, HttpSession session) {
        String lecturerId = (String) session.getAttribute("view_lecturerId");
        if (lecturerId == null) {
            return "redirect:/staff-home/lecturers-list";
        }

        MajorLecturers lecturer = lecturersService.getLecturerById(lecturerId);
        if (lecturer == null) {
            return prepareEmpty(model, "Lecturer not found.", "alert-danger");
        }

        List<MajorLecturers_Specializations> specializations = majorLecturersSpecializationsService.getSpecializationsByLecturer(lecturer);

        model.addAttribute("specializations", specializations);
        model.addAttribute("lecturer", lecturer);
        model.addAttribute("pageTitle", "Specializations of: " + lecturer.getFullName());
        model.addAttribute("isLecturerView", true);
        model.addAttribute("backUrl", "/staff-home/lecturers-list");
        model.addAttribute("isOwnView", "/staff-home");

        if (specializations.isEmpty()) {
            model.addAttribute("message", "This lecturer has no assigned specializations.");
            model.addAttribute("alertClass", "alert-warning");
        }

        return "YourSpecializations";
    }

    private String prepareEmpty(Model model, String message, String alertClass) {
        model.addAttribute("specializations", new ArrayList<>());
        model.addAttribute("message", message);
        model.addAttribute("alertClass", alertClass);
        model.addAttribute("backUrl", "/staff-home/lecturers-list");
        return "YourSpecializations";
    }
}