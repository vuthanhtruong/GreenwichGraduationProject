// src/main/java/com/example/demo/academicTranscript/controller/StaffAcademicTranscriptController.java
package com.example.demo.academicTranscript.controller;

import com.example.demo.academicTranscript.service.AcademicTranscriptsService;
import com.example.demo.user.student.model.Students;
import com.example.demo.user.student.service.StudentsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/staff-transcript")
public class StaffAcademicTranscriptController {

    private final AcademicTranscriptsService academicTranscriptsService;
    private final StudentsService studentsService;

    public StaffAcademicTranscriptController(
            AcademicTranscriptsService academicTranscriptsService,
            StudentsService studentsService) {
        this.academicTranscriptsService = academicTranscriptsService;
        this.studentsService = studentsService;
    }

    @PostMapping
    public String viewTranscriptPost(
            @RequestParam String studentId,
            HttpSession session) {

        session.setAttribute("transcript_studentId", studentId);
        return "redirect:/staff-transcript";
    }

    @GetMapping
    public String viewTranscriptGet(
            Model model,
            HttpSession session) {

        String studentId = (String) session.getAttribute("transcript_studentId");

        if (studentId == null) {
            model.addAttribute("error", "No student selected.");
            return "redirect:/staff-home/students-list";
        }

        Students student = studentsService.getStudentById(studentId);

        model.addAttribute("student", student);
        model.addAttribute("specializedAcademicTranscripts",
                academicTranscriptsService.getSpecializedAcademicTranscripts(student));
        model.addAttribute("majorAcademicTranscripts",
                academicTranscriptsService.getMajorAcademicTranscripts(student));
        model.addAttribute("minorAcademicTranscripts",
                academicTranscriptsService.getMinorAcademicTranscripts(student));
        model.addAttribute("home", "/staff-home");
        model.addAttribute("list", "/staff-home/students-list");

        return "AcademicTranscript";
    }
}