package com.example.demo.majorLecturers_Specializations.controller;

import com.example.demo.majorLecturers_Specializations.model.MajorLecturersSpecializationsId;
import com.example.demo.majorLecturers_Specializations.model.MajorLecturers_Specializations;
import com.example.demo.specialization.model.Specialization;
import com.example.demo.majorLecturers_Specializations.service.MajorLecturersSpecializationsService;
import com.example.demo.specialization.service.SpecializationService;
import com.example.demo.user.majorLecturer.model.MajorLecturers;
import com.example.demo.user.majorLecturer.service.MajorLecturersService;
import com.example.demo.user.staff.model.Staffs;
import com.example.demo.user.staff.service.StaffsService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/staff-home/lecturer-specialization")
@PreAuthorize("hasRole('STAFF')")
public class AssignLecturerToSpecializationController {

    private static final Logger logger = LoggerFactory.getLogger(AssignLecturerToSpecializationController.class);

    private final SpecializationService specializationService;
    private final MajorLecturersService lecturesService;
    private final StaffsService staffsService;
    private final MajorLecturersSpecializationsService majorLecturersSpecializationsService;

    @Autowired
    public AssignLecturerToSpecializationController(
            SpecializationService specializationService,
            MajorLecturersService lecturesService,
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
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (specializationId == null || specializationId.trim().isEmpty()) {
            logger.warn("Invalid specialization ID in POST /assign-lecturer");
            redirectAttributes.addFlashAttribute("errorMessage", "Specialization ID is required.");
            return "redirect:/staff-home/lecturer-specialization";
        }
        session.setAttribute("currentSpecializationId", specializationId);
        logger.info("Set specializationId {} in session", specializationId);
        return "redirect:/staff-home/lecturer-specialization/assign-lecturer";
    }

    @GetMapping("/assign-lecturer")
    public String assignLecturerGet(HttpSession session, Model model) {
        String specializationId = (String) session.getAttribute("currentSpecializationId");

        if (specializationId == null || specializationId.trim().isEmpty()) {
            logger.warn("No specialization ID found in session for GET /assign-lecturer");
            model.addAttribute("errorMessage", "Specialization ID is required. Please select a specialization first.");
            return "redirect:/staff-home/lecturer-specialization";
        }

        Specialization specialization = specializationService.getSpecializationById(specializationId);
        if (specialization == null) {
            logger.warn("Specialization not found for ID: {}", specializationId);
            model.addAttribute("errorMessage", "Specialization not found.");
            session.removeAttribute("currentSpecializationId");
            return "redirect:/staff-home/lecturer-specialization";
        }

        model.addAttribute("specialization", specialization);
        model.addAttribute("lecturersNotAssigned", majorLecturersSpecializationsService.getLecturersNotAssignedToSpecialization(specialization));
        model.addAttribute("lecturersAssigned", majorLecturersSpecializationsService.getLecturersAssignedToSpecialization(specialization));
        logger.info("Loaded assign-lecturer page for specialization: {}", specializationId);
        return "AssignLecturerToSpecialization";
    }

