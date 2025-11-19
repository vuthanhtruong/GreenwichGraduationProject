package com.example.demo.accountBalance.service;

import com.example.demo.accountBalance.dao.AccountBalancesDAO;
import com.example.demo.accountBalance.model.AccountBalances;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AccountBalancesServiceImpl implements AccountBalancesService {
    @Override
    public long totalStudentsWithAccount() {
        return accountBalancesDAO.totalStudentsWithAccount();
    }

    @Override
    public double totalBalanceAllStudents() {
        return accountBalancesDAO.totalBalanceAllStudents();
    }

    @Override
    public double averageBalance() {
        return accountBalancesDAO.averageBalance();
    }

    @Override
    public long countStudentsWithZeroBalance() {
        return accountBalancesDAO.countStudentsWithZeroBalance();
    }

    @Override
    public long countStudentsWithNegativeBalance() {
        return accountBalancesDAO.countStudentsWithNegativeBalance();
    }

    @Override
    public long countStudentsWithBalanceOver(double threshold) {
        return accountBalancesDAO.countStudentsWithBalanceOver(threshold);
    }

    @Override
    public Map<String, Long> balanceDistribution() {
        return accountBalancesDAO.balanceDistribution();
    }

    @Override
    public List<Object[]> top10HighestBalanceStudents() {
        return accountBalancesDAO.top10HighestBalanceStudents();
    }

    @Override
    public List<Object[]> top10LowestBalanceStudents() {
        return accountBalancesDAO.top10LowestBalanceStudents();
    }

    @Override
    public boolean refundBalance(String studentId, double amount) {
        return accountBalancesDAO.refundBalance(studentId, amount);
    }

    @Override
    public boolean deductBalance(String studentId, double amount) {
        return accountBalancesDAO.deductBalance(studentId, amount);
    }

    @Override
    public boolean hasSufficientBalance(String studentId, double requiredAmount) {
        return accountBalancesDAO.hasSufficientBalance(studentId, requiredAmount);
    }

    @Override
    public void DepositMoneyIntoAccount(AccountBalances accountBalances) {
        accountBalancesDAO.DepositMoneyIntoAccount(accountBalances);
    }

    @Override
    public AccountBalances findByStudentId(String studentId) {
        return accountBalancesDAO.findByStudentId(studentId);
    }

    private final AccountBalancesDAO accountBalancesDAO;

    public AccountBalancesServiceImpl(AccountBalancesDAO accountBalancesDAO) {
        this.accountBalancesDAO = accountBalancesDAO;
    }

    @Override
    public void createAccountBalances(AccountBalances accountBalances) {
        accountBalancesDAO.createAccountBalances(accountBalances);
    }
}
