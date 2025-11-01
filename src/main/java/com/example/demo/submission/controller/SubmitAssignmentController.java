package com.example.demo.submission.controller;

import com.example.demo.submission.service.SubmissionsService;
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
public class SubmitAssignmentController {
    private final StudentsService studentsService;
    private final SubmissionsService submissionsService;

    public SubmitAssignmentController( StudentsService studentsService, SubmissionsService submissionsService) {
        this.studentsService = studentsService;
        this.submissionsService = submissionsService;
    }

    @PostMapping("/submit-assignment")
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
            submissionsService.submitAssignment(student, postId, files);
            ra.addFlashAttribute("message", "Assignment submitted successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("errors", List.of(e.getMessage()));
        }

        ra.addAttribute("postId", postId);
        ra.addAttribute("classId", classId);
        return "redirect:/classroom/assignment-detail";
    }
}
