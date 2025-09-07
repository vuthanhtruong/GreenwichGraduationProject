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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        List<String> errors = new ArrayList<>(subjectsService.validateSubject(newSubject));

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("newSubject", newSubject);
            model.addAttribute("subjects", subjectsService.getPaginatedSubjects(0, (Integer) session.getAttribute("subjectPageSize") != null ? (Integer) session.getAttribute("subjectPageSize") : 5, staffsService.getStaffMajor()));
            model.addAttribute("currentPage", session.getAttribute("subjectPage") != null ? session.getAttribute("subjectPage") : 1);
            model.addAttribute("totalPages", session.getAttribute("subjectTotalPages") != null ? session.getAttribute("subjectTotalPages") : 1);
            model.addAttribute("pageSize", session.getAttribute("subjectPageSize") != null ? session.getAttribute("subjectPageSize") : 5);
            model.addAttribute("totalSubjects", subjectsService.numberOfSubjects(staffsService.getStaffMajor()));
            model.addAttribute("learningProgramTypes", LearningProgramTypes.values());
            return "MajorSubjectsList";
        }

        try {
            if (staffsService.getStaff() == null || staffsService.getStaffMajor() == null) {
                errors.add("Staff or major not found.");
                model.addAttribute("errors", errors);
                model.addAttribute("newSubject", newSubject);
                model.addAttribute("subjects", subjectsService.getPaginatedSubjects(0, (Integer) session.getAttribute("subjectPageSize") != null ? (Integer) session.getAttribute("subjectPageSize") : 5, staffsService.getStaffMajor()));
                model.addAttribute("currentPage", session.getAttribute("subjectPage") != null ? session.getAttribute("subjectPage") : 1);
                model.addAttribute("totalPages", session.getAttribute("subjectTotalPages") != null ? session.getAttribute("subjectTotalPages") : 1);
                model.addAttribute("pageSize", session.getAttribute("subjectPageSize") != null ? session.getAttribute("subjectPageSize") : 5);
                model.addAttribute("totalSubjects", subjectsService.numberOfSubjects(staffsService.getStaffMajor()));
                model.addAttribute("learningProgramTypes", LearningProgramTypes.values());
                return "MajorSubjectsList";
            }

            newSubject.setCreator(staffsService.getStaff());
            newSubject.setMajor(staffsService.getStaffMajor());
            String subjectId = subjectsService.generateUniqueSubjectId(staffsService.getStaffMajor().getMajorId(), LocalDate.now());
            newSubject.setSubjectId(subjectId);

            subjectsService.addSubject(newSubject);
            redirectAttributes.addFlashAttribute("message", "Subject added successfully!");
            return "redirect:/staff-home/major-subjects-list";
        } catch (Exception e) {
            errors.add("An error occurred while adding the subject: " + e.getMessage());
            model.addAttribute("errors", errors);
            model.addAttribute("newSubject", newSubject);
            model.addAttribute("subjects", subjectsService.getPaginatedSubjects(0, (Integer) session.getAttribute("subjectPageSize") != null ? (Integer) session.getAttribute("subjectPageSize") : 5, staffsService.getStaffMajor()));
            model.addAttribute("currentPage", session.getAttribute("subjectPage") != null ? session.getAttribute("subjectPage") : 1);
            model.addAttribute("totalPages", session.getAttribute("subjectTotalPages") != null ? session.getAttribute("subjectTotalPages") : 1);
            model.addAttribute("pageSize", session.getAttribute("subjectPageSize") != null ? session.getAttribute("subjectPageSize") : 5);
            model.addAttribute("totalSubjects", subjectsService.numberOfSubjects(staffsService.getStaffMajor()));
            model.addAttribute("learningProgramTypes", LearningProgramTypes.values());
            return "MajorSubjectsList";
        }
    }
}