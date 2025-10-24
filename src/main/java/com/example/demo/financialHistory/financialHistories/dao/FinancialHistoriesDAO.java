package com.example.demo.financialHistory.financialHistories.dao;

import com.example.demo.financialHistory.financialHistories.model.FinancialHistories;
import com.example.demo.user.student.model.Students;

import java.util.List;

public interface FinancialHistoriesDAO {
    List<FinancialHistories> getFinancialHistoriesByStudent(Students student);
}
