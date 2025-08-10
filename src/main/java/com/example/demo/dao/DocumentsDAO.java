package com.example.demo.dao;

import com.example.demo.entity.Documents;

import java.util.List;

public interface DocumentsDAO {
    Documents getDocumentById(String documentId);
    void addDocument(Documents document);
    List<Documents> getAllDocuments();
    List<Documents> getAllDocumentsByMajor(String majorId);
    List<Documents> getAllDocumentsByNews(String newsId);
    List<Documents> getAllDocumentsByAuthor(String authorId);
}
