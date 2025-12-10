package com.example.demo.document.dao;

import com.example.demo.document.model.Documents;
import com.example.demo.post.Blog.model.PublicPosts;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentsDAO {
    List<Documents> getDocumentsByPost(PublicPosts post);
    List<Documents> getDocumentsByPostId(String postId);
    Documents getDocumentById(String docId);
    void saveDocument(Documents doc);
    void deleteDocuments(List<String> documentIds);
    void addDocuments(PublicPosts post, MultipartFile[] files);
}