package com.example.demo.subject.controller;

import com.example.demo.Curriculum.model.Curriculum;
import com.example.demo.Curriculum.service.CurriculumService;
import com.example.demo.staff.service.StaffsService;
import com.example.demo.subject.model.MajorSubjects;
import com.example.demo.subject.service.MajorSubjectsService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
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
    private final StaffsService staffsService;
    private final CurriculumService curriculumService;

    public EditMajorSubjectController(MajorSubjectsService subjectsService, StaffsService staffsService, CurriculumService curriculumService) {
        this.subjectsService = subjectsService;
        this.staffsService = staffsService;
        this.curriculumService = curriculumService;
    }

    @PostMapping("/edit-major-subject-form")
    public String showEditMajorSubjectForm(
            @RequestParam("id") String id,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(defaultValue = "list") String source,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        MajorSubjects subject = subjectsService.getSubjectById(id);
        if (subject == null) {
            redirectAttributes.addFlashAttribute("message", "Subject not found.");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            if (source.equals("search")) {
                return "redirect:/staff-home/major-subjects-list/search-subjects?searchType=" + (searchType != null ? searchType : "") +
                        "&keyword=" + (keyword != null ? keyword : "") + "&page=" + page + "&pageSize=" + (pageSize != null ? pageSize : 20);
            }
            return "redirect:/staff-home/major-subjects-list?page=" + page + "&pageSize=" + (pageSize != null ? pageSize : 20);
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

    @PutMapping("/edit-major-subject")
    public String updateSubject(
            @Valid @ModelAttribute("subject") MajorSubjects subject,
            @RequestParam(value = "curriculumId", required = false) String curriculumId,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "source", defaultValue = "list") String source,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        Map<String, String> errors = subjectsService.validateSubject(subject, curriculumId);

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("subject", subject);
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
                redirectAttributes.addFlashAttribute("message", "Subject not found.");
                redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
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

            existingSubject.setSubjectName(subject.getSubjectName() != null ? subject.getSubjectName().toUpperCase() : existingSubject.getSubjectName());
            existingSubject.setSemester(subject.getSemester());
            existingSubject.setCurriculum(curriculumService.getCurriculumById(curriculumId));

            subjectsService.editSubject(subject.getSubjectId(), existingSubject);

            redirectAttributes.addFlashAttribute("message", "Subject updated successfully!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
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
            model.addAttribute("subject", subject);
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