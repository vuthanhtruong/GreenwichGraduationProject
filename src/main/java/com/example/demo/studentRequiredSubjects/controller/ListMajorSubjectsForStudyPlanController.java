package com.example.demo.studentRequiredSubjects.controller;

import com.example.demo.subject.service.MajorSubjectsService;
import com.example.demo.majorstaff.service.StaffsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/staff-home")
@PreAuthorize("hasRole('STAFF')")
public class ListMajorSubjectsForStudyPlanController {
    private final MajorSubjectsService subjectsService;
    private final StaffsService staffsService;

    public ListMajorSubjectsForStudyPlanController(MajorSubjectsService subjectsService, StaffsService staffsService) {
        this.subjectsService = subjectsService;
        this.staffsService = staffsService;
    }
    @GetMapping("/study-plan")
    public String getStudyPlan(Model model) {
        model.addAttribute("subjects", subjectsService.subjectsByMajor(staffsService.getStaffMajor()));
        return "StudyPlan";
    }
}
