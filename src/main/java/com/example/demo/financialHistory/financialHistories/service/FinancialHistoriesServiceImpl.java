package com.example.demo.financialHistory.financialHistories.service;

import com.example.demo.financialHistory.financialHistories.dao.FinancialHistoriesDAO;
import com.example.demo.financialHistory.financialHistories.model.FinancialHistories;
import com.example.demo.user.student.model.Students;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FinancialHistoriesServiceImpl implements FinancialHistoriesService {
    private final FinancialHistoriesDAO financialHistoriesService;

    public FinancialHistoriesServiceImpl(FinancialHistoriesDAO financialHistoriesService) {
        this.financialHistoriesService = financialHistoriesService;
    }

    @Override
    public List<FinancialHistories> getFinancialHistoriesByStudent(Students student) {
        return financialHistoriesService.getFinancialHistoriesByStudent(student);
    }
}
