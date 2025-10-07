package com.example.demo.MajorLecturers_Specializations.controller;

import com.example.demo.MajorLecturers_Specializations.model.MajorLecturers_Specializations;
import com.example.demo.Specialization.model.Specialization;
import com.example.demo.MajorLecturers_Specializations.service.MajorLecturersSpecializationsService;
import com.example.demo.Specialization.service.SpecializationService;
import com.example.demo.lecturer.model.MajorLecturers;
import com.example.demo.lecturer.service.LecturesService;
import com.example.demo.staff.model.Staffs;
import com.example.demo.staff.service.StaffsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/staff-home/lecturer-specialization")
@PreAuthorize("hasRole('STAFF')")
public class AssignLecturerToSpecializationController {

    private final SpecializationService specializationService;
    private final LecturesService lecturesService;
    private final StaffsService staffsService;
    private final MajorLecturersSpecializationsService majorLecturersSpecializationsService;

    public AssignLecturerToSpecializationController(SpecializationService specializationService,
                                                    LecturesService lecturesService,
                                                    StaffsService staffsService,
                                                    MajorLecturersSpecializationsService majorLecturersSpecializationsService) {
        this.specializationService = specializationService;
        this.lecturesService = lecturesService;
        this.staffsService = staffsService;
        this.majorLecturersSpecializationsService = majorLecturersSpecializationsService;
    }

    @PostMapping("/assign-lecturer")
    public String assignLecturerPost(
            @RequestParam("specializationId") String specializationId,
            HttpSession session) {
        if (specializationId == null || specializationId.trim().isEmpty()) {
            return "redirect:/staff-home/lecturer-specialization";
        }
        session.setAttribute("currentSpecializationId", specializationId);
        return "redirect:/staff-home/lecturer-specialization/assign-lecturer";
    }

    @GetMapping("/assign-lecturer")
    public String assignLecturerGet(HttpSession session, Model model) {
        String specializationId = (String) session.getAttribute("currentSpecializationId");

        if (specializationId == null || specializationId.trim().isEmpty()) {
            model.addAttribute("errorMessage", "Specialization ID is required. Please select a specialization first.");
            return "redirect:/staff-home/lecturer-specialization";
        }

        Specialization specialization = specializationService.getSpecializationById(specializationId);
        if (specialization == null) {
            model.addAttribute("errorMessage", "Specialization not found.");
            session.removeAttribute("currentSpecializationId");
            return "redirect:/staff-home/lecturer-specialization";
        }

        model.addAttribute("specialization", specialization);
        model.addAttribute("lecturersNotAssigned", majorLecturersSpecializationsService.getLecturersNotAssignedToSpecialization(specialization));
        model.addAttribute("lecturersAssigned", majorLecturersSpecializationsService.getLecturersAssignedToSpecialization(specialization));
        return "AssignLecturerToSpecialization";
    }

    @PostMapping("/assign-lecturer/add")
    public String addSelectedLecturers(
            @RequestParam("specializationId") String specializationId,
            @RequestParam("lecturerIds") List<String> lecturerIds,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (specializationId == null || specializationId.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid specialization ID.");
            return "redirect:/staff-home/lecturer-specialization/assign-lecturer";
        }

        Specialization specialization = specializationService.getSpecializationById(specializationId);
        if (specialization == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Specialization not found.");
            return "redirect:/staff-home/lecturer-specialization/assign-lecturer";
        }

        Staffs currentStaff = staffsService.getStaff();
        if (currentStaff == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Staff information not found.");
            return "redirect:/staff-home/lecturer-specialization/assign-lecturer";
        }

        session.setAttribute("currentSpecializationId", specializationId);

        int addedCount = 0;
        for (String lecturerId : lecturerIds) {
            if (lecturerId == null || lecturerId.trim().isEmpty()) continue;
            MajorLecturers lecturer = lecturesService.getLecturerById(lecturerId);
            if (lecturer != null && !majorLecturersSpecializationsService.isLecturerAlreadyAssignedToSpecialization(lecturerId, specializationId)) {
                MajorLecturers_Specializations assignment = new MajorLecturers_Specializations(lecturer, specialization);
                majorLecturersSpecializationsService.addLecturerSpecialization(assignment);
                addedCount++;
            }
        }

        if (addedCount > 0) {
            redirectAttributes.addFlashAttribute("successMessage", addedCount + " lecturer(s) assigned successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "No new lecturers were assigned. Check if they are already assigned.");
        }

        return "redirect:/staff-home/lecturer-specialization/assign-lecturer";
    }

    @PostMapping("/assign-lecturer/remove")
    public String removeSelectedLecturers(
            @RequestParam("specializationId") String specializationId,
            @RequestParam("lecturerIds") List<String> lecturerIds,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (specializationId == null || specializationId.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid specialization ID.");
            return "redirect:/staff-home/lecturer-specialization/assign-lecturer";
        }

        session.setAttribute("currentSpecializationId", specializationId);

        int removedCount = 0;
        for (String lecturerId : lecturerIds) {
            if (lecturerId == null || lecturerId.trim().isEmpty()) continue;
            if (majorLecturersSpecializationsService.removeLecturerSpecialization(lecturerId, specializationId)) {
                removedCount++;
            }
        }

        if (removedCount > 0) {
            redirectAttributes.addFlashAttribute("successMessage", removedCount + " lecturer(s) removed successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "No lecturers were removed.");
        }

        return "redirect:/staff-home/lecturer-specialization/assign-lecturer";
    }
}