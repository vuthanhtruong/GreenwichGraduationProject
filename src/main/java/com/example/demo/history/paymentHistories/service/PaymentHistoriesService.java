package com.example.demo.history.paymentHistories.service;

import com.example.demo.history.paymentHistories.model.PaymentHistories;
import com.example.demo.user.student.model.Students;

import java.util.List;

public interface PaymentHistoriesService {
    List<PaymentHistories> getStudentHistoriesPaymentDAO(Students student);
}
