package com.example.demo.financialHistory.financialHistories.controller;

import com.example.demo.accountBalance.model.AccountBalances;
import com.example.demo.accountBalance.service.AccountBalancesService;
import com.example.demo.financialHistory.financialHistories.model.FinancialHistories;
import com.example.demo.financialHistory.financialHistories.service.FinancialHistoriesService;
import com.example.demo.user.student.model.Students;
import com.example.demo.user.student.service.StudentsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/student-home")
public class FinancialHistoryController {

    private final FinancialHistoriesService financialHistoriesService;
    private final StudentsService studentsService;
    private final AccountBalancesService accountBalancesService;

    public FinancialHistoryController(FinancialHistoriesService financialHistoriesService, StudentsService studentsService, AccountBalancesService accountBalancesService) {
        this.financialHistoriesService = financialHistoriesService;
        this.studentsService = studentsService;
        this.accountBalancesService = accountBalancesService;
    }

    @GetMapping("/financial-histories")
    public String showFinancialHistories(HttpSession session, Model model) {
        Students student = studentsService.getStudent();

        List<FinancialHistories> histories = financialHistoriesService.getFinancialHistoriesByStudent(student);
        AccountBalances accountBalances=accountBalancesService.findByStudentId(student.getId());

        model.addAttribute("student", student);
        model.addAttribute("accountBalances", accountBalances);
        model.addAttribute("histories", histories);
        model.addAttribute("totalTransactions", histories.size());

        return "FinancialHistories";
    }
}