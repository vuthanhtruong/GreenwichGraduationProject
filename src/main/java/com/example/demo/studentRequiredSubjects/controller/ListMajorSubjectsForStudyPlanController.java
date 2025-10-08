package com.example.demo.studentRequiredSubjects.controller;

import com.example.demo.entity.Enums.LearningProgramTypes;
import com.example.demo.majorSubject.model.MajorSubjects;
import com.example.demo.majorSubject.service.MajorSubjectsService;
import com.example.demo.staff.service.StaffsService;
import com.example.demo.studentRequiredSubjects.service.StudentRequiredSubjectsService;
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
public class ListMajorSubjectsForStudyPlanController {
    private final MajorSubjectsService subjectsService;
    private final StaffsService staffsService;
    private final StudentRequiredSubjectsService studentRequiredSubjectsService;

    public ListMajorSubjectsForStudyPlanController(MajorSubjectsService subjectsService, StaffsService staffsService, StudentRequiredSubjectsService studentRequiredSubjectsService) {
        this.subjectsService = subjectsService;
        this.staffsService = staffsService;
        this.studentRequiredSubjectsService = studentRequiredSubjectsService;
    }

    @GetMapping("/study-plan")
    public String getStudyPlan(Model model) {
        List<MajorSubjects> subjects = subjectsService.subjectsByMajor(staffsService.getStaffMajor());
        model.addAttribute("subjects", subjects);
        model.addAttribute("LearningProgramTypes", LearningProgramTypes.values());
        model.addAttribute("totalSubjects", subjects.size());
        return "StudyPlan";
    }

    @PostMapping("/study-plan/filter-subjects")
    public String filterSubjects(
            @RequestParam(required = false) String learningProgramType,
            Model model) {
        List<MajorSubjects> subjects;
        if (learningProgramType == null || learningProgramType.isEmpty()) {
            subjects = subjectsService.subjectsByMajor(staffsService.getStaffMajor());
        } else {
            try {
                LearningProgramTypes.valueOf(learningProgramType);
                subjects = studentRequiredSubjectsService.getSubjectsByLearningProgramType(learningProgramType);
            } catch (IllegalArgumentException e) {
                subjects = List.of();
                model.addAttribute("errorMessage", "Invalid program type selected.");
            }
        }
        model.addAttribute("subjects", subjects);
        model.addAttribute("LearningProgramTypes", LearningProgramTypes.values());
        model.addAttribute("learningProgramType", learningProgramType);
        model.addAttribute("totalSubjects", subjects.size());
        if (subjects.isEmpty()) {
            model.addAttribute("errorMessage", model.asMap().getOrDefault("errorMessage", "No subjects found for the selected program type."));
        }
        return "FilterSubjects";
    }
}