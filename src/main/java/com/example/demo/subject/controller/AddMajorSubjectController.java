package com.example.demo.subject.controller;

import com.example.demo.entity.Enums.LearningProgramTypes;
import com.example.demo.subject.model.MajorSubjects;
import com.example.demo.staff.service.StaffsService;
import com.example.demo.subject.service.MajorSubjectsService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/staff-home/major-subjects-list")
@PreAuthorize("hasRole('STAFF')")
public class AddMajorSubjectController {

    private final MajorSubjectsService subjectsService;
    private final StaffsService staffsService;

    @Autowired
    public AddMajorSubjectController(MajorSubjectsService subjectsService, StaffsService staffsService) {
        this.subjectsService = subjectsService;
        this.staffsService = staffsService;
    }

    @PostMapping("/add-subject")
    public String addSubject(
            @Valid @ModelAttribute("newSubject") MajorSubjects newSubject,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        Map<String, String> errors = new HashMap<>(subjectsService.validateSubject(newSubject));
        if (result.hasErrors()) {
            result.getAllErrors().forEach(error -> {
                String field = result.getFieldError() != null ? result.getFieldError().getField() : "general";
                errors.put(field, error.getDefaultMessage());
            });
        }

        if (!errors.isEmpty()) {
            redirectAttributes.addFlashAttribute("editErrors", errors);
            redirectAttributes.addFlashAttribute("newSubject", newSubject);
            redirectAttributes.addFlashAttribute("openAddOverlay", true); // Mở lại overlay
            return "redirect:/staff-home/major-subjects-list";
        }

        try {
            if (staffsService.getStaff() == null || staffsService.getStaffMajor() == null) {
                errors.put("general", "Staff or major not found.");
                redirectAttributes.addFlashAttribute("editErrors", errors);
                redirectAttributes.addFlashAttribute("newSubject", newSubject);
                redirectAttributes.addFlashAttribute("openAddOverlay", true);
                return "redirect:/staff-home/major-subjects-list";
            }

            newSubject.setCreator(staffsService.getStaff());
            newSubject.setMajor(staffsService.getStaffMajor());
            String subjectId = subjectsService.generateUniqueSubjectId(staffsService.getStaffMajor().getMajorId(), LocalDate.now());
            newSubject.setSubjectId(subjectId);

            subjectsService.addSubject(newSubject);
            redirectAttributes.addFlashAttribute("message", "Subject added successfully!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
            return "redirect:/staff-home/major-subjects-list";
        } catch (Exception e) {
            errors.put("general", "An error occurred while adding the subject: " + e.getMessage());
            redirectAttributes.addFlashAttribute("editErrors", errors);
            redirectAttributes.addFlashAttribute("newSubject", newSubject);
            redirectAttributes.addFlashAttribute("openAddOverlay", true);
            return "redirect:/staff-home/major-subjects-list";
        }
    }
}