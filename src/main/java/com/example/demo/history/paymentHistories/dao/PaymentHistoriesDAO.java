package com.example.demo.history.paymentHistories.dao;

import com.example.demo.history.paymentHistories.model.PaymentHistories;
import com.example.demo.user.student.model.Students;

import java.util.List;

public interface PaymentHistoriesDAO {
    List<PaymentHistories> getStudentHistoriesPaymentDAO(Students student);
}
