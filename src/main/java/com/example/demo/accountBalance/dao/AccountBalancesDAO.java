package com.example.demo.accountBalance.dao;

import com.example.demo.accountBalance.model.AccountBalances;

public interface AccountBalancesDAO {
    void createAccountBalances(AccountBalances accountBalances);
    AccountBalances findByStudentId(String studentId);
    void DepositMoneyIntoAccount(AccountBalances accountBalances);
}