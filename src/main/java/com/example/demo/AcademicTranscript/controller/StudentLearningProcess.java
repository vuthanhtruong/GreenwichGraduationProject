package com.example.demo.AcademicTranscript.controller;

import com.example.demo.AcademicTranscript.service.AcademicTranscriptsService;
import com.example.demo.student.service.StudentsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/student-home/learning-process")
public class StudentLearningProcess {
    private final AcademicTranscriptsService academicTranscriptsService;
    private final StudentsService studentsService;

    public StudentLearningProcess(AcademicTranscriptsService academicTranscriptsService, StudentsService studentsService) {
        this.academicTranscriptsService = academicTranscriptsService;
        this.studentsService = studentsService;
    }

    @GetMapping("")
    public String studentLearningProcess(Model model) {
        model.addAttribute("studentMajorLearningProcess", academicTranscriptsService.getMajorAcademicTranscripts(studentsService.getStudent()));
        model.addAttribute("studentMinorLearningProcess", academicTranscriptsService.getMinorAcademicTranscripts(studentsService.getStudent()));
        return "StudentLearningProcess";
    }
}
