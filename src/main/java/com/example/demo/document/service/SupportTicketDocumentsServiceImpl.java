package com.example.demo.document.service;

import com.example.demo.document.dao.SupportTicketDocumentsDAO;
import com.example.demo.document.model.SupportTicketDocuments;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SupportTicketDocumentsServiceImpl implements SupportTicketDocumentsService {
    @Override
    public Map<String, String> validateDocument(SupportTicketDocuments doc) {
        return dao.validateDocument(doc);
    }

    private final SupportTicketDocumentsDAO dao;

    public SupportTicketDocumentsServiceImpl(SupportTicketDocumentsDAO dao) {
        this.dao = dao;
    }

    @Override public List<SupportTicketDocuments> getDocumentsByTicketId(String ticketId) { return dao.getDocumentsByTicketId(ticketId); }
    @Override public SupportTicketDocuments getDocumentById(Long id) { return dao.getDocumentById(id); }
    @Override public SupportTicketDocuments saveDocument(SupportTicketDocuments doc) { return dao.saveDocument(doc); }
    @Override public void deleteDocument(Long id) { dao.deleteDocument(id); }
    @Override public long countDocumentsByTicketId(String ticketId) { return dao.countDocumentsByTicketId(ticketId); }
}