package com.example.demo.accountBalance.service;

import com.example.demo.accountBalance.dao.AccountBalancesDAO;
import com.example.demo.accountBalance.model.AccountBalances;
import org.springframework.stereotype.Service;

@Service
public class AccountBalancesServiceImpl implements AccountBalancesService {
    private final AccountBalancesDAO accountBalancesDAO;

    public AccountBalancesServiceImpl(AccountBalancesDAO accountBalancesDAO) {
        this.accountBalancesDAO = accountBalancesDAO;
    }

    @Override
    public void createAccountBalances(AccountBalances accountBalances) {
        accountBalancesDAO.createAccountBalances(accountBalances);
    }
}
