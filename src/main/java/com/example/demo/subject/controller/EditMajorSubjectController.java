package com.example.demo.subject.controller;

import com.example.demo.Curriculum.model.Curriculum;
import com.example.demo.Curriculum.service.CurriculumService;
import com.example.demo.subject.model.MajorSubjects;
import com.example.demo.staff.service.StaffsService;
import com.example.demo.subject.service.MajorSubjectsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/staff-home")
@PreAuthorize("hasRole('STAFF')")
public class EditMajorSubjectController {

    private final MajorSubjectsService subjectsService;
    private final StaffsService staffsService;
    private final CurriculumService curriculumService;

    @Autowired
    public EditMajorSubjectController(MajorSubjectsService subjectsService, StaffsService staffsService, CurriculumService curriculumService) {
        this.subjectsService = subjectsService;
        this.staffsService = staffsService;
        this.curriculumService = curriculumService;
    }

    @PostMapping("/major-subjects-list/edit-major-subject-form")
    public String showEditMajorSubjectForm(@RequestParam("id") String id, Model model, RedirectAttributes redirectAttributes) {
        MajorSubjects subject = subjectsService.getSubjectById(id);
        if (subject == null) {
            redirectAttributes.addFlashAttribute("message", "Subject not found.");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            return "redirect:/staff-home/major-subjects-list";
        }
        model.addAttribute("subject", subject);
        model.addAttribute("curriculums", curriculumService.getCurriculums());
        return "EditMajorSubjectForm";
    }

    @PutMapping("/major-subjects-list/edit-major-subject")
    public String editSubject(
            @Valid @ModelAttribute("subject") MajorSubjects formSubject,
            BindingResult bindingResult,
            @RequestParam(value = "curriculumId", required = false) String curriculumId,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Validate input using DAO
        Map<String, String> errors = subjectsService.validateSubject(formSubject, curriculumId);
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> {
                String field = bindingResult.getFieldError() != null ? bindingResult.getFieldError().getField() : "general";
                errors.put(field, error.getDefaultMessage());
            });
        }

        if (!errors.isEmpty()) {
            model.addAttribute("editErrors", errors);
            model.addAttribute("subject", formSubject);
            model.addAttribute("curriculums", curriculumService.getCurriculums());
            return "EditMajorSubjectForm";
        }

        try {
            // Retrieve existing subject
            MajorSubjects existingSubject = subjectsService.getSubjectById(formSubject.getSubjectId());
            // Set updated fields
            existingSubject.setSubjectName(formSubject.getSubjectName() != null ? formSubject.getSubjectName().toUpperCase() : existingSubject.getSubjectName());
            existingSubject.setSemester(formSubject.getSemester());
            existingSubject.setCurriculum(curriculumService.getCurriculumById(curriculumId));

            // Update the subject
            subjectsService.editSubject(formSubject.getSubjectId(), existingSubject);

            redirectAttributes.addFlashAttribute("message", "Subject edited successfully!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
            return "redirect:/staff-home/major-subjects-list";
        } catch (Exception e) {
            errors.put("general", "Error updating subject: " + e.getMessage());
            model.addAttribute("editErrors", errors);
            model.addAttribute("subject", formSubject);
            model.addAttribute("curriculums", curriculumService.getCurriculums());
            return "EditMajorSubjectForm";
        }
    }
}