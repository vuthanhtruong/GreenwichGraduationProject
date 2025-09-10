package com.example.demo.depositHistory.service;

import com.example.demo.accountBalance.model.AccountBalances;
import com.example.demo.depositHistory.dao.DepositHistoryDAO;
import com.example.demo.depositHistory.model.DepositHistory;
import com.example.demo.entity.Enums.Status;
import org.springframework.stereotype.Service;

@Service
public class DepositHistoryServiceImpl implements DepositHistoryService {

    @Override
    public DepositHistory findByStudentIdAndStatus(String studentId, Status status) {
        return depositHistoryService.findByStudentIdAndStatus(studentId, status);
    }


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
