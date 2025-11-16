// src/main/java/com/example/demo/document/service/SpecializedSubmissionDocumentsServiceImpl.java

package com.example.demo.document.service;

import com.example.demo.document.dao.SpecializedSubmissionDocumentsDAO;
import com.example.demo.document.model.SpecializedSubmissionDocuments;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SpecializedSubmissionDocumentsServiceImpl implements SpecializedSubmissionDocumentsService {

    private final SpecializedSubmissionDocumentsDAO dao;

    public SpecializedSubmissionDocumentsServiceImpl(SpecializedSubmissionDocumentsDAO dao) {
        this.dao = dao;
    }

    @Override
    public void saveDocument(SpecializedSubmissionDocuments document) {
        dao.saveDocument(document);
    }

    @Override
    public List<SpecializedSubmissionDocuments> getDocumentsBySubmission(String submittedBy, String assignmentSlotId) {
        return dao.getDocumentsBySubmission(submittedBy, assignmentSlotId);
    }

    @Override
    public SpecializedSubmissionDocuments getDocumentById(String submissionDocumentId) {
        return dao.getDocumentById(submissionDocumentId);
    }

    @Override
    public Map<String, String> validateDocument(SpecializedSubmissionDocuments document) {
        return dao.validateDocument(document);
    }

    @Override
    public String generateUniqueDocumentId(String submittedBy, String assignmentSlotId) {
        return dao.generateUniqueDocumentId(submittedBy, assignmentSlotId);
    }
}