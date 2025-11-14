package com.example.demo.subject.majorSubject.controller;

import com.example.demo.curriculum.model.Curriculum;
import com.example.demo.curriculum.service.CurriculumService;
import com.example.demo.user.staff.service.StaffsService;
import com.example.demo.subject.majorSubject.model.MajorSubjects;
import com.example.demo.subject.majorSubject.service.MajorSubjectsService;
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
@RequestMapping("/staff-home/major-subjects-list")
@PreAuthorize("hasRole('STAFF')")
public class EditMajorSubjectController {

    private final MajorSubjectsService subjectsService;
    private final CurriculumService curriculumService;
    private final StaffsService staffsService;

    @Autowired
    public EditMajorSubjectController(MajorSubjectsService subjectsService,
                                      CurriculumService curriculumService,
                                      StaffsService staffsService) {
        this.subjectsService = subjectsService;
        this.curriculumService = curriculumService;
        this.staffsService = staffsService;
    }

    @PostMapping("/edit-major-subject-form")
    public String showEditSubjectForm(
            @RequestParam String id,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false, defaultValue = "list") String source,
            Model model) {
        MajorSubjects subject = subjectsService.getSubjectById(id);
        if (subject == null) {
            if (source.equals("search")) {
                return "redirect:/staff-home/major-subjects-list/search-subjects?error=Subject+not+found" +
                        "&searchType=" + (searchType != null ? searchType : "") +
                        "&keyword=" + (keyword != null ? keyword : "") +
                        "&page=" + page +
                        "&pageSize=" + (pageSize != null ? pageSize : 20);
            }
            return "redirect:/staff-home/major-subjects-list?error=Subject+not+found" +
                    "&page=" + page +
                    "&pageSize=" + (pageSize != null ? pageSize : 20);
        }
        model.addAttribute("subject", subject);
        model.addAttribute("curriculums", curriculumService.getCurriculums());
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);
        model.addAttribute("page", page);
        model.addAttribute("pageSize", pageSize != null ? pageSize : 20);
        model.addAttribute("source", source);
        return "EditMajorSubjectForm";
    }

    @PutMapping("/edit-major-subject-form")
    public String updateSubject(
            @Valid @ModelAttribute("subject") MajorSubjects subject,
            @RequestParam(value = "curriculumId") String curriculumId,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "source", required = false, defaultValue = "list") String source,
            RedirectAttributes redirectAttributes,
            Model model) {
        // Set curriculum from ID
        if (curriculumId != null && !curriculumId.isEmpty()) {
            Curriculum curriculum = curriculumService.getCurriculumById(curriculumId);
            if (curriculum != null) {
                subject.setCurriculum(curriculum);
            }
        }
        // Set major from staff
        subject.setMajor(staffsService.getStaffMajor());

        Map<String, String> errors = subjectsService.validateSubject(subject, curriculumId);
        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("curriculumId", curriculumId);
            model.addAttribute("curriculums", curriculumService.getCurriculums());
            model.addAttribute("searchType", searchType);
            model.addAttribute("keyword", keyword);
            model.addAttribute("page", page);
            model.addAttribute("pageSize", pageSize != null ? pageSize : 20);
            model.addAttribute("source", source);
            return "EditMajorSubjectForm";
        }

        try {
            MajorSubjects existingSubject = subjectsService.getSubjectById(subject.getSubjectId());
            if (existingSubject == null) {
                redirectAttributes.addFlashAttribute("error", "Subject with ID " + subject.getSubjectId() + " not found.");
                if (source.equals("search")) {
                    redirectAttributes.addFlashAttribute("searchType", searchType);
                    redirectAttributes.addFlashAttribute("keyword", keyword);
                    redirectAttributes.addFlashAttribute("page", page);
                    redirectAttributes.addFlashAttribute("pageSize", pageSize);
                    return "redirect:/staff-home/major-subjects-list/search-subjects";
                }
                redirectAttributes.addFlashAttribute("page", page);
                redirectAttributes.addFlashAttribute("pageSize", pageSize);
                return "redirect:/staff-home/major-subjects-list";
            }

            subjectsService.editSubject(subject.getSubjectId(), subject);
            redirectAttributes.addFlashAttribute("successMessage", "Subject updated successfully!");
            if (source.equals("search")) {
                redirectAttributes.addFlashAttribute("searchType", searchType);
                redirectAttributes.addFlashAttribute("keyword", keyword);
                redirectAttributes.addFlashAttribute("page", page);
                redirectAttributes.addFlashAttribute("pageSize", pageSize);
                return "redirect:/staff-home/major-subjects-list/search-subjects";
            }
            redirectAttributes.addFlashAttribute("page", page);
            redirectAttributes.addFlashAttribute("pageSize", pageSize);
            return "redirect:/staff-home/major-subjects-list";
        } catch (Exception e) {
            Map<String, String> errorsCatch = new HashMap<>();
            errorsCatch.put("general", "Error updating subject: " + e.getMessage());
            model.addAttribute("errors", errorsCatch);
            model.addAttribute("curriculums", curriculumService.getCurriculums());
            model.addAttribute("searchType", searchType);
            model.addAttribute("keyword", keyword);
            model.addAttribute("page", page);
            model.addAttribute("pageSize", pageSize != null ? pageSize : 20);
            model.addAttribute("source", source);
            return "EditMajorSubjectForm";
        }
    }
}