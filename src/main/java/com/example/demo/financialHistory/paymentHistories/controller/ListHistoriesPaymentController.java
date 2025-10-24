package com.example.demo.financialHistory.paymentHistories.controller;

import com.example.demo.financialHistory.paymentHistories.service.PaymentHistoriesService;
import com.example.demo.user.student.service.StudentsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("student-home/payment-histories")
public class ListHistoriesPaymentController {

    private final PaymentHistoriesService PaymentHistories;
    private final StudentsService Students;

    public ListHistoriesPaymentController(PaymentHistoriesService paymentHistories, StudentsService students) {
        PaymentHistories = paymentHistories;
        Students = students;
    }

    @GetMapping("")
    public String listPaymentHistories(Model model) {
        model.addAttribute("PaymentHistories",PaymentHistories.getStudentHistoriesPaymentDAO(Students.getStudent()));
        return "PaymentHistories";
    }
}
