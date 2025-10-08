package com.example.demo.majorSubject.controller;

import com.example.demo.majorSubject.service.MajorSubjectsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/staff-home/major-subjects-list")
@PreAuthorize("hasRole('STAFF')")
public class DeleteMajorSubjectController {

    private static final Logger logger = LoggerFactory.getLogger(DeleteMajorSubjectController.class);

    private final MajorSubjectsService subjectsService;

    @Autowired
    public DeleteMajorSubjectController(MajorSubjectsService subjectsService) {
        this.subjectsService = subjectsService;
    }

    @DeleteMapping("/delete-major-subject")
    public String deleteSubject(
            @RequestParam String id,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false, defaultValue = "list") String source,
            RedirectAttributes redirectAttributes) {
        if (id == null || id.trim().isEmpty()) {
            logger.warn("Invalid subject ID for deletion: {}", id);
            redirectAttributes.addFlashAttribute("errors", Map.of("general", "Subject ID cannot be empty"));
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

        try {
            subjectsService.deleteSubject(id);
            redirectAttributes.addFlashAttribute("message", "Subject deleted successfully!");
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
        } catch (IllegalArgumentException e) {
            logger.warn("Subject not found for ID: {}", id);
            redirectAttributes.addFlashAttribute("errors", Map.of("general", e.getMessage()));
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
            logger.error("Error deleting subject with ID {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errors", Map.of("general", "Error deleting subject: " + e.getMessage()));
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