package com.example.demo.majorLecturers_Specializations.controller;

import com.example.demo.majorLecturers_Specializations.service.MajorLecturersSpecializationsService;
import com.example.demo.user.majorLecturer.service.MajorLecturersService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/major-lecturer-home/your-specialized")
public class ListLecturerSpecializationController {
    private final MajorLecturersSpecializationsService majorLecturersSpecializationsService;
    private final MajorLecturersService majorLecturersService;

    public ListLecturerSpecializationController(MajorLecturersSpecializationsService majorLecturersSpecializationsService, MajorLecturersService majorLecturersService) {
        this.majorLecturersSpecializationsService = majorLecturersSpecializationsService;
        this.majorLecturersService = majorLecturersService;
    }

    @GetMapping()
    public String listLecturerSpecializations(Model model) {
        model.addAttribute("Specializations",majorLecturersSpecializationsService.getSpecializationsByLecturer(majorLecturersService.getMajorLecturer()));
        model.addAttribute("backUrl", "/major-lecturer-home");
        return "YourSpecializations";
    }
}
