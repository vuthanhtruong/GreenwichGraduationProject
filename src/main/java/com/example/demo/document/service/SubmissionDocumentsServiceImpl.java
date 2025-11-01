package com.example.demo.document.service;

import com.example.demo.document.dao.SubmissionDocumentsDAO;
import com.example.demo.document.model.SubmissionDocuments;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SubmissionDocumentsServiceImpl implements SubmissionDocumentsService {
    private final SubmissionDocumentsDAO submissionDocumentsDAO;

    public SubmissionDocumentsServiceImpl(SubmissionDocumentsDAO submissionDocumentsDAO) {
        this.submissionDocumentsDAO = submissionDocumentsDAO;
    }

    @Override
    public void saveDocument(SubmissionDocuments document) {
        submissionDocumentsDAO.saveDocument(document);
    }

    @Override
    public List<SubmissionDocuments> getDocumentsBySubmission(String submittedBy, String assignmentSlotId) {
        return submissionDocumentsDAO.getDocumentsBySubmission(submittedBy, assignmentSlotId);
    }

    @Override
    public SubmissionDocuments getDocumentById(String submissionDocumentId) {
        return submissionDocumentsDAO.getDocumentById(submissionDocumentId);
    }

    @Override
    public Map<String, String> validateDocument(SubmissionDocuments document) {
        return submissionDocumentsDAO.validateDocument(document);
    }

    @Override
    public String generateUniqueDocumentId(String submittedBy, String assignmentSlotId) {
        return submissionDocumentsDAO.generateUniqueDocumentId(submittedBy, assignmentSlotId);
    }
}
