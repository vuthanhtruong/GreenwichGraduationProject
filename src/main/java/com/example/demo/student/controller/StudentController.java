package com.example.demo.student.controller;
import com.example.demo.student.service.StudentsService;
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
        model.addAttribute("major", studentsService.getStudentMajor().getMajorName());
        model.addAttribute("student", studentsService.getStudent());
        return "StudentHome";
    }
}
