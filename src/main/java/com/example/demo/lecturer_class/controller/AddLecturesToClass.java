package com.example.demo.lecturer_class.controller;

import com.example.demo.classes.model.MajorClasses;
import com.example.demo.classes.service.ClassesService;
import com.example.demo.lecturer.model.MajorLecturers;
import com.example.demo.lecturer_class.service.Lecturers_ClassesService;
import com.example.demo.student.model.Students;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
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
}