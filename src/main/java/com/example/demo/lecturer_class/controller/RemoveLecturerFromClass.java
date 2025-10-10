package com.example.demo.lecturer_class.controller;


import com.example.demo.classes.model.MajorClasses;
import com.example.demo.classes.service.ClassesService;
import com.example.demo.lecturer_class.service.Lecturers_ClassesService;
import com.example.demo.lecturer.service.LecturesService;
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
    private final Lecturers_ClassesService lecturersClassesService;

    public RemoveLecturerFromClass(ClassesService classesService, Lecturers_ClassesService lecturersClassesService) {
        this.classesService = classesService;

        this.lecturersClassesService = lecturersClassesService;
    }
}
