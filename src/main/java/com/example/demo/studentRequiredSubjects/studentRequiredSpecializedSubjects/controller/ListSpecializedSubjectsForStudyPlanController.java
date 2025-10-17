package com.example.demo.studentRequiredSubjects.studentRequiredSpecializedSubjects.controller;

import com.example.demo.curriculum.service.CurriculumService;
import com.example.demo.subject.specializedSubject.model.SpecializedSubject;
import com.example.demo.subject.specializedSubject.service.SpecializedSubjectsService;
import com.example.demo.user.staff.service.StaffsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/staff-home")
@PreAuthorize("hasRole('STAFF')")
public class ListSpecializedSubjectsForStudyPlanController {
    private final SpecializedSubjectsService subjectsService;
    private final StaffsService staffsService;
    private final CurriculumService curriculumService;

    public ListSpecializedSubjectsForStudyPlanController(
            SpecializedSubjectsService subjectsService,
            StaffsService staffsService,
            CurriculumService curriculumService) {
        this.subjectsService = subjectsService;
        this.staffsService = staffsService;
        this.curriculumService = curriculumService;
    }

    @GetMapping("/specialized-study-plan")
    public String getStudyPlan(Model model) {
        List<SpecializedSubject> subjects = subjectsService.subjectsByMajor(staffsService.getStaffMajor());
        model.addAttribute("subjects", subjects);
        model.addAttribute("curriculums", curriculumService.getCurriculums());
        model.addAttribute("totalSubjects", subjects.size());
        return "SpecializedStudyPlan";
    }

    @PostMapping("/specialized-study-plan/filter-subjects")
    public String filterSubjects(
            @RequestParam(required = false) String curriculumId,
            Model model) {
        List<SpecializedSubject> subjects;
        if (curriculumId == null || curriculumId.isEmpty()) {
            subjects = subjectsService.subjectsByMajor(staffsService.getStaffMajor());
        } else {
            subjects = subjectsService.getSubjectsByCurriculumId(curriculumId);
            if (subjects.isEmpty()) {
                model.addAttribute("errorMessage", "No subjects found for the selected curriculum.");
            }
        }
        model.addAttribute("subjects", subjects);
        model.addAttribute("curriculumId", curriculumId);
        model.addAttribute("curriculums", curriculumService.getCurriculums());
        model.addAttribute("totalSubjects", subjects.size());
        return "FilterSpecializedSubjects";
    }
}