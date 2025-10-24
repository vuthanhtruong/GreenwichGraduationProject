// com.example.demo.SupportTickets.dao.SupportTicketsDAO.java
package com.example.demo.supportTickets.dao;

import com.example.demo.supportTickets.model.SupportTickets;
import java.util.List;
import java.util.Map;

public interface SupportTicketsDAO {
    List<SupportTickets> getPaginatedTickets(int firstResult, int pageSize);
    long numberOfTickets();
    SupportTickets getTicketById(String id);
    SupportTickets addTicket(SupportTickets ticket);
    void updateTicket(String id, SupportTickets ticket);
    void deleteTicket(String id);
    Map<String, String> validateTicket(SupportTickets ticket);
    List<SupportTickets> searchTickets(String searchType, String keyword, int firstResult, int pageSize);
    long countSearchResults(String searchType, String keyword);
    String generateUniqueTicketId();
}