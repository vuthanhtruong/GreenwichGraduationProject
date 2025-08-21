package com.example.demo.subject.controller;

import com.example.demo.entity.Enums.LearningProgramTypes;
import com.example.demo.subject.model.MajorSubjects;
import com.example.demo.majorstaff.service.StaffsService;
import com.example.demo.subject.service.MajorSubjectsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/staff-home")
@PreAuthorize("hasRole('STAFF')")
public class ListMajorSubjectsController {

    private final MajorSubjectsService subjectsService;
    private final StaffsService staffsService;

    @Autowired
    public ListMajorSubjectsController(MajorSubjectsService subjectsService, StaffsService staffsService) {
        this.subjectsService = subjectsService;
        this.staffsService = staffsService;
    }

    @GetMapping("/major-subjects-list")
    public String showSubjectsList(Model model) {
        model.addAttribute("newSubject", new MajorSubjects());
        model.addAttribute("learningProgramTypes", LearningProgramTypes.values());
        model.addAttribute("subjects", subjectsService.subjectsByMajor(staffsService.getStaffMajor()));
        return "SubjectsList";
    }
}