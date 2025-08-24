package com.example.demo.subject.controller;

import com.example.demo.subject.model.MajorSubjects;
import com.example.demo.Staff.service.StaffsService;
import com.example.demo.subject.service.MajorSubjectsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/staff-home")
@PreAuthorize("hasRole('STAFF')")
public class EditMajorSubjectController {

    private final MajorSubjectsService subjectsService;
    private final StaffsService staffsService;

    @Autowired
    public EditMajorSubjectController(MajorSubjectsService subjectsService, StaffsService staffsService) {
        this.subjectsService = subjectsService;
        this.staffsService = staffsService;
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
        return "EditMajorSubjectForm";
    }

    @PutMapping("/major-subjects-list/edit-subject/{id}")
    public String editSubject(
            @PathVariable("id") String id,
            @Valid @ModelAttribute("subject") MajorSubjects formSubject,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        MajorSubjects existingSubject = subjectsService.getSubjectById(id);
        if (existingSubject == null) {
            redirectAttributes.addFlashAttribute("message", "Subject not found.");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            return "redirect:/staff-home/major-subjects-list";
        }

        List<String> errors = new ArrayList<>(subjectsService.validateSubject(formSubject)); // Sao chép danh sách lỗi
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));
        }

        if (!errors.isEmpty()) {
            model.addAttribute("editErrors", errors);
            model.addAttribute("subject", formSubject);
            return "EditMajorSubjectForm";
        }

        try {
            existingSubject.setSubjectName(formSubject.getSubjectName() != null ? formSubject.getSubjectName().toUpperCase() : existingSubject.getSubjectName());
            existingSubject.setSemester(formSubject.getSemester());
            subjectsService.editSubject(id, existingSubject);
            redirectAttributes.addFlashAttribute("message", "Subject editd successfully!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } catch (Exception e) {
            errors.add("Error updating subject: " + e.getMessage());
            model.addAttribute("editErrors", errors);
            model.addAttribute("subject", formSubject);
            return "EditMajorSubjectForm";
        }

        return "redirect:/staff-home/major-subjects-list";
    }
}