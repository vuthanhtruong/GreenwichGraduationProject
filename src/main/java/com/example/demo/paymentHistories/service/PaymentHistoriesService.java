package com.example.demo.paymentHistories.service;

import com.example.demo.paymentHistories.model.PaymentHistories;
import com.example.demo.student.model.Students;

import java.util.List;

public interface PaymentHistoriesService {
    List<PaymentHistories> getStudentHistoriesPaymentDAO(Students student);
}
