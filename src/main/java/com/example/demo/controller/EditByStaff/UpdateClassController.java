package com.example.demo.controller.EditByStaff;

import com.example.demo.entity.MajorClasses;
import com.example.demo.entity.Staffs;
import com.example.demo.entity.MajorSubjects;
import com.example.demo.service.ClassesService;
import com.example.demo.service.StaffsService;
import com.example.demo.service.MajorSubjectsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
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
        // Security check

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
        // Validate class data
        List<String> editErrors = new ArrayList<>();
        validateClass(classObj, bindingResult, editErrors);

        if (!editErrors.isEmpty() || bindingResult.hasErrors()) {
            model.addAttribute("editErrors", editErrors);
            model.addAttribute("class", classObj);
            model.addAttribute("classes", classesService.ClassesByMajor(staffsService.getStaffMajor()));
            model.addAttribute("subjects", subjectsService.subjectsByMajor(staffsService.getStaffMajor()));
            return "ClassesList";
        }

        try {
            // Update class
            classesService.updateClass(classObj.getClassId(), classObj);
            redirectAttributes.addFlashAttribute("successMessage", "Class updated successfully!");
        } catch (DataAccessException e) {
            redirectAttributes.addFlashAttribute("errors", List.of("Database error while updating class: " + e.getMessage()));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errors", List.of("Unexpected error while updating class: " + e.getMessage()));
        }

        return "redirect:/staff-home/classes-list";
    }
    private void validateClass(MajorClasses classObj, BindingResult bindingResult, List<String> errors) {
        // Annotation-based validation
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));
        }

        // Custom validations
        if (!isValidName(classObj.getNameClass())) {
            errors.add("Class name is not valid. Only letters, numbers, spaces, and standard punctuation are allowed.");
        }

        // Check for duplicate class name (excluding current class)
        if (classObj.getNameClass() != null && classesService.getClassByName(classObj.getNameClass()) != null &&
                !classesService.getClassByName(classObj.getNameClass()).getClassId().equals(classObj.getClassId())) {
            errors.add("Class name is already in use.");
        }

        // Validate subject
        if (classObj.getSubject() != null && classObj.getSubject().getSubjectId() != null) {
            MajorSubjects subject = subjectsService.getSubjectById(classObj.getSubject().getSubjectId());
            if (subject == null) {
                errors.add("Invalid subject selected.");
            } else {
                classObj.setSubject(subject); // Ensure the subject is a managed entity
            }
        } else {
            errors.add("Subject is required.");
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
