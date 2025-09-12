package com.example.demo.depositHistory.service;

import com.example.demo.depositHistory.dao.DepositHistoriesDAO;
import com.example.demo.depositHistory.model.DepositHistories;
import com.example.demo.entity.Enums.Status;
import com.example.demo.student.model.Students;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepositHistoriesServiceImpl implements DepositHistoriesService {
    @Override
    public List<DepositHistories> getStudentDepositHistories(Students student) {
        return depositHistoryService.getStudentDepositHistories(student);
    }

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
