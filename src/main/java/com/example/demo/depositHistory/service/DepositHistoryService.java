package com.example.demo.depositHistory.service;

import com.example.demo.accountBalance.model.AccountBalances;
import com.example.demo.depositHistory.model.DepositHistory;
import com.example.demo.entity.Enums.Status;

public interface DepositHistoryService {
    void save(DepositHistory depositHistory);
    void createDepositHistory(DepositHistory depositHistory);
    DepositHistory findByStudentIdAndStatus(String studentId, Status status);
}