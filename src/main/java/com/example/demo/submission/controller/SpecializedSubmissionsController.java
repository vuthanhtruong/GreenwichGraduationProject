// src/main/java/com/example/demo/submission/controller/SpecializedSubmissionsController.java

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
    private final SpecializedSubmissionsService specializedSubmissionsService;

    public SpecializedSubmissionsController(StudentsService studentsService, SpecializedSubmissionsService specializedSubmissionsService) {
        this.studentsService = studentsService;
        this.specializedSubmissionsService = specializedSubmissionsService;
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
            specializedSubmissionsService.submit(student, postId, files);
            ra.addFlashAttribute("message", "Assignment submitted successfully!");

            // LƯU VÀO SESSION
            session.setAttribute("specializedClassId", classId);
            session.setAttribute("specializedPostId", postId);
        } catch (Exception e) {
            ra.addFlashAttribute("errors", List.of(e.getMessage()));
        }

        return "redirect:/classroom/specialized-assignment-detail"; // SẠCH
    }

    @PostMapping("/delete-specialized-submission")
    public String deleteSpecializedSubmission(
            @RequestParam String studentId,
            @RequestParam String slotId,
            HttpSession session) {

        specializedSubmissionsService.deleteByStudentAndSlot(studentId, slotId);
        return "redirect:/classroom/specialized-assignment-detail"; // SẠCH
    }
}