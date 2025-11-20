// src/main/java/com/example/demo/financialHistory/financialHistories/controller/ParentPaymentHistoryController.java
package com.example.demo.financialHistory.financialHistories.controller;

import com.example.demo.accountBalance.model.AccountBalances;
import com.example.demo.accountBalance.service.AccountBalancesService;
import com.example.demo.entity.Enums.Status;
import com.example.demo.financialHistory.depositHistory.model.DepositHistories;
import com.example.demo.financialHistory.depositHistory.service.DepositHistoriesService;
import com.example.demo.financialHistory.financialHistories.model.FinancialHistories;
import com.example.demo.financialHistory.financialHistories.service.FinancialHistoriesService;
import com.example.demo.user.parentAccount.service.ParentAccountsService;
import com.example.demo.user.student.model.Students;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/parent")
public class ParentPaymentHistoryController {

    private final ParentAccountsService parentAccountsService;
    private final FinancialHistoriesService financialHistoriesService;
    private final AccountBalancesService accountBalancesService;
    private final DepositHistoriesService depositHistoriesService;

    public ParentPaymentHistoryController(ParentAccountsService parentAccountsService,
                                          FinancialHistoriesService financialHistoriesService,
                                          AccountBalancesService accountBalancesService,
                                          DepositHistoriesService depositHistoriesService) {
        this.parentAccountsService = parentAccountsService;
        this.financialHistoriesService = financialHistoriesService;
        this.accountBalancesService = accountBalancesService;
        this.depositHistoriesService = depositHistoriesService;
    }

    // 2. Submit con đã chọn
    @PostMapping("/payment-history")
    public String selectChild(@RequestParam String studentId, HttpSession session) {
        var parent = parentAccountsService.getParent();
        boolean valid = parentAccountsService.getStudentsByParentId(parent.getId()).stream()
                .anyMatch(s -> s.getId().equals(studentId));

        if (!valid) {
            return "redirect:/parent/payment-history?error";
        }

        session.setAttribute("payment_childId", studentId);
        return "redirect:/parent/payment-history/view";
    }

    // 3. Xem lịch sử thanh toán của con
    @GetMapping("/payment-history/view")
    public String viewPaymentHistory(Model model, HttpSession session) {
        String childId = (String) session.getAttribute("payment_childId");
        if (childId == null) {
            return "redirect:/parent/payment-history";
        }

        var parent = parentAccountsService.getParent();
        Students child = parentAccountsService.getStudentsByParentId(parent.getId()).stream()
                .filter(s -> s.getId().equals(childId))
                .findFirst()
                .orElse(null);

        if (child == null) {
            session.removeAttribute("payment_childId");
            return "redirect:/parent/payment-history";
        }

        List<FinancialHistories> histories = financialHistoriesService.getFinancialHistoriesByStudent(child);
        AccountBalances accountBalances = accountBalancesService.findByStudentId(child.getId());

        model.addAttribute("student", child);
        model.addAttribute("accountBalances", accountBalances);
        model.addAttribute("histories", histories);
        model.addAttribute("totalTransactions", histories.size());

        // Kiểm tra có giao dịch nạp tiền đang chờ không (hiển thị nút tiếp tục nếu có)
        DepositHistories processing = depositHistoriesService.findByStudentIdAndStatus(child.getId(), Status.PROCESSING);
        if (processing != null) {
            String pendingUrl = (String) session.getAttribute("pendingStripeUrl_" + child.getId());
            if (pendingUrl != null) {
                model.addAttribute("continueDepositUrl", pendingUrl);
            }
        }

        // Cho phép chuyển con nhanh
        model.addAttribute("siblings", parentAccountsService.getStudentsByParentId(parent.getId()));
        model.addAttribute("currentChildId", childId);

        return "ParentPaymentHistory"; // HTML riêng cho phụ huynh
    }
}