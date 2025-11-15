package com.example.demo.user.student.controller;
import com.example.demo.accountBalance.service.AccountBalancesService;
import com.example.demo.user.student.service.StudentsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/student-home")
public class StudentController {

    private final StudentsService studentsService;
    private final AccountBalancesService accountBalancesService;

    public StudentController(StudentsService studentsService, AccountBalancesService accountBalancesService) {
        this.studentsService = studentsService;
        this.accountBalancesService = accountBalancesService;
    }

    @GetMapping("")
    public String getStaffHomeInfo(Model model) {
        model.addAttribute("major", studentsService.getStudentMajor().getMajorName());
        model.addAttribute("student", studentsService.getStudent());
        model.addAttribute("accountBalance",accountBalancesService.findByStudentId(studentsService.getStudent().getId()));
        model.addAttribute("campus",studentsService.getStudent().getCampus().getCampusName());
        return "StudentHome";
    }
}
