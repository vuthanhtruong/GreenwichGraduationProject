// File: SupportTicketRequestsDocumentServiceImpl.java
package com.example.demo.supportTickets.service;

import com.example.demo.supportTickets.dao.SupportTicketRequestsDAO;
import com.example.demo.supportTickets.dao.SupportTicketRequestsDocumentDAO;
import com.example.demo.supportTickets.model.SupportTicketRequests;
import com.example.demo.supportTickets.model.SupportTicketRequestsDocument;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class SupportTicketRequestsDocumentServiceImpl implements SupportTicketRequestsDocumentService {
    @Override
    public SupportTicketRequestsDocument getDocumentById(String documentId) {
        return documentDAO.getDocumentById(documentId);
    }

    private final SupportTicketRequestsDocumentDAO documentDAO;
    private final SupportTicketRequestsDAO requestDAO;

    public SupportTicketRequestsDocumentServiceImpl(SupportTicketRequestsDocumentDAO documentDAO,
                                                    SupportTicketRequestsDAO requestDAO) {
        this.documentDAO = documentDAO;
        this.requestDAO = requestDAO;
    }

    @Override
    public SupportTicketRequestsDocument uploadDocument(MultipartFile file, String requestId) {
        if (file.isEmpty()) throw new IllegalArgumentException("File rỗng!");

        SupportTicketRequests request = requestDAO.getRequestById(requestId);
        if (request == null) throw new IllegalArgumentException("Yêu cầu không tồn tại!");

        try {
            String fileName = file.getOriginalFilename();
            String fileType = file.getContentType();
            long fileSize = file.getSize();
            byte[] fileData = file.getBytes();

            SupportTicketRequestsDocument doc = new SupportTicketRequestsDocument(
                    fileName, fileType, fileSize, fileData, request
            );

            return documentDAO.save(doc);

        } catch (IOException e) {
            throw new RuntimeException("Lỗi đọc file: " + file.getOriginalFilename(), e);
        }
    }

    @Override
    public List<SupportTicketRequestsDocument> getDocumentsByRequestId(String requestId) {
        return documentDAO.getDocumentsByRequestId(requestId);
    }

    @Override
    public ByteArrayResource downloadDocument(String documentId) {
        SupportTicketRequestsDocument doc = documentDAO.getDocumentById(documentId);
        if (doc == null) throw new IllegalArgumentException("Tài liệu không tồn tại!");

        return new ByteArrayResource(doc.getFileData()) {
            @Override
            public String getFilename() {
                return doc.getFileName(); // ĐÃ CÓ
            }
        };
    }

    @Override
    public void deleteDocument(String documentId) {
        documentDAO.delete(documentId);
    }
}