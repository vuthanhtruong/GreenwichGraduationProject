package com.example.demo.subject.controller;

import com.example.demo.entity.Enums.LearningProgramTypes;
import com.example.demo.subject.model.MajorSubjects;
import com.example.demo.majorStaff.service.StaffsService;
import com.example.demo.subject.service.MajorSubjectsService;
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
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/staff-home")
@PreAuthorize("hasRole('STAFF')")
public class AddMajorSubjectController {

    private final MajorSubjectsService subjectsService;
    private final StaffsService staffsService;

    @Autowired
    public AddMajorSubjectController(MajorSubjectsService subjectsService, StaffsService staffsService) {
        this.subjectsService = subjectsService;
        this.staffsService = staffsService;
    }

    @PostMapping("/major-subjects-list/add-subject")
    public String addSubject(
            @Valid @ModelAttribute("newSubject") MajorSubjects newSubject,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        List<String> errors = new ArrayList<>(subjectsService.validateSubject(newSubject)); // Sao chép danh sách lỗi
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));
        }

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("subjects", subjectsService.subjectsByMajor(staffsService.getStaffMajor()));
            model.addAttribute("learningProgramTypes", LearningProgramTypes.values());
            return "SubjectsList";
        }

        try {
            if (staffsService.getStaff() == null || staffsService.getStaffMajor() == null) {
                errors.add("Staff or major not found");
                model.addAttribute("errors", errors);
                model.addAttribute("subjects", subjectsService.subjectsByMajor(null));
                model.addAttribute("learningProgramTypes", LearningProgramTypes.values());
                return "SubjectsList";
            }
            newSubject.setCreator(staffsService.getStaff());
            newSubject.setMajor(staffsService.getStaffMajor());
            String subjectId = subjectsService.generateUniqueSubjectId(staffsService.getStaffMajor().getMajorId(), LocalDate.now());
            newSubject.setSubjectId(subjectId);

            subjectsService.addSubject(newSubject);
            redirectAttributes.addFlashAttribute("successMessage", "Subject added successfully!");
            return "redirect:/staff-home/major-subjects-list";
        } catch (Exception e) {
            errors.add("Failed to add subject: " + e.getMessage());
            model.addAttribute("errors", errors);
            model.addAttribute("subjects", subjectsService.subjectsByMajor(staffsService.getStaffMajor()));
            model.addAttribute("learningProgramTypes", LearningProgramTypes.values());
            return "SubjectsList";
        }
    }
}