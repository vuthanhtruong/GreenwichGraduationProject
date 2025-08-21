package com.example.demo.classes.controller;

import com.example.demo.classes.model.MajorClasses;
import com.example.demo.majorStaff.model.Staffs;
import com.example.demo.classes.service.ClassesService;
import com.example.demo.majorStaff.service.StaffsService;
import com.example.demo.subject.service.MajorSubjectsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/staff-home/classes-list")
public class UpdateClassController {

    private final ClassesService classesService;
    private final StaffsService staffsService;
    private final MajorSubjectsService subjectsService;

    @Autowired
    public UpdateClassController(ClassesService classesService, StaffsService staffsService, MajorSubjectsService subjectsService) {
        this.classesService = classesService;
        this.staffsService = staffsService;
        this.subjectsService = subjectsService;
    }

    @PostMapping("/edit-class-form")
    public String showEditClassForm(
            @RequestParam("id") String classId,
            Model model,
            RedirectAttributes redirectAttributes) {
        Staffs user = staffsService.getStaff();
        if (!(user instanceof Staffs)) {
            redirectAttributes.addFlashAttribute("errors", List.of("Only staff members can edit classes."));
            return "redirect:/staff-home/classes-list";
        }

        MajorClasses editClass = classesService.getClassById(classId);
        if (editClass == null) {
            redirectAttributes.addFlashAttribute("errors", List.of("Class not found."));
            return "redirect:/staff-home/classes-list";
        }

        model.addAttribute("editErrors", editClass);
        model.addAttribute("classes", classesService.ClassesByMajor(staffsService.getStaffMajor()));
        model.addAttribute("subjects", subjectsService.subjectsByMajor(staffsService.getStaffMajor()));
        return "EditFormClass";
    }

    @PutMapping("/edit-class")
    public String updateClass(
            @Valid @ModelAttribute("class") MajorClasses classObj,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        List<String> errors = classesService.validateClass(classObj, classObj.getClassId());

        if (!errors.isEmpty() || bindingResult.hasErrors()) {
            errors.addAll(bindingResult.getAllErrors().stream().map(error -> error.getDefaultMessage()).toList());
            model.addAttribute("editErrors", errors);
            model.addAttribute("class", classObj);
            model.addAttribute("classes", classesService.ClassesByMajor(staffsService.getStaffMajor()));
            model.addAttribute("subjects", subjectsService.subjectsByMajor(staffsService.getStaffMajor()));
            return "ClassesList";
        }

        try {
            classesService.updateClass(classObj.getClassId(), classObj);
            redirectAttributes.addFlashAttribute("successMessage", "Class updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errors", List.of("Error updating class: " + e.getMessage()));
            model.addAttribute("editErrors", List.of("Error updating class: " + e.getMessage()));
            model.addAttribute("class", classObj);
            model.addAttribute("classes", classesService.ClassesByMajor(staffsService.getStaffMajor()));
            model.addAttribute("subjects", subjectsService.subjectsByMajor(staffsService.getStaffMajor()));
            return "ClassesList";
        }

        return "redirect:/staff-home/classes-list";
    }
}