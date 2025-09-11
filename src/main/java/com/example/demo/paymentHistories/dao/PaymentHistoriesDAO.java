package com.example.demo.paymentHistories.dao;

import com.example.demo.paymentHistories.model.PaymentHistories;
import com.example.demo.student.model.Students;

import java.util.List;

public interface PaymentHistoriesDAO {
    List<PaymentHistories> getStudentHistoriesPaymentDAO(Students student);
}
