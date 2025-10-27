package com.example.demo.subject.minorSubject.controller;

import com.example.demo.subject.minorSubject.model.MinorSubjects;
import com.example.demo.subject.minorSubject.service.MinorSubjectsService;
import com.example.demo.user.deputyStaff.service.DeputyStaffsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/deputy-staff-home/minor-subjects-list")
@PreAuthorize("hasRole('DEPUTY_STAFF')")
public class EditMinorSubjectController {

    private final MinorSubjectsService subjectsService;
    private final DeputyStaffsService deputyStaffsService;

    @Autowired
    public EditMinorSubjectController(MinorSubjectsService subjectsService, DeputyStaffsService deputyStaffsService) {
        this.subjectsService = subjectsService;
        this.deputyStaffsService = deputyStaffsService;
    }

    @PostMapping("/edit-minor-subject-form")
    public String showEditSubjectForm(
            @RequestParam String id,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false, defaultValue = "list") String source,
            Model model) {
        MinorSubjects subject = subjectsService.getSubjectById(id);
        if (subject == null) {
            if (source.equals("search")) {
                return "redirect:/deputy-staff-home/minor-subjects-list/search-subjects?error=Subject+not+found" +
                        "&searchType=" + (searchType != null ? searchType : "") +
                        "&keyword=" + (keyword != null ? keyword : "") +
                        "&page=" + page +
                        "&pageSize=" + (pageSize != null ? pageSize : 20);
            }
            return "redirect:/deputy-staff-home/minor-subjects-list?error=Subject+not+found" +
                    "&page=" + page +
                    "&pageSize=" + (pageSize != null ? pageSize : 20);
        }
        model.addAttribute("subject", subject);
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);
        model.addAttribute("page", page);
        model.addAttribute("pageSize", pageSize != null ? pageSize : 20);
        model.addAttribute("source", source);
        return "EditMinorSubjectForm";
    }

    @PutMapping("/edit-minor-subject-form")
    public String updateSubject(
            @Valid @ModelAttribute("subject") MinorSubjects subject,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "source", required = false, defaultValue = "list") String source,
            RedirectAttributes redirectAttributes,
            Model model) {
        // Set creator from deputy staff
        subject.setCreator(deputyStaffsService.getDeputyStaff());

        Map<String, String> errors = subjectsService.validateSubject(subject);
        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("searchType", searchType);
            model.addAttribute("keyword", keyword);
            model.addAttribute("page", page);
            model.addAttribute("pageSize", pageSize != null ? pageSize : 20);
            model.addAttribute("source", source);
            return "EditMinorSubjectForm";
        }

        try {
            MinorSubjects existingSubject = subjectsService.getSubjectById(subject.getSubjectId());
            if (existingSubject == null) {
                redirectAttributes.addFlashAttribute("error", "Subject with ID " + subject.getSubjectId() + " not found.");
                if (source.equals("search")) {
                    redirectAttributes.addFlashAttribute("searchType", searchType);
                    redirectAttributes.addFlashAttribute("keyword", keyword);
                    redirectAttributes.addFlashAttribute("page", page);
                    redirectAttributes.addFlashAttribute("pageSize", pageSize);
                    return "redirect:/deputy-staff-home/minor-subjects-list/search-subjects";
                }
                redirectAttributes.addFlashAttribute("page", page);
                redirectAttributes.addFlashAttribute("pageSize", pageSize);
                return "redirect:/deputy-staff-home/minor-subjects-list";
            }

            subjectsService.editSubject(subject.getSubjectId(), subject);
            redirectAttributes.addFlashAttribute("successMessage", "Subject updated successfully!");
            if (source.equals("search")) {
                redirectAttributes.addFlashAttribute("searchType", searchType);
                redirectAttributes.addFlashAttribute("keyword", keyword);
                redirectAttributes.addFlashAttribute("page", page);
                redirectAttributes.addFlashAttribute("pageSize", pageSize);
                return "redirect:/deputy-staff-home/minor-subjects-list/search-subjects";
            }
            redirectAttributes.addFlashAttribute("page", page);
            redirectAttributes.addFlashAttribute("pageSize", pageSize);
            return "redirect:/deputy-staff-home/minor-subjects-list";
        } catch (Exception e) {
            Map<String, String> errorsCatch = new HashMap<>();
            errorsCatch.put("general", "Error updating subject: " + e.getMessage());
            model.addAttribute("errors", errorsCatch);
            model.addAttribute("searchType", searchType);
            model.addAttribute("keyword", keyword);
            model.addAttribute("page", page);
            model.addAttribute("pageSize", pageSize != null ? pageSize : 20);
            model.addAttribute("source", source);
            return "EditMinorSubjectForm";
        }
    }
}