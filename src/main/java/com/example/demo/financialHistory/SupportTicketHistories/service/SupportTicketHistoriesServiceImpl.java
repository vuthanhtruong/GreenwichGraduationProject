// File: SupportTicketHistoriesServiceImpl.java
package com.example.demo.financialHistory.SupportTicketHistories.service;

import com.example.demo.financialHistory.SupportTicketHistories.dao.SupportTicketHistoriesDAO;
import com.example.demo.financialHistory.SupportTicketHistories.model.SupportTicketHistories;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupportTicketHistoriesServiceImpl implements SupportTicketHistoriesService {

    private final SupportTicketHistoriesDAO historiesDAO;

    public SupportTicketHistoriesServiceImpl(SupportTicketHistoriesDAO historiesDAO) {
        this.historiesDAO = historiesDAO;
    }

    @Override
    public SupportTicketHistories createHistory(SupportTicketHistories history) {
        return historiesDAO.createHistory(history);
    }

    @Override
    public List<SupportTicketHistories> getHistoriesByStudentId(String studentId) {
        return historiesDAO.getHistoriesByStudentId(studentId);
    }

    @Override
    public List<SupportTicketHistories> getAllHistories() {
        return historiesDAO.getAllHistories();
    }

    @Override
    public SupportTicketHistories getHistoryById(String historyId) {
        return historiesDAO.getHistoryById(historyId);
    }

    @Override
    public long countByStudentId(String studentId) {
        return historiesDAO.countByStudentId(studentId);
    }
}