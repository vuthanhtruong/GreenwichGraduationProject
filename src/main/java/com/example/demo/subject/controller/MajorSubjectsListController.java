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

import java.util.ArrayList;
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

    @GetMapping("")
    public String showSubjectsList(
            Model model,
            HttpSession session,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize) {
        try {
            if (pageSize == null) {
                pageSize = (Integer) session.getAttribute("subjectPageSize");
                if (pageSize == null) {
                    pageSize = 5;
                }
            }
            session.setAttribute("subjectPageSize", pageSize);

            Long totalSubjects = subjectsService.numberOfSubjects(staffsService.getStaffMajor());
            int totalPages = Math.max(1, (int) Math.ceil((double) totalSubjects / pageSize));
            page = Math.max(1, Math.min(page, totalPages));
            session.setAttribute("subjectPage", page);
            session.setAttribute("subjectTotalPages", totalPages);

            if (totalSubjects == 0) {
                model.addAttribute("subjects", new ArrayList<>());
                model.addAttribute("newSubject", new MajorSubjects());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", pageSize);
                model.addAttribute("totalSubjects", 0);
                model.addAttribute("message", "No subjects found for this major.");
                model.addAttribute("alertClass", "alert-warning");
                model.addAttribute("learningProgramTypes", LearningProgramTypes.values());
                return "MajorSubjectsList";
            }

            int firstResult = (page - 1) * pageSize;
            List<MajorSubjects> subjects = subjectsService.getPaginatedSubjects(firstResult, pageSize, staffsService.getStaffMajor());

            model.addAttribute("subjects", subjects);
            model.addAttribute("newSubject", new MajorSubjects());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalSubjects", totalSubjects);
            model.addAttribute("learningProgramTypes", LearningProgramTypes.values());
            return "MajorSubjectsList";
        } catch (Exception e) {
            model.addAttribute("errors", List.of("An error occurred while retrieving subjects: " + e.getMessage()));
            model.addAttribute("newSubject", new MajorSubjects());
            model.addAttribute("currentPage", 1);
            model.addAttribute("totalPages", 1);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalSubjects", 0);
            model.addAttribute("learningProgramTypes", LearningProgramTypes.values());
            return "MajorSubjectsList";
        }
    }
}