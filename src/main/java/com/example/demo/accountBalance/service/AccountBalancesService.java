package com.example.demo.accountBalance.service;

import com.example.demo.accountBalance.model.AccountBalances;

public interface AccountBalancesService {
    void createAccountBalances(AccountBalances accountBalances);
    AccountBalances findByStudentId(String studentId);
    void save(AccountBalances accountBalances);
}
