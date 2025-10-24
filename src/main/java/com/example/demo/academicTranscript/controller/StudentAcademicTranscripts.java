package com.example.demo.academicTranscript.controller;

import com.example.demo.academicTranscript.service.AcademicTranscriptsService;
import com.example.demo.user.student.service.StudentsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/student-home/transcript")
public class StudentAcademicTranscripts {
    private final AcademicTranscriptsService academicTranscriptsService;
    private final StudentsService studentsService;

    public StudentAcademicTranscripts(AcademicTranscriptsService academicTranscriptsService, StudentsService studentsService) {
        this.academicTranscriptsService = academicTranscriptsService;
        this.studentsService = studentsService;
    }

    @GetMapping("")
    public String getStudentAcademicTranscripts(Model model) {
        model.addAttribute("student", studentsService.getStudent());
        model.addAttribute("SpecializedAcademicTranscripts", academicTranscriptsService.getSpecializedAcademicTranscripts(studentsService.getStudent()));
        model.addAttribute("MajorAcademicTranscripts", academicTranscriptsService.getMajorAcademicTranscripts(studentsService.getStudent()));
        model.addAttribute("MinorAcademicTranscripts", academicTranscriptsService.getMinorAcademicTranscripts(studentsService.getStudent()));
        return "AcademicTranscript";
    }
}
