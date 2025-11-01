package com.example.demo.document.dao;

import com.example.demo.document.model.SubmissionDocuments;

import java.util.List;
import java.util.Map;

public interface SubmissionDocumentsDAO {
    void saveDocument(SubmissionDocuments document);
    List<SubmissionDocuments> getDocumentsBySubmission(String submittedBy, String assignmentSlotId);
    SubmissionDocuments getDocumentById(String submissionDocumentId);
    Map<String, String> validateDocument(SubmissionDocuments document);
    String generateUniqueDocumentId(String submittedBy, String assignmentSlotId);
}