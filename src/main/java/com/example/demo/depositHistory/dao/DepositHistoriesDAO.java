package com.example.demo.depositHistory.dao;

import com.example.demo.depositHistory.model.DepositHistories;
import com.example.demo.entity.Enums.Status;
import com.example.demo.student.model.Students;

import java.util.List;

public interface DepositHistoriesDAO {
    void save(DepositHistories depositHistory);
    void createDepositHistory(DepositHistories depositHistory);
    DepositHistories findByStudentIdAndStatus(String studentId, Status status);
    List<DepositHistories> getStudentDepositHistories(Students  student);
}
