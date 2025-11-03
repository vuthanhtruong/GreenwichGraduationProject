// com.example.demo.submissionFeedback.controller.SpecializedSubmissionFeedbackController.java
package com.example.demo.submissionFeedback.controller;

import com.example.demo.entity.Enums.Grades;
import com.example.demo.submissionFeedback.service.SpecializedSubmissionFeedbacksService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/classroom")
public class SpecializedSubmissionFeedbackController {

    private final SpecializedSubmissionFeedbacksService feedbackService;

    public SpecializedSubmissionFeedbackController(SpecializedSubmissionFeedbacksService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping("/specialized-save-feedback")
    public String saveFeedback(
            @RequestParam String postId,
            @RequestParam String classId,
            @RequestParam String submittedBy,
            @RequestParam String assignmentSlotId,
            @RequestParam String announcerId,
            @RequestParam(required = false) Grades grade,
            @RequestParam(required = false) String content,
            RedirectAttributes redirectAttrs) {

        try {
            feedbackService.saveFeedback(submittedBy, assignmentSlotId, announcerId, content, grade);
            redirectAttrs.addFlashAttribute("success", "Chấm điểm thành công!");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        return "redirect:/classroom/specialized-assignment-detail?postId=" + postId + "&classId=" + classId;
    }
}