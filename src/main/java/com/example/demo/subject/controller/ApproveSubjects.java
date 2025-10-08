package com.example.demo.subject.controller;

import com.example.demo.admin.service.AdminsService;
import com.example.demo.subject.model.Subjects;
import com.example.demo.subject.service.SubjectsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin-home/tuition-management/approve-subjects")
@PreAuthorize("hasRole('ADMIN')")
public class ApproveSubjects {

    private final SubjectsService subjectsService;
    private final AdminsService adminsService;

    public ApproveSubjects(SubjectsService subjectsService, AdminsService adminsService) {
        this.subjectsService = subjectsService;
        this.adminsService = adminsService;
    }

    @GetMapping
    public String showUnacceptedSubjects(
            Model model,
            HttpSession session) {
        List<Subjects> unacceptedSubjects = subjectsService.YetAcceptedSubjects();
        model.addAttribute("subjects", unacceptedSubjects);
        model.addAttribute("totalSubjects", unacceptedSubjects.size());
        model.addAttribute("admissionYear", session.getAttribute("admissionYear"));
        if (unacceptedSubjects.isEmpty()) {
            model.addAttribute("errorMessage", "No unaccepted subjects found.");
        }
        return "ApproveSubjects";
    }

    @PostMapping
    public String getUnacceptedSubjects(
            @RequestParam(value = "admissionYear", required = false) Integer admissionYear,
            Model model,
            HttpSession session) {
        if (admissionYear != null) {
            session.setAttribute("admissionYear", admissionYear);
        }
        List<Subjects> unacceptedSubjects = subjectsService.YetAcceptedSubjects();
        model.addAttribute("subjects", unacceptedSubjects);
        model.addAttribute("totalSubjects", unacceptedSubjects.size());
        model.addAttribute("admissionYear", admissionYear);
        if (unacceptedSubjects.isEmpty()) {
            model.addAttribute("errorMessage", "No unaccepted subjects found.");
        }
        return "ApproveSubjects";
    }

    @PostMapping("/approve")
    public String approveSelectedSubjects(
            @RequestParam(value = "subjectIds", required = false) List<String> subjectIds,
            @RequestParam(value = "admissionYear", required = false) Integer admissionYear,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        if (admissionYear != null) {
            session.setAttribute("admissionYear", admissionYear);
        }
        if (subjectIds == null || subjectIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "No subjects selected for approval.");
        } else {
            String acceptorId = adminsService.getAdmin().getId();
            subjectsService.approveSubjects(subjectIds, acceptorId);
            redirectAttributes.addFlashAttribute("successMessage", "Selected subjects have been approved successfully.");
        }
        return "redirect:/admin-home/tuition-management/approve-subjects";
    }
}