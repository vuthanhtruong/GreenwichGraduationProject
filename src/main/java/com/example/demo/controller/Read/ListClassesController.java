package com.example.demo.controller.Read;

import com.example.demo.entity.Classes;
import com.example.demo.entity.Subjects;
import com.example.demo.service.ClassesService;
import com.example.demo.service.StaffsService;
import com.example.demo.service.SubjectsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/staff-home/classes-list")
public class ListClassesController {

    private final ClassesService classesService;
    private final StaffsService staffsService;
    private final SubjectsService subjectsService;

    @Autowired
    public ListClassesController(ClassesService classesService, StaffsService staffsService, SubjectsService subjectsService) {
        this.classesService = classesService;
        this.staffsService = staffsService;
        this.subjectsService = subjectsService;
    }

    @GetMapping("")
    public String showClassesList(Model model) {
        model.addAttribute("classes", classesService.ClassesByMajor(staffsService.getMajors()));
        model.addAttribute("subjects", subjectsService.subjectsByMajor(staffsService.getMajors()));
        return "ClassesList";
    }
}