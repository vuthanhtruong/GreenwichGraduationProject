package com.example.demo.accountBalance.controller;

import com.example.demo.accountBalance.model.AccountBalances;
import com.example.demo.accountBalance.service.AccountBalancesService;
import com.example.demo.entity.Enums.Status;
import com.example.demo.financialHistory.depositHistory.model.DepositHistories;
import com.example.demo.financialHistory.depositHistory.service.DepositHistoriesService;
import com.example.demo.user.student.model.Students;
import com.example.demo.user.student.service.StudentsService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/student-home/deposit")
public class DepositController {

    private final StudentsService studentService;
    private final AccountBalancesService accountBalancesService;
    private final DepositHistoriesService depositHistoryService;

    @Value("${stripe.successUrl}")
    private String successUrl;

    @Value("${stripe.cancelUrl}")
    private String cancelUrl;

    public DepositController(StudentsService studentService,
                             AccountBalancesService accountBalancesService,
                             DepositHistoriesService depositHistoryService) {
        this.studentService = studentService;
        this.accountBalancesService = accountBalancesService;
        this.depositHistoryService = depositHistoryService;
    }

    // ==================== NẠP MỚI ====================
    @GetMapping
    public String showNewDepositPage(Model model) {
        Students student = studentService.getStudent();
        if (student == null) {
            model.addAttribute("error", "Student not found");
            return "error";
        }
        model.addAttribute("account", accountBalancesService.findByStudentId(student.getId()));
        model.addAttribute("student", student);
        return "deposit";
    }

    // ==================== TẠO PHIÊN NẠP MỚI ====================
    @PostMapping("/create-new")
    public String createNewDeposit(@RequestParam("amount") Double amount,
                                   @RequestParam("studentId") String studentId) throws StripeException {

        Students student = studentService.findById(studentId);
        if (student == null || amount == null || amount <= 0) {
            return "redirect:/error";
        }

        // ✅ Luôn tạo log PROCESSING mới
        AccountBalances account = accountBalancesService.findByStudentId(studentId);
        if (account == null) {
            account = new AccountBalances(student, 0.0, LocalDateTime.now());
            accountBalancesService.createAccountBalances(account);
        }

        DepositHistories deposit = new DepositHistories();
        deposit.setStudent(student);
        deposit.setAccountBalance(account);
        deposit.setAmount(amount);
        deposit.setCreatedAt(LocalDateTime.now());
        deposit.setStatus(Status.PROCESSING);
        deposit.setDescription("Stripe new deposit initiated for student " + studentId);
        depositHistoryService.createDepositHistory(deposit);

        // ✅ Tạo phiên Stripe
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(cancelUrl)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setUnitAmount((long) (amount * 100))
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("New Deposit for student " + studentId)
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .putMetadata("studentId", studentId) // ✅ metadata: tránh lộ id trên URL
                .build();

        Session session = Session.create(params);
        return "redirect:" + session.getUrl();
    }

    // ==================== NẠP LẠI ====================
    @PostMapping("/retry")
    public String retryDeposit(@RequestParam("studentId") String studentId) throws StripeException {

        DepositHistories deposit = depositHistoryService.findByStudentIdAndStatus(studentId, Status.PROCESSING);

        // Nếu không có giao dịch đang xử lý → quay lại trang nạp mới
        if (deposit == null) {
            return "redirect:/student-home/deposit";
        }

        double amount = deposit.getAmount();
        Students student = deposit.getStudent();

        // ✅ Tạo lại session Stripe, KHÔNG tạo log mới
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(cancelUrl)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setUnitAmount((long) (amount * 100))
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Retry Deposit for student " + studentId)
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .putMetadata("studentId", studentId) // ✅ truyền ngầm ID qua metadata Stripe
                .build();

        Session session = Session.create(params);

        // ✅ Redirect thẳng tới Stripe Checkout
        return "redirect:" + session.getUrl();
    }

    // ==================== THANH TOÁN THÀNH CÔNG ====================
    @GetMapping("/success")
    public String depositSuccess(@RequestParam("session_id") String sessionId, Model model) throws StripeException {

        Session session = Session.retrieve(sessionId);
        String studentId = session.getMetadata().get("studentId");

        if ("complete".equalsIgnoreCase(session.getStatus())) {
            Students student = studentService.findById(studentId);
            AccountBalances account = accountBalancesService.findByStudentId(studentId);
            if (account == null) {
                account = new AccountBalances(student, 0.0, LocalDateTime.now());
                accountBalancesService.createAccountBalances(account);
            }

            double amount = session.getAmountTotal() / 100.0;
            account.setBalance(account.getBalance() + amount);
            account.setLastUpdated(LocalDateTime.now());
            accountBalancesService.DepositMoneyIntoAccount(account);

            DepositHistories deposit = depositHistoryService.findByStudentIdAndStatus(studentId, Status.PROCESSING);
            if (deposit != null) {
                deposit.setStatus(Status.COMPLETED);
                deposit.setDescription("Stripe deposit completed for student " + studentId);
                depositHistoryService.save(deposit);
            }

            model.addAttribute("message", "Deposit successful!");
        } else {
            model.addAttribute("message", "Payment not completed");
        }

        return "DepositSuccess";
    }

    // ==================== HỦY THANH TOÁN ====================
    @GetMapping("/cancel")
    public String depositCancel() {
        return "redirect:/student-home";
    }
}
