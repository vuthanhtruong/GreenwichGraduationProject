package com.example.demo.student_class.controller;

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
public class AddStudentsToClass {

    private final Students_ClassesService studentsClassesService;
    private final Lecturers_ClassesService lecturersClassesService;
    private final ClassesService classesService;
    private final StudentsService studentsService;
    private final LecturesService lecturersService;
    private final PersonsService personsService;

    @Autowired
    public AddStudentsToClass(Students_ClassesService studentsClassesService,
                              Lecturers_ClassesService lecturersClassesService,
                              ClassesService classesService,
                              StudentsService studentsService,
                              LecturesService lecturersService,
                              PersonsService personsService) {
        this.studentsClassesService = studentsClassesService;
        this.lecturersClassesService = lecturersClassesService;
        this.classesService = classesService;
        this.studentsService = studentsService;
        this.lecturersService = lecturersService;
        this.personsService = personsService;
    }

    @PostMapping("/add-students-to-class")
    public String addStudentsToClass(@RequestParam("classId") String classId,
                                     @RequestParam(value = "studentIds", required = false) List<String> studentIds,
                                     RedirectAttributes redirectAttributes) {
        MajorClasses selectedClass = classesService.getClassById(classId);
        if (selectedClass == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Class not found.");
            return "redirect:/staff-home/classes-list";
        }
        try {
            studentsClassesService.addStudentsToClass(selectedClass, studentIds);
            redirectAttributes.addFlashAttribute("successMessage", "Students added successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/staff-home/classes-list/member-arrangement";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to add students: " + e.getMessage());
        }
        return "redirect:/staff-home/classes-list/member-arrangement";
    }
}
