package com.example.demo.document.service;

import com.example.demo.document.dao.DocumentsDAO;
import com.example.demo.document.model.Documents;
import com.example.demo.entity.AbstractClasses.PublicPosts;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class DocumentsServiceImpl implements DocumentsService {
    @Override
    public List<Documents> getDocumentsByPostId(String postId) {
        return documentsDAO.getDocumentsByPostId(postId);
    }

    @Override
    public void saveDocument(Documents doc) {
        documentsDAO.saveDocument(doc);
    }

    @Override
    public void addDocuments(PublicPosts post, MultipartFile[] files) {
        documentsDAO.addDocuments(post, files);
    }

    private final DocumentsDAO documentsDAO;

    public DocumentsServiceImpl(DocumentsDAO documentsDAO) {
        this.documentsDAO = documentsDAO;
    }

    @Override
    public void deleteDocuments(List<String> documentIds) {
        documentsDAO.deleteDocuments(documentIds);
    }

    @Override
    public List<Documents> getDocumentsByPost(PublicPosts post) {
        return documentsDAO.getDocumentsByPost(post);
    }

    @Override
    public Documents getDocumentById(String docId) {
        return documentsDAO.getDocumentById(docId);
    }

}