package com.example.demo.accountBalance.dao;

import com.example.demo.accountBalance.model.AccountBalances;

import java.util.List;
import java.util.Map;

public interface AccountBalancesDAO {
    void createAccountBalances(AccountBalances accountBalances);
    AccountBalances findByStudentId(String studentId);
    void DepositMoneyIntoAccount(AccountBalances accountBalances);
    boolean hasSufficientBalance(String studentId, double requiredAmount);
    boolean deductBalance(String studentId, double amount);
    boolean refundBalance(String studentId, double amount);

    // ==================== ADMIN DASHBOARD - ACCOUNT BALANCE STATISTICS ====================
    long totalStudentsWithAccount();
    double totalBalanceAllStudents();
    double averageBalance();
    long countStudentsWithZeroBalance();
    long countStudentsWithNegativeBalance();
    long countStudentsWithBalanceOver(double threshold); // e.g., 10_000_000 VND
    Map<String, Long> balanceDistribution(); // groups: 0, <5M, 5-20M, 20-50M, >50M
    List<Object[]> top10HighestBalanceStudents();
    List<Object[]> top10LowestBalanceStudents();
}