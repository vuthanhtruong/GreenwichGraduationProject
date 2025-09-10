package com.example.demo.accountBalance.dao;

import com.example.demo.accountBalance.model.AccountBalances;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class AccountBalancesDAOImpl implements AccountBalancesDAO {
    @Override
    public void DepositMoneyIntoAccount(AccountBalances accountBalances) {
        entityManager.persist(accountBalances);
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void createAccountBalances(AccountBalances accountBalances) {
        entityManager.persist(accountBalances);
    }

    @Override
    public AccountBalances findByStudentId(String studentId) {
        return entityManager.find(AccountBalances.class, studentId);
    }
}