package com.example.demo.student_class.controller;

import com.example.demo.student.service.StudentsService;
import com.example.demo.student_class.service.Students_ClassesService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/student-home/student-classes-list")
public class StudentClassesList {
    private final StudentsService studentsService;
    private final Students_ClassesService studentsClassesService;

    public StudentClassesList(StudentsService studentsService, Students_ClassesService studentsClassesService) {
        this.studentsService = studentsService;
        this.studentsClassesService = studentsClassesService;
    }

    @GetMapping("")
    public String studentClassesList(Model model) {
        model.addAttribute("classes",studentsClassesService.studentClassesList(studentsService.getStudent()));
        return "StudentClassesList";
    }
}
