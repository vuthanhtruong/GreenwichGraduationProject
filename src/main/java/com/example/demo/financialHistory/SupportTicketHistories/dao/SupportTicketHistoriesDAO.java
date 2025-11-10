// File: SupportTicketHistoriesDAO.java
package com.example.demo.financialHistory.SupportTicketHistories.dao;

import com.example.demo.financialHistory.SupportTicketHistories.model.SupportTicketHistories;

import java.util.List;

public interface SupportTicketHistoriesDAO {
    SupportTicketHistories createHistory(SupportTicketHistories history);
    List<SupportTicketHistories> getHistoriesByStudentId(String studentId);
    List<SupportTicketHistories> getAllHistories();
    SupportTicketHistories getHistoryById(String historyId);
    long countByStudentId(String studentId);
}