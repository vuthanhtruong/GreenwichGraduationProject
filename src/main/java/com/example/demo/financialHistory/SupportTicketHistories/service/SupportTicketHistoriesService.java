// File: SupportTicketHistoriesService.java
package com.example.demo.financialHistory.SupportTicketHistories.service;

import com.example.demo.financialHistory.SupportTicketHistories.model.SupportTicketHistories;

import java.util.List;

public interface SupportTicketHistoriesService {
    SupportTicketHistories createHistory(SupportTicketHistories history);
    List<SupportTicketHistories> getHistoriesByStudentId(String studentId);
    List<SupportTicketHistories> getAllHistories();
    SupportTicketHistories getHistoryById(String historyId);
    long countByStudentId(String studentId);
}