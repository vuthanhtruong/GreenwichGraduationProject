package com.example.demo.financialHistory.financialHistories.controller;

import com.example.demo.accountBalance.model.AccountBalances;
import com.example.demo.accountBalance.service.AccountBalancesService;
import com.example.demo.entity.Enums.Status;
import com.example.demo.financialHistory.depositHistory.model.DepositHistories;
import com.example.demo.financialHistory.depositHistory.service.DepositHistoriesService;
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
    private final DepositHistoriesService depositHistoriesService;

    public FinancialHistoryController(FinancialHistoriesService financialHistoriesService,
                                      StudentsService studentsService,
                                      AccountBalancesService accountBalancesService,
                                      DepositHistoriesService depositHistoriesService) {
        this.financialHistoriesService = financialHistoriesService;
        this.studentsService = studentsService;
        this.accountBalancesService = accountBalancesService;
        this.depositHistoriesService = depositHistoriesService;
    }

    @GetMapping("/financial-histories")
    public String showFinancialHistories(HttpSession session, Model model) {
        Students student = studentsService.getStudent();
        if (student == null) {
            return "redirect:/login";
        }

        List<FinancialHistories> histories = financialHistoriesService.getFinancialHistoriesByStudent(student);
        AccountBalances accountBalances = accountBalancesService.findByStudentId(student.getId());

        model.addAttribute("student", student);
        model.addAttribute("accountBalances", accountBalances);
        model.addAttribute("histories", histories);
        model.addAttribute("totalTransactions", histories.size());

        // Kiểm tra có đang chờ nạp không
        DepositHistories processing = depositHistoriesService.findByStudentIdAndStatus(student.getId(), Status.PROCESSING);
        if (processing != null) {
            String pendingUrl = (String) session.getAttribute("pendingStripeUrl_" + student.getId());
            if (pendingUrl != null) {
                model.addAttribute("continueDepositUrl", pendingUrl);
            }
        }
        return "FinancialHistories";
    }
}