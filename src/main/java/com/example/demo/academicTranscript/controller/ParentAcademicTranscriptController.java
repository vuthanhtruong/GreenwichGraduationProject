// src/main/java/com/example/demo/academicTranscript/controller/ParentAcademicTranscriptController.java
package com.example.demo.academicTranscript.controller;

import com.example.demo.academicTranscript.service.AcademicTranscriptsService;
import com.example.demo.user.parentAccount.service.ParentAccountsService;
import com.example.demo.user.student.model.Students;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/parent")
public class ParentAcademicTranscriptController {

    private final ParentAccountsService parentAccountsService;
    private final AcademicTranscriptsService academicTranscriptsService;

    public ParentAcademicTranscriptController(ParentAccountsService parentAccountsService,
                                              AcademicTranscriptsService academicTranscriptsService) {
        this.parentAccountsService = parentAccountsService;
        this.academicTranscriptsService = academicTranscriptsService;
    }

    @PostMapping("/transcript")
    public String selectChildForTranscript(
            @RequestParam String studentId,
            HttpSession session,
            Model model) {

        var parent = parentAccountsService.getParent();
        boolean valid = parentAccountsService.getStudentsByParentId(parent.getId()).stream()
                .anyMatch(s -> s.getId().equals(studentId));

        if (!valid) {
            return "redirect:/parent/transcript?error";
        }

        session.setAttribute("transcript_childId", studentId);
        return "redirect:/parent/transcript/view";
    }

    // ===================== 3. Xem bảng điểm của con đã chọn =====================
    @GetMapping("/transcript/view")
    public String viewChildTranscript(Model model, HttpSession session) {

        String childId = (String) session.getAttribute("transcript_childId");
        if (childId == null) {
            return "redirect:/parent/transcript";
        }

        var parent = parentAccountsService.getParent();
        Students child = parentAccountsService.getStudentsByParentId(parent.getId()).stream()
                .filter(s -> s.getId().equals(childId))
                .findFirst()
                .orElse(null);

        if (child == null) {
            session.removeAttribute("transcript_childId");
            return "redirect:/parent/transcript";
        }

        model.addAttribute("student", child);
        model.addAttribute("specializedAcademicTranscripts",
                academicTranscriptsService.getSpecializedAcademicTranscripts(child));
        model.addAttribute("majorAcademicTranscripts",
                academicTranscriptsService.getMajorAcademicTranscripts(child));
        model.addAttribute("minorAcademicTranscripts",
                academicTranscriptsService.getMinorAcademicTranscripts(child));

        // Parent mode + navigation
        model.addAttribute("parentMode", true);
        model.addAttribute("home", "/parent-home");
        model.addAttribute("list", "/parent-home");

        // Cho phép chuyển con nhanh ở đầu trang
        model.addAttribute("siblings", parentAccountsService.getStudentsByParentId(parent.getId()));
        model.addAttribute("currentChildId", childId);

        return "AcademicTranscript"; // Dùng lại template đẹp của staff
    }
}