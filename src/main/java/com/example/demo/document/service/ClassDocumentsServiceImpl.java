package com.example.demo.document.service;

import com.example.demo.document.dao.ClassDocumentsDAO;
import com.example.demo.document.model.ClassDocuments;
import com.example.demo.post.classPost.model.ClassPosts;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ClassDocumentsServiceImpl implements ClassDocumentsService {
    @Override
    public ClassDocuments getDocumentById(String id) {
        return classDocumentsDAO.getDocumentById(id);
    }

    private final ClassDocumentsDAO classDocumentsDAO;

    public ClassDocumentsServiceImpl(ClassDocumentsDAO classDocumentsDAO) {
        this.classDocumentsDAO = classDocumentsDAO;
    }

    @Override
    public List<String> saveDocuments(ClassPosts post, MultipartFile[] files) {
        List<String> errors = new ArrayList<>();
        if (files.length > 5) {
            errors.add("Cannot upload more than 5 files");
            return errors;
        }

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue; // Skip empty files
            }

            try {
                if (file.getSize() > 10 * 1024 * 1024) { // 10MB limit
                    errors.add("File '" + file.getOriginalFilename() + "' exceeds 10MB limit");
                    continue;
                }

                String contentType = file.getContentType();
                if (!isValidFileType(contentType)) {
                    errors.add("File '" + file.getOriginalFilename() + "' has invalid type. Allowed: pdf, doc, docx, txt, ppt, pptx, zip");
                    continue;
                }

                ClassDocuments document = new ClassDocuments();
                document.setPost(post);
                document.setDocumentTitle(file.getOriginalFilename());
                document.setFileData(file.getBytes());

                Map<String, String> validationErrors = classDocumentsDAO.validateDocument(document);
                if (!validationErrors.isEmpty()) {
                    errors.addAll(validationErrors.values());
                    continue;
                }

                classDocumentsDAO.saveDocument(document);
            } catch (IOException e) {
                errors.add("Failed to process file '" + file.getOriginalFilename() + "': " + e.getMessage());
            }
        }

        return errors;
    }

    @Override
    public List<ClassDocuments> getDocumentsByPostId(String postId) {
        return classDocumentsDAO.getDocumentsByPostId(postId);
    }

    @Override
    public boolean isValidFileType(String contentType) {
        return contentType != null && (
                contentType.equals("application/pdf") ||
                        contentType.equals("application/msword") ||
                        contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") ||
                        contentType.equals("text/plain") ||
                        contentType.equals("application/vnd.ms-powerpoint") ||
                        contentType.equals("application/vnd.openxmlformats-officedocument.presentationml.presentation") ||
                        contentType.equals("application/zip") ||
                        contentType.equals("application/x-zip-compressed") ||
                        contentType.equals("application/octet-stream")
        );
    }
}