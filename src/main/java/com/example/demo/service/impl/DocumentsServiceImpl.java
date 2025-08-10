package com.example.demo.service.impl;

import com.example.demo.dao.DocumentsDAO;
import com.example.demo.entity.Documents;
import com.example.demo.entity.News;
import com.example.demo.service.DocumentsService;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class DocumentsServiceImpl implements DocumentsService {
    private final DocumentsDAO documentsDAO;

    public DocumentsServiceImpl(DocumentsDAO documentsDAO) {
        this.documentsDAO = documentsDAO;
    }

    @Override
    public List<Documents> getDocumentsByNews(News news) {
        return documentsDAO.getDocumentsByNews(news);
    }

    @Override
    public void deleteDocuments(List<String> documentIds) {
        documentsDAO.deleteDocuments(documentIds);
    }
}
