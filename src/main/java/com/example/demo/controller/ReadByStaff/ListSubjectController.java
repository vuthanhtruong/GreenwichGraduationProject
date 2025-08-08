package com.example.demo.controller.ReadByStaff;

import com.example.demo.entity.MajorSubjects;
import com.example.demo.service.StaffsService;
import com.example.demo.service.MajorSubjectsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/staff-home")
@PreAuthorize("hasRole('STAFF')")
public class ListSubjectController {

    private final MajorSubjectsService subjectsService;
    private final StaffsService staffsService;

    @Autowired
    public ListSubjectController(MajorSubjectsService subjectsService, StaffsService staffsService) {
        this.subjectsService = subjectsService;
        this.staffsService = staffsService;
    }

    @GetMapping("/major-subjects-list")
    public String showSubjectsList(Model model) {
        model.addAttribute("newSubject", new MajorSubjects());
        model.addAttribute("subjects", subjectsService.subjectsByMajor(staffsService.getStaffMajor()));
        return "SubjectsList";
    }
}