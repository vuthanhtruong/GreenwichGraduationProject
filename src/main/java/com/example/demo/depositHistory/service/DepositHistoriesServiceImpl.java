package com.example.demo.depositHistory.service;

import com.example.demo.depositHistory.dao.DepositHistoriesDAO;
import com.example.demo.depositHistory.model.DepositHistories;
import com.example.demo.entity.Enums.Status;
import org.springframework.stereotype.Service;

@Service
public class DepositHistoriesServiceImpl implements DepositHistoriesService {

    @Override
    public DepositHistories findByStudentIdAndStatus(String studentId, Status status) {
        return depositHistoryService.findByStudentIdAndStatus(studentId, status);
    }


    private final DepositHistoriesDAO depositHistoryService;

    public DepositHistoriesServiceImpl(DepositHistoriesDAO depositHistoryService) {
        this.depositHistoryService = depositHistoryService;
    }

    @Override
    public void save(DepositHistories depositHistory) {
        depositHistoryService.save(depositHistory);
    }

    @Override
    public void createDepositHistory(DepositHistories depositHistory) {
        depositHistoryService.createDepositHistory(depositHistory);
    }
}
