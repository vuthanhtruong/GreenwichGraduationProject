package com.example.demo.document.service;

import com.example.demo.document.dao.DocumentsDAO;
import com.example.demo.document.model.Documents;
import com.example.demo.post.news.model.News;
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
