package com.example.demo.paymentHistories.dao;
import com.example.demo.paymentHistories.model.PaymentHistories;
import com.example.demo.student.model.Students;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
@Transactional
public class PaymentHistoriesDAOImpl implements PaymentHistoriesDAO {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<PaymentHistories> getStudentHistoriesPaymentDAO(Students student) {
        return entityManager.createQuery("from PaymentHistories d where d.student=:student", PaymentHistories.class).setParameter("student", student).getResultList();
    }
}
