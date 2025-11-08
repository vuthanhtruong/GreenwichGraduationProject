package com.example.demo.accountBalance.service;

import com.example.demo.accountBalance.model.AccountBalances;

public interface AccountBalancesService {
    void createAccountBalances(AccountBalances accountBalances);
    AccountBalances findByStudentId(String studentId);
    void DepositMoneyIntoAccount(AccountBalances accountBalances);
    boolean hasSufficientBalance(String studentId, double requiredAmount);
    boolean deductBalance(String studentId, double amount);
}
