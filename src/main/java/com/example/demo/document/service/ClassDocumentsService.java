package com.example.demo.document.service;

import com.example.demo.document.model.ClassDocuments;
import com.example.demo.post.classPost.model.ClassPosts;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ClassDocumentsService {
    List<String> saveDocuments(ClassPosts post, MultipartFile[] files);
    List<ClassDocuments> getDocumentsByPostId(String postId);
    boolean isValidFileType(String contentType);
    ClassDocuments getDocumentById(String id);
}
