package com.example.demo.financialHistory.financialHistories.dao;

import com.example.demo.financialHistory.financialHistories.model.FinancialHistories;
import com.example.demo.user.student.model.Students;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class FinancialHistoriesDAOImpl implements FinancialHistoriesDAO {
    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public List<FinancialHistories> getFinancialHistoriesByStudent(Students student) {
        return entityManager.createQuery("from FinancialHistories f where f.student=:student", FinancialHistories.class).setParameter("student", student).getResultList();
    }
}
