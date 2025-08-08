package com.example.demo.controller.DeleteByStaff;


import com.example.demo.entity.MajorClasses;
import com.example.demo.entity.Staffs;
import com.example.demo.service.ClassesService;
import com.example.demo.service.StaffsService;
import com.example.demo.service.MajorSubjectsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/staff-home/classes-list")
public class DeleteClassController {
    private final StaffsService staffsService;
    private final MajorSubjectsService subjectsService;
    private final ClassesService classesService;

    @Autowired
    public DeleteClassController(ClassesService classesService, StaffsService staffsService, MajorSubjectsService subjectsService) {
        this.classesService = classesService;
        this.staffsService = staffsService;
        this.subjectsService = subjectsService;
    }

    @DeleteMapping("/delete-class/{id}")
    public String deleteClass(
            @PathVariable("id") String classId,
            RedirectAttributes redirectAttributes) {
        // Security check

        Staffs user = staffsService.getStaff();
        if (!(user instanceof Staffs)) {
            redirectAttributes.addFlashAttribute("errors", List.of("Only staff members can delete classes."));
            return "redirect:/staff-home/classes-list";
        }

        MajorClasses existingClass = classesService.getClassById(classId);
        if (existingClass == null) {
            redirectAttributes.addFlashAttribute("errors", List.of("Class not found."));
            return "redirect:/staff-home/classes-list";
        }

        try {
            classesService.deleteClass(classId);
            redirectAttributes.addFlashAttribute("successMessage", "Class deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errors", List.of("Failed to delete class: " + e.getMessage()));
        }
        return "redirect:/staff-home/classes-list";
    }
}
