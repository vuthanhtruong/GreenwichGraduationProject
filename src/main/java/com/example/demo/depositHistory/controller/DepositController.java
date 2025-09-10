package com.example.demo.depositHistory.controller;

import com.example.demo.accountBalance.model.AccountBalances;
import com.example.demo.accountBalance.service.AccountBalancesService;
import com.example.demo.depositHistory.model.DepositHistory;
import com.example.demo.depositHistory.service.DepositHistoryService;
import com.example.demo.student.model.Students;
import com.example.demo.entity.Enums.Status;
import com.example.demo.student.service.StudentsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/student-home")
public class DepositController {

    private final StudentsService studentService;
    private final AccountBalancesService accountBalancesService;
    private final DepositHistoryService depositHistoryService;

    public DepositController(StudentsService studentService, AccountBalancesService accountBalancesService, DepositHistoryService depositHistoryService) {
        this.studentService = studentService;
        this.accountBalancesService = accountBalancesService;
        this.depositHistoryService = depositHistoryService;
    }

    @PostMapping("/deposit")
    public ResponseEntity<Map<String, Object>> processDeposit(@RequestBody Map<String, Object> payload) {
        Map<String, Object> response = new HashMap<>();
        try {
            String orderId = (String) payload.get("orderId");
            Double amount = Double.valueOf(payload.get("amount").toString());
            String studentId = (String) payload.get("studentId");

            // Validate student
            Students student = studentService.findById(studentId);
            if (student == null) {
                response.put("success", false);
                response.put("message", "Student not found");
                return ResponseEntity.badRequest().body(response);
            }

            // Update AccountBalances
            AccountBalances accountBalance = accountBalancesService.findByStudentId(studentId);
            if (accountBalance == null) {
                accountBalance = new AccountBalances(student, 0.0, LocalDateTime.now());
            }
            accountBalance.setBalance(accountBalance.getBalance() + amount);
            accountBalance.setLastUpdated(LocalDateTime.now());
            accountBalancesService.save(accountBalance);

            // Create DepositHistory record
            DepositHistory depositHistory = new DepositHistory();
            depositHistory.setDepositHistoryId(UUID.randomUUID().toString());
            depositHistory.setStudent(student);
            depositHistory.setAccountBalance(accountBalance);
            depositHistory.setAmount(amount);
            depositHistory.setDepositTime(LocalDateTime.now());
            depositHistory.setCreatedAt(LocalDateTime.now());
            depositHistory.setStatus(Status.COMPLETED);
            depositHistory.setDescription("Deposit via PayPal, Order ID: " + orderId);
            depositHistoryService.save(depositHistory);

            response.put("success", true);
            response.put("message", "Deposit processed successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}