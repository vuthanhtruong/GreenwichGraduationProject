package com.example.demo.controller.EditByStaff;

import com.example.demo.entity.MajorSubjects;
import com.example.demo.service.StaffsService;
import com.example.demo.service.SubjectsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
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
public class UpdateSubjectController {

    private final SubjectsService subjectsService;
    private final StaffsService staffsService;

    @Autowired
    public UpdateSubjectController(SubjectsService subjectsService, StaffsService staffsService) {
        this.subjectsService = subjectsService;
        this.staffsService = staffsService;
    }

    @PostMapping("/major-subjects-list/edit-subject-form")
    public String showEditSubjectForm(@RequestParam("id") String id, Model model) {
        MajorSubjects subject = subjectsService.getSubjectById(id);
        if (subject == null) {
            model.addAttribute("message", "Subject not found");
            model.addAttribute("alertClass", "alert-danger");
            return "redirect:/staff-home/major-subjects-list";
        }
        model.addAttribute("subject", subject);
        return "EditSubjectForm";
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

        List<String> editErrors = new ArrayList<>();
        validateSubject(formSubject, bindingResult, editErrors);

        if (!editErrors.isEmpty() || bindingResult.hasErrors()) {
            model.addAttribute("editErrors", editErrors);
            model.addAttribute("subject", formSubject);
            return "EditSubjectForm";
        }

        try {
            existingSubject.setSubjectName(formSubject.getSubjectName() != null ? formSubject.getSubjectName().toUpperCase() : existingSubject.getSubjectName());
            existingSubject.setTuition(formSubject.getTuition());
            existingSubject.setSemester(formSubject.getSemester());
            subjectsService.updateSubject(id, existingSubject);
            redirectAttributes.addFlashAttribute("message", "Subject updated successfully!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } catch (DataAccessException e) {
            redirectAttributes.addFlashAttribute("message", "Database error while updating subject: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Unexpected error while updating subject: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }

        return "redirect:/staff-home/major-subjects-list";
    }

    private void validateSubject(MajorSubjects subject, BindingResult bindingResult, List<String> errors) {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));
        }

        if (!isValidName(subject.getSubjectName())) {
            errors.add("Subject name is not valid. Only letters, numbers, spaces, and standard punctuation are allowed.");
        }

        MajorSubjects existingSubjectByName = subjectsService.getSubjectByName(subject.getSubjectName());
        if (subject.getSubjectName() != null && existingSubjectByName != null &&
                (subject.getSubjectId() == null || !existingSubjectByName.getSubjectId().equals(subject.getSubjectId()))) {
            errors.add("Subject name is already in use.");
        }
    }

    private boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        String nameRegex = "^[\\p{L}0-9][\\p{L}0-9 .'-]{0,49}$";
        return name.matches(nameRegex);
    }
}