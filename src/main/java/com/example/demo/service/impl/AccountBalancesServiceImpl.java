package com.example.demo.service.impl;

import com.example.demo.dao.AccountBalancesDAO;
import com.example.demo.entity.AccountBalances;
import com.example.demo.service.AccountBalancesService;
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
