package com.example.demo.document.dao;

import com.example.demo.document.model.Documents;
import com.example.demo.post.news.model.News;

import java.util.List;

public interface DocumentsDAO {
    List<Documents> getDocumentsByNews(News news);
    void deleteDocuments(List<String> documentIds);
}
