package com.example.demo.subject.controller;

import com.example.demo.staff.service.StaffsService;
import com.example.demo.subject.service.MajorSubjectsService;
import com.example.demo.subject.service.SubjectsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/staff-home/major-subjects-list")
@PreAuthorize("hasRole('STAFF')")
public class DeleteMajorSubjectController {

    private final MajorSubjectsService subjectsService;
    private final SubjectsService subjectsService2;
    private final StaffsService staffsService;

    public DeleteMajorSubjectController(MajorSubjectsService subjectsService, SubjectsService subjectsService2, StaffsService staffsService) {
        this.subjectsService = subjectsService;
        this.subjectsService2 = subjectsService2;
        this.staffsService = staffsService;
    }

    @DeleteMapping("/delete-subject/{id}")
    public String deleteSubject(
            @PathVariable String id,
            @RequestParam(required = false, defaultValue = "list") String source,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize,
            RedirectAttributes redirectAttributes) {
        try {
            if (!subjectsService2.existsSubjectById(id)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Subject with ID " + id + " not found.");
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

            subjectsService.deleteSubject(id);
            redirectAttributes.addFlashAttribute("successMessage", "Deleted subject ID: " + id);
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
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting subject: " + e.getMessage());
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
    }
}