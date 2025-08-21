package com.example.demo.subject.controller;

import com.example.demo.subject.service.MajorSubjectsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/staff-home")
@PreAuthorize("hasRole('STAFF')")
public class DeleteMajorSubjectController {
    private final MajorSubjectsService subjectsService;

    @Autowired
    public DeleteMajorSubjectController(MajorSubjectsService subjectsService) {
        this.subjectsService = subjectsService;
    }
    @DeleteMapping("/major-subjects-list/delete-subject/{id}")
    public String deleteSubject(
            @PathVariable("id") String id,
            RedirectAttributes redirectAttributes) {
        try {
            subjectsService.deleteSubject(id);
            redirectAttributes.addFlashAttribute("successMessage", "Subject deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete subject: " + e.getMessage());
        }
        return "redirect:/staff-home/major-subjects-list";
    }

}
