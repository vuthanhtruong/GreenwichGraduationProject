package com.example.demo.classes.specializedClasses.controller;

import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.classes.specializedClasses.service.SpecializedClassesService;
import com.example.demo.user.staff.model.Staffs;
import com.example.demo.user.staff.service.StaffsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/staff-home/specialized-classes-list")
public class DeleteSpecializedClassController {

    private final SpecializedClassesService classesService;
    private final StaffsService staffsService;

    @Autowired
    public DeleteSpecializedClassController(SpecializedClassesService classesService, StaffsService staffsService) {
        this.classesService = classesService;
        this.staffsService = staffsService;
    }

    @DeleteMapping("/delete-class/{id}")
    public String deleteClass(
            @PathVariable("classId") String classId,
            @RequestParam(value = "source", required = false, defaultValue = "list") String source,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            RedirectAttributes redirectAttributes) {
        Staffs user = staffsService.getStaff();
        if (!(user instanceof Staffs)) {
            redirectAttributes.addFlashAttribute("errors", List.of("Only staff members can delete classes."));
            if (source.equals("search")) {
                redirectAttributes.addFlashAttribute("searchType", searchType);
                redirectAttributes.addFlashAttribute("keyword", keyword);
                redirectAttributes.addFlashAttribute("page", page);
                redirectAttributes.addFlashAttribute("pageSize", pageSize);
                return "redirect:/staff-home/specialized-classes-list/search-classes";
            }
            redirectAttributes.addFlashAttribute("page", page);
            redirectAttributes.addFlashAttribute("pageSize", pageSize);
            return "redirect:/staff-home/specialized-classes-list";
        }

        SpecializedClasses existingClass = classesService.getClassById(classId);
        if (existingClass == null) {
            redirectAttributes.addFlashAttribute("errors", List.of("Class not found."));
            if (source.equals("search")) {
                redirectAttributes.addFlashAttribute("searchType", searchType);
                redirectAttributes.addFlashAttribute("keyword", keyword);
                redirectAttributes.addFlashAttribute("page", page);
                redirectAttributes.addFlashAttribute("pageSize", pageSize);
                return "redirect:/staff-home/specialized-classes-list/search-classes";
            }
            redirectAttributes.addFlashAttribute("page", page);
            redirectAttributes.addFlashAttribute("pageSize", pageSize);
            return "redirect:/staff-home/specialized-classes-list";
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
            return "redirect:/staff-home/specialized-classes-list/search-classes";
        }
        redirectAttributes.addFlashAttribute("page", page);
        redirectAttributes.addFlashAttribute("pageSize", pageSize);
        return "redirect:/staff-home/specialized-classes-list";
    }
}