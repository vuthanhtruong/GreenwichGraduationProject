package com.example.demo.financialHistory.paymentHistories.service;

import com.example.demo.financialHistory.paymentHistories.model.PaymentHistories;
import com.example.demo.user.student.model.Students;

import java.util.List;

public interface PaymentHistoriesService {
    List<PaymentHistories> getStudentHistoriesPaymentDAO(Students student);
    void save(PaymentHistories payment);
}