    @PostMapping("/assign-lecturer/add")
    public String addSelectedLecturers(
            @RequestParam("specializationId") String specializationId,
            @RequestParam(value = "lecturerIds", required = false) List<String> lecturerIds,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (specializationId == null || specializationId.trim().isEmpty()) {
            logger.warn("Invalid specialization ID in POST /assign-lecturer/add");
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid specialization ID.");
            return "redirect:/staff-home/lecturer-specialization/assign-lecturer";
        }

        Specialization specialization = specializationService.getSpecializationById(specializationId);
        if (specialization == null) {
            logger.warn("Specialization not found for ID: {}", specializationId);
            redirectAttributes.addFlashAttribute("errorMessage", "Specialization not found.");
            return "redirect:/staff-home/lecturer-specialization/assign-lecturer";
        }

        Staffs currentStaff = staffsService.getStaff();
        if (currentStaff == null) {
            logger.warn("Staff information not found in POST /assign-lecturer/add");
            redirectAttributes.addFlashAttribute("errorMessage", "Staff information not found.");
            return "redirect:/staff-home/lecturer-specialization/assign-lecturer";
        }

        if (lecturerIds == null || lecturerIds.isEmpty()) {
            logger.warn("No lecturers selected for assignment to specialization {}", specializationId);
            redirectAttributes.addFlashAttribute("errorMessage", "No lecturers selected for assignment.");
            return "redirect:/staff-home/lecturer-specialization/assign-lecturer";
        }

        session.setAttribute("currentSpecializationId", specializationId);

        int addedCount = 0;
        for (String lecturerId : lecturerIds) {
            if (lecturerId == null || lecturerId.trim().isEmpty()) {
                logger.warn("Invalid lecturer ID: {}", lecturerId);
                continue;
            }
            MajorLecturers lecturer = lecturesService.getLecturerById(lecturerId);
            if (lecturer == null) {
                logger.warn("Lecturer not found for ID: {}", lecturerId);
                continue;
            }
            try {
                if (!majorLecturersSpecializationsService.isLecturerAlreadyAssignedToSpecialization(lecturerId, specializationId)) {
                    MajorLecturersSpecializationsId assignmentId = new MajorLecturersSpecializationsId(lecturerId, specializationId);
                    MajorLecturers_Specializations assignment = new MajorLecturers_Specializations();
                    assignment.setId(assignmentId);
                    assignment.setMajorLecturer(lecturer);
                    assignment.setSpecialization(specialization);
                    assignment.setCreatedAt(LocalDateTime.now());
                    majorLecturersSpecializationsService.addLecturerSpecialization(assignment);
                    addedCount++;
                    logger.info("Assigned lecturer {} to specialization {}", lecturerId, specializationId);
                } else {
                    logger.warn("Lecturer {} already assigned to specialization {}", lecturerId, specializationId);
                }
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid data for assigning lecturer {} to specialization {}: {}", lecturerId, specializationId, e.getMessage());
                redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            } catch (IllegalStateException e) {
                logger.warn("Duplicate assignment attempt for lecturer {} to specialization {}: {}", lecturerId, specializationId, e.getMessage());
                redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            } catch (Exception e) {
                logger.error("Unexpected error assigning lecturer {} to specialization {}: {}", lecturerId, specializationId, e.getMessage(), e);
                if (e.getMessage().contains("Duplicate entry")) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Specialization " + specializationId + " already exists in the database.");
                } else {
                    redirectAttributes.addFlashAttribute("errorMessage", "Error assigning lecturer: " + e.getMessage());
                }
            }
        }

        if (addedCount > 0) {
            redirectAttributes.addFlashAttribute("successMessage", addedCount + " lecturer(s) assigned successfully.");
        } else {
            String errorMessage = redirectAttributes.getFlashAttributes().containsKey("errorMessage")
                    ? (String) redirectAttributes.getFlashAttributes().get("errorMessage")
                    : "No new lecturers were assigned. Check if they are already assigned or invalid.";
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }

        return "redirect:/staff-home/lecturer-specialization/assign-lecturer";
    }

    @PostMapping("/assign-lecturer/remove")
    public String removeSelectedLecturers(
            @RequestParam("specializationId") String specializationId,
            @RequestParam(value = "lecturerIds", required = false) List<String> lecturerIds,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (specializationId == null || specializationId.trim().isEmpty()) {
            logger.warn("Invalid specialization ID in POST /assign-lecturer/remove");
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid specialization ID.");
            return "redirect:/staff-home/lecturer-specialization/assign-lecturer";
        }

        if (lecturerIds == null || lecturerIds.isEmpty()) {
            logger.warn("No lecturers selected for removal from specialization {}", specializationId);
            redirectAttributes.addFlashAttribute("errorMessage", "No lecturers selected for removal.");
            return "redirect:/staff-home/lecturer-specialization/assign-lecturer";
        }

        session.setAttribute("currentSpecializationId", specializationId);

        int removedCount = 0;
        for (String lecturerId : lecturerIds) {
            if (lecturerId == null || lecturerId.trim().isEmpty()) {
                logger.warn("Invalid lecturer ID: {}", lecturerId);
                continue;
            }
            try {
                if (majorLecturersSpecializationsService.removeLecturerSpecialization(lecturerId, specializationId)) {
                    removedCount++;
                    logger.info("Removed lecturer {} from specialization {}", lecturerId, specializationId);
                }
            } catch (Exception e) {
                logger.error("Error removing lecturer {} from specialization {}: {}", lecturerId, specializationId, e.getMessage(), e);
                redirectAttributes.addFlashAttribute("errorMessage", "Error removing lecturer: " + e.getMessage());
            }
        }

        if (removedCount > 0) {
            redirectAttributes.addFlashAttribute("successMessage", removedCount + " lecturer(s) removed successfully.");
        } else {
            String errorMessage = redirectAttributes.getFlashAttributes().containsKey("errorMessage")
                    ? (String) redirectAttributes.getFlashAttributes().get("errorMessage")
                    : "No lecturers were removed.";
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }

        return "redirect:/staff-home/lecturer-specialization/assign-lecturer";
    }
}