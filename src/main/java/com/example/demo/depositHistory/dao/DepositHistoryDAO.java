package com.example.demo.depositHistory.dao;

import com.example.demo.accountBalance.model.AccountBalances;
import com.example.demo.depositHistory.model.DepositHistory;
import com.example.demo.entity.Enums.Status;

public interface DepositHistoryDAO {
    void save(DepositHistory depositHistory);
    void createDepositHistory(DepositHistory depositHistory);
    DepositHistory findByStudentIdAndStatus(String studentId, Status status);
}
