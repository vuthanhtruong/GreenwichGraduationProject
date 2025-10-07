package com.example.demo.lecturer_class.controller;

import com.example.demo.classes.model.MajorClasses;
import com.example.demo.classes.service.ClassesService;
import com.example.demo.lecturer.service.LecturesService;
import com.example.demo.lecturer_class.service.Lecturers_ClassesService;
import com.example.demo.person.service.PersonsService;
import com.example.demo.student.service.StudentsService;
import com.example.demo.student_class.service.Students_ClassesService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class AddLecturesToClass {

    private final Lecturers_ClassesService lecturersClassesService;
    private final ClassesService classesService;

    @Autowired
    public AddLecturesToClass(Lecturers_ClassesService lecturersClassesService,
                              ClassesService classesService) {
        this.lecturersClassesService = lecturersClassesService;
        this.classesService = classesService;
    }

    @PostMapping("/add-lecturers-to-class")
    public String addLecturersToClass(@RequestParam("classId") String classId,
                                      @RequestParam(value = "lecturerIds", required = false) List<String> lecturerIds,
                                      RedirectAttributes redirectAttributes) {
        MajorClasses selectedClass = classesService.getClassById(classId);
        if (selectedClass == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Class not found.");
            return "redirect:/staff-home/classes-list";
        }
        try {
            lecturersClassesService.addLecturersToClass(selectedClass, lecturerIds);
            redirectAttributes.addFlashAttribute("successMessage", "Lecturers added successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/staff-home/classes-list/member-arrangement";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to add lecturers: " + e.getMessage());
        }
        return "redirect:/staff-home/classes-list/member-arrangement";
    }
}