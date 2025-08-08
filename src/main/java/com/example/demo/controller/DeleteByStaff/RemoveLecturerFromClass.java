package com.example.demo.controller.DeleteByStaff;


import com.example.demo.entity.Classes;
import com.example.demo.service.ClassesService;
import com.example.demo.service.Lecturers_ClassesService;
import com.example.demo.service.LecturesService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/staff-home/classes-list")
@PreAuthorize("hasRole('STAFF')")
public class RemoveLecturerFromClass {
    private  final ClassesService classesService;
    private  final LecturesService  lecturesService;
    private final Lecturers_ClassesService lecturersClassesService;

    public RemoveLecturerFromClass(ClassesService classesService, LecturesService lecturesService, Lecturers_ClassesService lecturersClassesService) {
        this.classesService = classesService;
        this.lecturesService = lecturesService;

        this.lecturersClassesService = lecturersClassesService;
    }


    @PostMapping("/member-arrangement/delete-lecturer-from-class")
    public String deleteLecturerFromClass(@RequestParam("classId") String classId,
                                          @RequestParam(value = "lecturerIds", required = false) List<String> lecturerIds,
                                          RedirectAttributes redirectAttributes,
                                          HttpSession session) {
        // Validate classId
        Classes selectedClass = classesService.getClassById(classId);
        if (selectedClass == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Class not found.");
            return "redirect:/staff-home/classes-list";
        }

        // Validate lecturerIds
        if (lecturerIds == null || lecturerIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "No lecturers selected for removal.");
            session.setAttribute("currentClassId", classId);
            return "redirect:/staff-home/classes-list/member-arrangement";
        }

        // Remove lecturers from the class
        try {
            lecturersClassesService.removeLecturerFromClass(selectedClass, lecturerIds);
            redirectAttributes.addFlashAttribute("successMessage", "Selected lecturers have been removed from the class.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to remove lecturers: " + e.getMessage());
        }
        // Keep the current class in session
        session.setAttribute("currentClassId", classId);
        return "redirect:/staff-home/classes-list/member-arrangement";
    }
}
