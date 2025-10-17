package com.example.demo.history.depositHistory.controller;

import com.example.demo.accountBalance.model.AccountBalances;
import com.example.demo.accountBalance.service.AccountBalancesService;
import com.example.demo.history.depositHistory.model.DepositHistories;
import com.example.demo.history.depositHistory.service.DepositHistoriesService;
import com.example.demo.entity.Enums.Status;
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

    // Trang nạp tiền
    @GetMapping("")
    public String showDepositPage(Model model) {
        Students student = studentService.getStudent();
        if (student == null) {
            model.addAttribute("error", "Student not found");
            return "error";
        }
        model.addAttribute("student", student);
        return "deposit";
    }

    // Tạo session Stripe Checkout
    @PostMapping("/create")
    public String createPayment(@RequestParam("amount") Double amount,
                                @RequestParam("studentId") String studentId) throws StripeException {

        Students student = studentService.findById(studentId);
        if (student == null || amount <= 0) {
            return "redirect:/error";
        }

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}&studentId=" + studentId)
                .setCancelUrl(cancelUrl + "?studentId=" + studentId)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setUnitAmount((long) (amount * 100)) // Stripe lưu bằng cents
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Deposit for student " + studentId)
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();

        Session session = Session.create(params);

        // Lưu deposit tạm thời
        AccountBalances account = accountBalancesService.findByStudentId(studentId);
        if (account == null) {
            account = new AccountBalances(student, 0.0, LocalDateTime.now());
            accountBalancesService.createAccountBalances(account);
        }

        DepositHistories deposit = new DepositHistories();
        deposit.setStudent(student);
        deposit.setAccountBalance(account);
        deposit.setAmount(amount);
        deposit.setDepositTime(LocalDateTime.now());
        deposit.setCreatedAt(LocalDateTime.now());
        deposit.setStatus(Status.PROCESSING);
        deposit.setDescription("Stripe deposit initiated for student " + studentId);

        depositHistoryService.createDepositHistory(deposit);

        return "redirect:" + session.getUrl();
    }

    // Xác nhận thanh toán Stripe
    @GetMapping("/success")
    public String depositSuccess(@RequestParam("session_id") String sessionId,
                                 @RequestParam("studentId") String studentId,
                                 Model model) throws StripeException {

        Session session = Session.retrieve(sessionId);

        if ("complete".equalsIgnoreCase(session.getStatus())) {
            Students student = studentService.findById(studentId);
            AccountBalances account = accountBalancesService.findByStudentId(studentId);

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

    // Hủy thanh toán
    @GetMapping("/cancel")
    public String depositCancel(@RequestParam("studentId") String studentId, Model model) {
        return "redirect:/student-home";
    }
}
