package com.example.demo.subject.controller;

import com.example.demo.Curriculum.model.Curriculum;
import com.example.demo.Curriculum.service.CurriculumService;
import com.example.demo.Specialization.model.Specialization;
import com.example.demo.Specialization.service.SpecializationService;
import com.example.demo.entity.Enums.LearningProgramTypes;
import com.example.demo.subject.model.SpecializedSubject;
import com.example.demo.staff.service.StaffsService;
import com.example.demo.subject.service.SpecializedSubjectsService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/staff-home/specialized-subjects-list")
@PreAuthorize("hasRole('STAFF')")
public class AddSpecializedSubjectController {

    private final SpecializedSubjectsService subjectsService;
    private final StaffsService staffsService;
    private final CurriculumService curriculumService;
    private final SpecializationService specializationService;

    @Autowired
    public AddSpecializedSubjectController(SpecializedSubjectsService subjectsService, StaffsService staffsService,
                                           CurriculumService curriculumService, SpecializationService specializationService) {
        this.subjectsService = subjectsService;
        this.staffsService = staffsService;
        this.curriculumService = curriculumService;
        this.specializationService = specializationService;
    }

    @PostMapping("/add-subject")
    public String addSubject(
            @Valid @ModelAttribute("newSubject") SpecializedSubject newSubject,
            BindingResult result,
            @RequestParam(value = "curriculumId", required = false) String curriculumId,
            @RequestParam(value = "specializationId", required = false) String specializationId,
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

        // Validate curriculum
        if (curriculumId != null && !curriculumId.isEmpty()) {
            Curriculum curriculum = curriculumService.getCurriculumById(curriculumId);
            if (curriculum != null) {
                newSubject.setCurriculum(curriculum);
            } else {
                errors.put("curriculumId", "Invalid curriculum selected.");
            }
        } else {
            errors.put("curriculumId", "Curriculum is required.");
        }

        // Validate specialization
        Specialization specialization = null;
        if (specializationId != null && !specializationId.isEmpty()) {
            specialization = specializationService.getSpecializationById(specializationId);
            if (specialization == null) {
                errors.put("specializationId", "Invalid specialization selected.");
            }
        } else {
            errors.put("specializationId", "Specialization is required.");
        }

        if (!errors.isEmpty()) {
            redirectAttributes.addFlashAttribute("editErrors", errors);
            redirectAttributes.addFlashAttribute("newSubject", newSubject);
            redirectAttributes.addFlashAttribute("openAddOverlay", true);
            redirectAttributes.addFlashAttribute("curriculums", curriculumService.getCurriculums());
            redirectAttributes.addFlashAttribute("specializations", specializationService.specializationsByMajor(staffsService.getStaffMajor()));
            return "redirect:/staff-home/specialized-subjects-list";
        }

        try {
            if (staffsService.getStaff() == null) {
                errors.put("general", "Authenticated staff not found.");
                redirectAttributes.addFlashAttribute("editErrors", errors);
                redirectAttributes.addFlashAttribute("newSubject", newSubject);
                redirectAttributes.addFlashAttribute("openAddOverlay", true);
                redirectAttributes.addFlashAttribute("curriculums", curriculumService.getCurriculums());
                redirectAttributes.addFlashAttribute("specializations", specializationService.specializationsByMajor(staffsService.getStaffMajor()));
                return "redirect:/staff-home/specialized-subjects-list";
            }

            newSubject.setCreator(staffsService.getStaff());
            String subjectId = subjectsService.generateUniqueSubjectId(specializationId, LocalDate.now());
            newSubject.setSubjectId(subjectId);

            subjectsService.addSubject(newSubject, specialization);
            redirectAttributes.addFlashAttribute("message", "Specialized subject added successfully!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
            return "redirect:/staff-home/specialized-subjects-list";
        } catch (Exception e) {
            errors.put("general", "An error occurred while adding the specialized subject: " + e.getMessage());
            redirectAttributes.addFlashAttribute("editErrors", errors);
            redirectAttributes.addFlashAttribute("newSubject", newSubject);
            redirectAttributes.addFlashAttribute("openAddOverlay", true);
            redirectAttributes.addFlashAttribute("curriculums", curriculumService.getCurriculums());
            redirectAttributes.addFlashAttribute("specializations", specializationService.specializationsByMajor(staffsService.getStaffMajor()));
            return "redirect:/staff-home/specialized-subjects-list";
        }
    }
}