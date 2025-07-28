package com.example.demo.controller.Read;

import com.example.demo.entity.Semester;
import com.example.demo.entity.Subjects;
import com.example.demo.service.StaffsService;
import com.example.demo.service.SubjectsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;

@Controller
@RequestMapping("/staff-home")
@PreAuthorize("hasRole('STAFF')")
public class ListSubjectController {

    private final SubjectsService subjectsService;
    private final StaffsService staffsService;

    @Autowired
    public ListSubjectController(SubjectsService subjectsService, StaffsService staffsService) {
        this.subjectsService = subjectsService;
        this.staffsService = staffsService;
    }

    @GetMapping("/subjects")
    public String subjectsList(Model model) {
        model.addAttribute("subjects", subjectsService.subjectsByMajor(staffsService.getMajors()));
        model.addAttribute("newSubject", new Subjects());
        model.addAttribute("semesters", Arrays.asList(Semester.values()));
        return "SubjectsList";
    }
}