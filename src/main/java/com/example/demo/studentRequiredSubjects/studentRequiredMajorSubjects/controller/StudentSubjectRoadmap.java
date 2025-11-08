package com.example.demo.studentRequiredSubjects.studentRequiredMajorSubjects.controller;

import com.example.demo.user.student.service.StudentsService;
import com.example.demo.studentRequiredSubjects.studentRequiredMajorSubjects.service.StudentRequiredMajorSubjectsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/student-home/student-subject-roadmap")
public class StudentSubjectRoadmap {
    private final StudentRequiredMajorSubjectsService studentRequiredSubjectsService;
    private final StudentsService  studentsService;

    public StudentSubjectRoadmap(StudentRequiredMajorSubjectsService studentRequiredSubjectsService, StudentsService studentsService) {
        this.studentRequiredSubjectsService = studentRequiredSubjectsService;
        this.studentsService = studentsService;
    }


    @GetMapping("")
    public String StudentSubjectRoadmap(Model model) {
        model.addAttribute("studentRequiredMajorSubjects", studentRequiredSubjectsService.studentMinorRoadmap(studentsService.getStudent()));
        model.addAttribute("studentRequiredMinorSubjects", studentRequiredSubjectsService.studentMinorRoadmap(studentsService.getStudent()));
        return "StudentSubjectRoadmap";
    }
}
