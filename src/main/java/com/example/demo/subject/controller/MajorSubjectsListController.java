package com.example.demo.subject.controller;

import com.example.demo.entity.Enums.LearningProgramTypes;
import com.example.demo.subject.model.MajorSubjects;
import com.example.demo.staff.service.StaffsService;
import com.example.demo.subject.service.MajorSubjectsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/staff-home/major-subjects-list")
@PreAuthorize("hasRole('STAFF')")
public class MajorSubjectsListController {

    private final MajorSubjectsService subjectsService;
    private final StaffsService staffsService;

    @Autowired
    public MajorSubjectsListController(MajorSubjectsService subjectsService, StaffsService staffsService) {
        this.subjectsService = subjectsService;
        this.staffsService = staffsService;
    }

    @GetMapping
    public String showSubjectsList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(value = "successMessage", required = false) String successMessage,
            @RequestParam(value = "errorMessage", required = false) String errorMessage,
            Model model,
            HttpSession session) {
        try {
            if (pageSize == null) {
                pageSize = (Integer) session.getAttribute("subjectsPageSize");
                if (pageSize == null) {
                    pageSize = 5;
                }
            }
            session.setAttribute("subjectsPageSize", pageSize);

            if (staffsService.getStaffMajor() == null) {
                model.addAttribute("errorMessage", "No major assigned to the current staff.");
                model.addAttribute("subjects", List.of());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", pageSize);
                model.addAttribute("newSubject", new MajorSubjects());
                model.addAttribute("learningProgramTypes", LearningProgramTypes.values());
                return "MajorSubjectsList";
            }

            long totalSubjects = subjectsService.numberOfSubjects(staffsService.getStaffMajor());
            List<MajorSubjects> subjects = subjectsService.getPaginatedSubjects((page - 1) * pageSize, pageSize, staffsService.getStaffMajor());

            if (totalSubjects == 0) {
                model.addAttribute("subjects", List.of());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", pageSize);
                model.addAttribute("newSubject", new MajorSubjects());
                model.addAttribute("learningProgramTypes", LearningProgramTypes.values());
                model.addAttribute("message", successMessage != null ? successMessage : (errorMessage != null ? errorMessage : "No subjects found."));
                return "MajorSubjectsList";
            }

            int totalPages = (int) Math.ceil((double) totalSubjects / pageSize);
            if (page < 1) page = 1;
            if (page > totalPages) page = totalPages;

            model.addAttribute("subjects", subjects);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("newSubject", new MajorSubjects());
            model.addAttribute("learningProgramTypes", LearningProgramTypes.values());
            if (successMessage != null) {
                model.addAttribute("successMessage", successMessage);
            } else if (errorMessage != null) {
                model.addAttribute("errorMessage", errorMessage);
            }

            return "MajorSubjectsList";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "An error occurred while loading subjects: " + e.getMessage());
            model.addAttribute("subjects", List.of());
            model.addAttribute("currentPage", 1);
            model.addAttribute("totalPages", 1);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("newSubject", new MajorSubjects());
            model.addAttribute("learningProgramTypes", LearningProgramTypes.values());
            return "MajorSubjectsList";
        }
    }
}