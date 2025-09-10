package com.example.demo.depositHistory.dao;

import com.example.demo.accountBalance.model.AccountBalances;
import com.example.demo.depositHistory.model.DepositHistory;

public interface DepositHistoryDAO {
    void save(DepositHistory depositHistory);
    void createDepositHistory(DepositHistory depositHistory);
    void save(AccountBalances accountBalances);
}
