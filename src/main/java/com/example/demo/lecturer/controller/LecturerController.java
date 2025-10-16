package com.example.demo.lecturer.controller;

import com.example.demo.lecturer.service.LecturesService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

@Controller
@RequestMapping("/lecturer-home")
public class LecturerController {

    private final LecturesService majorLecturers;

    public LecturerController(LecturesService majorLecturers) {
        this.majorLecturers = majorLecturers;
    }

    @GetMapping
    public String showLecturerHome(Model model) {
        try {
            model.addAttribute("lecturer",majorLecturers.getMajorLecturer());
            return "MajorLecturerHome";
        } catch (Exception e) {
            model.addAttribute("errors", List.of("Error loading lecturer home: " + e.getMessage()));
            model.addAttribute("specialization", "N/A");
            model.addAttribute("unreadMessages", 0);
            return "MajorLecturerHome";
        }
    }
}