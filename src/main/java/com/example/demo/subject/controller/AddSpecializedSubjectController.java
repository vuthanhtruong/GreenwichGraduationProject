package com.example.demo.subject.controller;

import com.example.demo.Curriculum.model.Curriculum;
import com.example.demo.Curriculum.service.CurriculumService;
import com.example.demo.Specialization.model.Specialization;
import com.example.demo.Specialization.service.SpecializationService;
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

        // Validate input using DAO
        Map<String, String> errors = subjectsService.validateSubject(newSubject, specializationId, curriculumId);
        if (result.hasErrors()) {
            result.getAllErrors().forEach(error -> {
                String field = result.getFieldError() != null ? result.getFieldError().getField() : "general";
                errors.put(field, error.getDefaultMessage());
            });
        }

        if (!errors.isEmpty()) {
            model.addAttribute("editErrors", errors);
            model.addAttribute("newSubject", newSubject);
            model.addAttribute("openAddOverlay", true);
            model.addAttribute("curriculums", curriculumService.getCurriculums());
            model.addAttribute("specializations", specializationService.specializationsByMajor(staffsService.getStaffMajor()));
            return "SpecializedSubjectsList";
        }

        try {
            // Set creator and generate subject ID
            newSubject.setCreator(staffsService.getStaff());
            String subjectId = subjectsService.generateUniqueSubjectId(specializationId, LocalDate.now());
            newSubject.setSubjectId(subjectId);

            // Retrieve specialization and curriculum for setting
            Specialization specialization = specializationService.getSpecializationById(specializationId);
            Curriculum curriculum = curriculumService.getCurriculumById(curriculumId);
            newSubject.setSpecialization(specialization);
            newSubject.setCurriculum(curriculum);

            // Add the subject
            subjectsService.addSubject(newSubject, specialization);
            redirectAttributes.addFlashAttribute("message", "Specialized subject added successfully!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
            return "redirect:/staff-home/specialized-subjects-list";
        } catch (Exception e) {
            errors.put("general", "An error occurred while adding the specialized subject: " + e.getMessage());
            model.addAttribute("editErrors", errors);
            model.addAttribute("newSubject", newSubject);
            model.addAttribute("openAddOverlay", true);
            model.addAttribute("curriculums", curriculumService.getCurriculums());
            model.addAttribute("specializations", specializationService.specializationsByMajor(staffsService.getStaffMajor()));
            return "SpecializedSubjectsList";
        }
    }
}