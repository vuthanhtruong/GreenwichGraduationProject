package com.example.demo.classes.controller;

import com.example.demo.classes.service.ClassesService;
import com.example.demo.majorstaff.service.StaffsService;
import com.example.demo.subject.service.MajorSubjectsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/staff-home/classes-list")
public class ListClassesController {

    private final ClassesService classesService;
    private final StaffsService staffsService;
    private final MajorSubjectsService subjectsService;

    @Autowired
    public ListClassesController(ClassesService classesService, StaffsService staffsService, MajorSubjectsService subjectsService) {
        this.classesService = classesService;
        this.staffsService = staffsService;
        this.subjectsService = subjectsService;
    }

    @GetMapping("")
    public String showClassesList(Model model) {
        model.addAttribute("classes", classesService.ClassesByMajor(staffsService.getStaffMajor()));
        model.addAttribute("subjects", subjectsService.AcceptedSubjectsByMajor(staffsService.getStaffMajor()));
        return "ClassesList";
    }
}