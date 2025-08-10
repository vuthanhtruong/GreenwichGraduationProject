package com.example.demo.service;

import com.example.demo.entity.Documents;
import com.example.demo.entity.News;

import java.util.List;

public interface DocumentsService {
    List<Documents> getDocumentsByNews(News news);
    void deleteDocuments(List<String> documentIds);
}
