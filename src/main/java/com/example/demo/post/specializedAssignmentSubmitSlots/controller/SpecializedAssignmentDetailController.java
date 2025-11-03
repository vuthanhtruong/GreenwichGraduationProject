// com.example.demo.post.specializedAssignmentSubmitSlots.controller.SpecializedAssignmentDetailController.java
package com.example.demo.post.specializedAssignmentSubmitSlots.controller;

import com.example.demo.entity.Enums.Grades;
import com.example.demo.post.specializedAssignmentSubmitSlots.model.SpecializedAssignmentSubmitSlots;
import com.example.demo.post.specializedAssignmentSubmitSlots.service.SpecializedAssignmentSubmitSlotsService;
import com.example.demo.submission.model.SpecializedSubmissions;
import com.example.demo.submission.service.SpecializedSubmissionsService;
import com.example.demo.submissionFeedback.service.SpecializedSubmissionFeedbacksService;
import com.example.demo.user.employe.service.EmployesService;
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
    private final EmployesService employesService;
    private final SpecializedSubmissionFeedbacksService feedbackService;

    public SpecializedAssignmentDetailController(
            SpecializedAssignmentSubmitSlotsService slotService,
            StudentsService studentsService,
            SpecializedSubmissionsService submissionsService,
            EmployesService employesService,
            SpecializedSubmissionFeedbacksService feedbackService) {
        this.slotService = slotService;
        this.studentsService = studentsService;
        this.submissionsService = submissionsService;
        this.employesService = employesService;
        this.feedbackService = feedbackService;
    }

    @GetMapping("/specialized-assignment-detail")
    public String getDetail(
            @RequestParam("postId") String postId,
            @RequestParam("classId") String classId,
            HttpSession session,
            Model model) {
        return handleDetail(postId, classId, session, model);
    }

    @PostMapping("/specialized-assignment-detail")
    public String postDetail(
            @RequestParam("postId") String postId,
            @RequestParam("classId") String classId,
            HttpSession session,
            Model model) {
        return handleDetail(postId, classId, session, model);
    }

    private String handleDetail(String postId, String classId, HttpSession session, Model model) {
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

        if (isStudent) {
            // Sinh viên: chỉ xem bài nộp của mình
            SpecializedSubmissions submission = submissionsService.getByStudentAndSlot(student.getId(), postId);
            model.addAttribute("submission", submission);
        } else if (employesService.getMajorEmployee() != null) {
            // Giảng viên: xem tất cả
            List<SpecializedSubmissions> allSubs = submissionsService.getSubmissionsByAssignment(postId);
            List<SpecializedSubmissions> graded = feedbackService.getSubmissionsWithGrade(postId);
            List<SpecializedSubmissions> ungraded = feedbackService.getSubmissionsWithoutGrade(postId);
            List<Students> notSubmitted = submissionsService.getStudentsNotSubmitted(classId, postId);

            model.addAttribute("graded", graded);
            model.addAttribute("ungraded", ungraded);
            model.addAttribute("notSubmitted", notSubmitted);
            model.addAttribute("grades", Grades.values());
            model.addAttribute("currentLecturerId", employesService.getMajorEmployee().getId());
        }

        model.addAttribute("assignment", assignment);
        model.addAttribute("classId", classId);
        model.addAttribute("isStudent", isStudent);
        model.addAttribute("isPastDeadline", isPastDeadline);

        return isStudent ? "SpecializedStudentAssignmentDetail" : "SpecializedAssignmentDetail";
    }
}