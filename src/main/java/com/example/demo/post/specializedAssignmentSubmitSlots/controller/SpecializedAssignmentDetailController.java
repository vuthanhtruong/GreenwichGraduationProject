package com.example.demo.post.specializedAssignmentSubmitSlots.controller;

import com.example.demo.post.specializedAssignmentSubmitSlots.model.SpecializedAssignmentSubmitSlots;
import com.example.demo.post.specializedAssignmentSubmitSlots.service.SpecializedAssignmentSubmitSlotsService;
import com.example.demo.submission.model.SpecializedSubmissions;
import com.example.demo.submission.service.SpecializedSubmissionsService;
import com.example.demo.user.student.model.Students;
import com.example.demo.user.student.service.StudentsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/classroom")
public class SpecializedAssignmentDetailController {

    private final SpecializedAssignmentSubmitSlotsService slotService;
    private final StudentsService studentsService;
    private final SpecializedSubmissionsService submissionsService;

    public SpecializedAssignmentDetailController(
            SpecializedAssignmentSubmitSlotsService slotService,
            StudentsService studentsService,
            SpecializedSubmissionsService submissionsService) {
        this.slotService = slotService;
        this.studentsService = studentsService;
        this.submissionsService = submissionsService;
    }

    // GET: Xem chi tiết (F5, share link)
    @GetMapping("/specialized-assignment-detail")
    public String getAssignmentDetail(
            @RequestParam("postId") String postId,
            @RequestParam("classId") String classId,
            HttpSession session,
            Model model) {

        return handleAssignmentDetail(postId, classId, session, model);
    }

    // POST: Vào từ form ẩn (click assignment)
    @PostMapping("/specialized-assignment-detail")
    public String postAssignmentDetail(
            @RequestParam("postId") String postId,
            @RequestParam("classId") String classId,
            HttpSession session,
            Model model) {

        return handleAssignmentDetail(postId, classId, session, model);
    }

    // HÀM CHUNG
    private String handleAssignmentDetail(String postId, String classId, HttpSession session, Model model) {
        session.setAttribute("classId", classId);

        SpecializedAssignmentSubmitSlots assignment = slotService.findByPostId(postId);
        if (assignment == null) {
            model.addAttribute("errors", List.of("Assignment not found"));
            return "redirect:/classroom";
        }

        Students student = studentsService.getStudent();
        boolean isStudent = student != null;

        boolean isPastDeadline = assignment.getDeadline() != null
                && LocalDateTime.now().isAfter(assignment.getDeadline());

        SpecializedSubmissions submission = null;
        if (isStudent) {
            submission = submissionsService.getByStudentAndSlot(student.getId(), postId);
        }

        model.addAttribute("assignment", assignment);
        model.addAttribute("classId", classId);
        model.addAttribute("isStudent", isStudent);
        model.addAttribute("isPastDeadline", isPastDeadline);
        model.addAttribute("submission", submission);

        return isStudent ? "SpecializedStudentAssignmentDetail" : "SpecializedAssignmentDetail";
    }
}