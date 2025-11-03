// com.example.demo.post.majorAssignmentSubmitSlots.controller.AssignmentDetailController
package com.example.demo.post.majorAssignmentSubmitSlots.controller;

import com.example.demo.entity.Enums.Grades;
import com.example.demo.post.majorAssignmentSubmitSlots.model.AssignmentSubmitSlots;
import com.example.demo.post.majorAssignmentSubmitSlots.service.AssignmentSubmitSlotsService;
import com.example.demo.submission.model.Submissions;
import com.example.demo.submissionFeedback.service.SubmissionFeedbacksService;
import com.example.demo.submission.service.SubmissionsService;
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
public class AssignmentDetailController {

    private final AssignmentSubmitSlotsService assignmentSubmitSlotsService;
    private final StudentsService studentsService;
    private final SubmissionsService submissionsService;
    private final EmployesService employesService;
    private final SubmissionFeedbacksService submissionFeedbacksService;

    public AssignmentDetailController(
            AssignmentSubmitSlotsService assignmentSubmitSlotsService,
            StudentsService studentsService,
            SubmissionsService submissionsService,
            EmployesService employesService,
            SubmissionFeedbacksService submissionFeedbacksService) {
        this.assignmentSubmitSlotsService = assignmentSubmitSlotsService;
        this.studentsService = studentsService;
        this.submissionsService = submissionsService;
        this.employesService = employesService;
        this.submissionFeedbacksService = submissionFeedbacksService;
    }

    @GetMapping("/assignment-detail")
    public String getAssignmentDetail(
            @RequestParam("postId") String postId,
            @RequestParam("classId") String classId,
            HttpSession session,
            Model model) {
        return handleAssignmentDetail(postId, classId, session, model);
    }

    @PostMapping("/assignment-detail")
    public String postAssignmentDetail(
            @RequestParam("postId") String postId,
            @RequestParam("classId") String classId,
            HttpSession session,
            Model model) {
        return handleAssignmentDetail(postId, classId, session, model);
    }

    private String handleAssignmentDetail(String postId, String classId, HttpSession session, Model model) {
        session.setAttribute("classId", classId);

        AssignmentSubmitSlots assignment = assignmentSubmitSlotsService.findByPostId(postId);
        if (assignment == null) {
            model.addAttribute("errors", List.of("Assignment not found"));
            return "redirect:/classroom";
        }

        Students student = studentsService.getStudent();
        boolean isStudent = student != null;

        boolean isPastDeadline = assignment.getDeadline() != null
                && LocalDateTime.now().isAfter(assignment.getDeadline());

        if (isStudent) {
            Submissions submission = submissionsService.getSubmissionByStudentAndAssignment(student.getId(), postId);
            model.addAttribute("submission", submission);
        } else if (employesService.getMajorEmployee() != null) {
            // LẤY TẤT CẢ BÀI NỘP
            List<Submissions> allSubmissions = submissionsService.getSubmissionsByAssignment(postId);

            // PHÂN LOẠI: ĐÃ CHẤM & CHƯA CHẤM
            List<Submissions> graded = submissionFeedbacksService.getSubmissionsWithGrade(postId);
            List<Submissions> ungraded = submissionFeedbacksService.getSubmissionsWithoutGrade(postId);

            // DANH SÁCH CHƯA NỘP (không thay đổi)
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

        return isStudent ? "StudentAssignmentDetail" : "AssignmentDetail";
    }

}