package com.example.demo.history.depositHistory.controller;

import com.example.demo.history.depositHistory.service.DepositHistoriesService;
import com.example.demo.user.student.service.StudentsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/student-home/deposit-histories")
public class ListDepositHistoriestController {
    private final DepositHistoriesService depositHistoriesService;
    private final StudentsService studentsService;

    public ListDepositHistoriestController(DepositHistoriesService depositHistoriesService, StudentsService studentsService) {
        this.depositHistoriesService = depositHistoriesService;
        this.studentsService = studentsService;
    }

    @GetMapping("")
    public String listDepositHistories(Model model) {
        model.addAttribute("DepositHistories", depositHistoriesService.getStudentDepositHistories(studentsService.getStudent()));
        return "ListDepositHistories";
    }
}
