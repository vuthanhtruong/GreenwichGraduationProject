package com.example.demo.financialHistory.paymentHistories.service;

import com.example.demo.financialHistory.paymentHistories.dao.PaymentHistoriesDAO;
import com.example.demo.financialHistory.paymentHistories.model.PaymentHistories;
import com.example.demo.user.student.model.Students;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class PaymentHistoriesServiceImpl implements PaymentHistoriesService {
    private final PaymentHistoriesDAO historiesPaymentDAO;

    public PaymentHistoriesServiceImpl(PaymentHistoriesDAO historiesPaymentDAO) {
        this.historiesPaymentDAO = historiesPaymentDAO;
    }

    @Override
    public List<PaymentHistories> getStudentHistoriesPaymentDAO(Students student) {
        return historiesPaymentDAO.getStudentHistoriesPaymentDAO(student);
    }
}
