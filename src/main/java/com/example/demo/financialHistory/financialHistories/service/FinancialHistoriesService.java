package com.example.demo.financialHistory.financialHistories.service;

import com.example.demo.financialHistory.financialHistories.model.FinancialHistories;
import com.example.demo.user.student.model.Students;

import java.util.List;

public interface FinancialHistoriesService {
    List<FinancialHistories> getFinancialHistoriesByStudent(Students student);
}
