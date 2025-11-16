package com.example.demo.document.dao;

import com.example.demo.document.model.ClassDocuments;
import com.example.demo.post.classPost.model.ClassPosts;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ClassDocumentsDAO {
    List<ClassDocuments> getDocumentsByPostId(String postId);
    void saveDocument(ClassDocuments document);
    Map<String, String> validateDocument(ClassDocuments document);
    String generateUniqueDocumentId(String postId);
    ClassDocuments getDocumentById(String id);
}
