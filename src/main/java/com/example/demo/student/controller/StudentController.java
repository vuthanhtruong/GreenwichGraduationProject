package com.example.demo.student.controller;
import com.example.demo.accountBalance.service.AccountBalancesService;
import com.example.demo.student.service.StudentsService;
import com.stripe.service.AccountService;
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
        model.addAttribute("accountBalancesService",accountBalancesService.findByStudentId(studentsService.getStudent().getId()));
        return "StudentHome";
    }
}
