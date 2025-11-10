package com.example.demo.accountBalance.dao;

import com.example.demo.accountBalance.model.AccountBalances;

public interface AccountBalancesDAO {
    void createAccountBalances(AccountBalances accountBalances);
    AccountBalances findByStudentId(String studentId);
    void DepositMoneyIntoAccount(AccountBalances accountBalances);
    boolean hasSufficientBalance(String studentId, double requiredAmount);
    boolean deductBalance(String studentId, double amount);
    boolean refundBalance(String studentId, double amount);
}