package com.example.demo.accountBalance.dao;

import com.example.demo.accountBalance.model.AccountBalances;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
@Transactional
public class AccountBalancesDAOImpl implements AccountBalancesDAO {
    // File: AccountBalancesServiceImpl.java
    @Override
    public boolean refundBalance(String studentId, double amount) {
        AccountBalances account = findByStudentId(studentId);
        if (account == null) return false;

        account.setBalance(account.getBalance() + amount);
        account.setLastUpdated(LocalDateTime.now());
        entityManager.merge(account);
        return true;
    }

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
    @Override
    public boolean hasSufficientBalance(String studentId, double requiredAmount) {
        AccountBalances account = findByStudentId(studentId);
        if (account == null) {
            return false;
        }
        return account.getBalance() >= requiredAmount;
    }

    @Override
    public boolean deductBalance(String studentId, double amount) {
        AccountBalances account = findByStudentId(studentId);
        if (account == null || account.getBalance() < amount) {
            return false;
        }
        account.setBalance(account.getBalance() - amount);
        account.setLastUpdated(LocalDateTime.now());
        entityManager.merge(account); // dùng merge
        return true;
    }
}