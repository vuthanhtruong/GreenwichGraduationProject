package com.example.demo.classes.minorClasses.controller;

import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.classes.minorClasses.service.MinorClassesService;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import com.example.demo.user.deputyStaff.service.DeputyStaffsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/deputy-staff-home/minor-classes-list")
@PreAuthorize("hasRole('DEPUTY_STAFF')")
public class DeleteMinorClassController {
    private final MinorClassesService classesService;
    private final DeputyStaffsService deputyStaffsService;

    @Autowired
    public DeleteMinorClassController(MinorClassesService classesService, DeputyStaffsService deputyStaffsService) {
        this.classesService = classesService;
        this.deputyStaffsService = deputyStaffsService;
    }

    @DeleteMapping("/delete-class/{id}")
    public String deleteClass(
            @PathVariable("id") String classId,
            @RequestParam(value = "source", required = false, defaultValue = "list") String source,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            RedirectAttributes redirectAttributes) {
        DeputyStaffs user = deputyStaffsService.getDeputyStaff();
        if (user == null) {
            redirectAttributes.addFlashAttribute("errors", List.of("Only deputy staff members can delete classes."));
            if (source.equals("search")) {
                redirectAttributes.addFlashAttribute("searchType", searchType);
                redirectAttributes.addFlashAttribute("keyword", keyword);
                redirectAttributes.addFlashAttribute("page", page);
                redirectAttributes.addFlashAttribute("pageSize", pageSize);
                return "redirect:/deputy-staff-home/minor-classes-list/search-classes";
            }
            redirectAttributes.addFlashAttribute("page", page);
            redirectAttributes.addFlashAttribute("pageSize", pageSize);
            return "redirect:/deputy-staff-home/minor-classes-list";
        }

        MinorClasses existingClass = classesService.getClassById(classId);
        if (existingClass == null) {
            redirectAttributes.addFlashAttribute("errors", List.of("Class not found."));
            if (source.equals("search")) {
                redirectAttributes.addFlashAttribute("searchType", searchType);
                redirectAttributes.addFlashAttribute("keyword", keyword);
                redirectAttributes.addFlashAttribute("page", page);
                redirectAttributes.addFlashAttribute("pageSize", pageSize);
                return "redirect:/deputy-staff-home/minor-classes-list/search-classes";
            }
            redirectAttributes.addFlashAttribute("page", page);
            redirectAttributes.addFlashAttribute("pageSize", pageSize);
            return "redirect:/deputy-staff-home/minor-classes-list";
        }

        try {
            classesService.deleteClass(classId);
            redirectAttributes.addFlashAttribute("successMessage", "Class deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errors", List.of("Failed to delete class: " + e.getMessage()));
        }

        if (source.equals("search")) {
            redirectAttributes.addFlashAttribute("searchType", searchType);
            redirectAttributes.addFlashAttribute("keyword", keyword);
            redirectAttributes.addFlashAttribute("page", page);
            redirectAttributes.addFlashAttribute("pageSize", pageSize);
            return "redirect:/deputy-staff-home/minor-classes-list/search-classes";
        }
        redirectAttributes.addFlashAttribute("page", page);
        redirectAttributes.addFlashAttribute("pageSize", pageSize);
        return "redirect:/deputy-staff-home/minor-classes-list";
    }
}