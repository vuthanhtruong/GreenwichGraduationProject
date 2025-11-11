package com.example.demo.user.minorLecturer.controller;

import com.example.demo.user.minorLecturer.service.MinorLecturersService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/minor-lecturer-home")
public class MinorLecturerController {
    private final MinorLecturersService minorLecturersService;

    public MinorLecturerController(MinorLecturersService minorLecturersService) {
        this.minorLecturersService = minorLecturersService;
    }
    @GetMapping
    public String minorLecturerHome(Model model) {
        model.addAttribute("lecturer",minorLecturersService.getMinorLecturer());
        model.addAttribute("currentCampusName", minorLecturersService.getMinorLecturer().getCampus().getCampusName());
        return "MinorLecturerHome";
    }
}
