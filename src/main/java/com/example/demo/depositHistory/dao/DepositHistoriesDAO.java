package com.example.demo.depositHistory.dao;

import com.example.demo.depositHistory.model.DepositHistories;
import com.example.demo.entity.Enums.Status;

public interface DepositHistoriesDAO {
    void save(DepositHistories depositHistory);
    void createDepositHistory(DepositHistories depositHistory);
    DepositHistories findByStudentIdAndStatus(String studentId, Status status);
}
