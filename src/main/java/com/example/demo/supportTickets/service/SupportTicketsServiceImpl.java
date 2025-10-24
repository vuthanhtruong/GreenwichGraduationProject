// com.example.demo.SupportTickets.service.SupportTicketsServiceImpl.java
package com.example.demo.supportTickets.service;

import com.example.demo.supportTickets.dao.SupportTicketsDAO;
import com.example.demo.supportTickets.model.SupportTickets;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SupportTicketsServiceImpl implements SupportTicketsService {

    private final SupportTicketsDAO dao;

    public SupportTicketsServiceImpl(SupportTicketsDAO dao) {
        this.dao = dao;
    }

    @Override public List<SupportTickets> getPaginatedTickets(int firstResult, int pageSize) { return dao.getPaginatedTickets(firstResult, pageSize); }
    @Override public long numberOfTickets() { return dao.numberOfTickets(); }
    @Override public SupportTickets getTicketById(String id) { return dao.getTicketById(id); }
    @Override public SupportTickets addTicket(SupportTickets ticket) { return dao.addTicket(ticket); }
    @Override public void updateTicket(String id, SupportTickets ticket) { dao.updateTicket(id, ticket); }
    @Override public void deleteTicket(String id) { dao.deleteTicket(id); }
    @Override public Map<String, String> validateTicket(SupportTickets ticket) { return dao.validateTicket(ticket); }
    @Override public List<SupportTickets> searchTickets(String searchType, String keyword, int firstResult, int pageSize) { return dao.searchTickets(searchType, keyword, firstResult, pageSize); }
    @Override public long countSearchResults(String searchType, String keyword) { return dao.countSearchResults(searchType, keyword); }
}