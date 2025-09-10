package com.example.demo.depositHistory.service;

import com.example.demo.accountBalance.model.AccountBalances;
import com.example.demo.depositHistory.model.DepositHistory;
import org.springframework.stereotype.Service;

@Service
public interface DepositHistoryService {
    void save(DepositHistory depositHistory);
    void createDepositHistory(DepositHistory depositHistory);
}