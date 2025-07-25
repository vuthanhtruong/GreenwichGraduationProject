package com.example.demo.controller;
import com.example.demo.entity.Staffs;
import com.example.demo.service.StaffsService;
import com.example.demo.service.StudentsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/student-home")
public class StudentController {

    private final StudentsService studentsService;

    public StudentController(StudentsService studentsService) {
        this.studentsService = studentsService;
    }

    @GetMapping("")
    public String getStaffHomeInfo(Model model) {
        model.addAttribute("major", studentsService.getMajors().getMajorName());
        model.addAttribute("student", studentsService.dataStudent());
        return "StudentHome";
    }
}
