package com.example.demo.submission.controller;

import com.example.demo.submission.service.SpecializedSubmissionsService;
import com.example.demo.user.student.model.Students;
import com.example.demo.user.student.service.StudentsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/classroom")
public class SpecializedSubmissionsController {

    private final StudentsService studentsService;
    private final SpecializedSubmissionsService submissionsService;

    public SpecializedSubmissionsController(StudentsService studentsService, SpecializedSubmissionsService submissionsService) {
        this.studentsService = studentsService;
        this.submissionsService = submissionsService;
    }

    @PostMapping("/submit-specialized-assignment")
    public String submitAssignment(
            @RequestParam("postId") String postId,
            @RequestParam("classId") String classId,
            @RequestParam("files") List<MultipartFile> files,
            HttpSession session,
            RedirectAttributes ra) {

        Students student = studentsService.getStudent();
        if (student == null) {
            ra.addFlashAttribute("errors", List.of("You must be logged in as a student"));
            return "redirect:/classroom";
        }

        try {
            submissionsService.submit(student, postId, files);
            ra.addFlashAttribute("message", "Assignment submitted successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("errors", List.of(e.getMessage()));
        }

        ra.addAttribute("postId", postId);
        ra.addAttribute("classId", classId);
        return "redirect:/classroom/specialized-assignment-detail";
    }
}
