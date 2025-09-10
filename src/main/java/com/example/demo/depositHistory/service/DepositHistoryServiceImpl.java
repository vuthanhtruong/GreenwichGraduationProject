package com.example.demo.depositHistory.service;

import com.example.demo.accountBalance.model.AccountBalances;
import com.example.demo.depositHistory.dao.DepositHistoryDAO;
import com.example.demo.depositHistory.model.DepositHistory;
import org.springframework.stereotype.Service;

@Service
public class DepositHistoryServiceImpl implements DepositHistoryService {

    private final DepositHistoryDAO depositHistoryService;

    public DepositHistoryServiceImpl(DepositHistoryDAO depositHistoryService) {
        this.depositHistoryService = depositHistoryService;
    }

    @Override
    public void save(DepositHistory depositHistory) {
        depositHistoryService.save(depositHistory);
    }

    @Override
    public void createDepositHistory(DepositHistory depositHistory) {
        depositHistoryService.createDepositHistory(depositHistory);
    }
}
