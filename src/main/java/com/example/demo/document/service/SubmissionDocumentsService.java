package com.example.demo.document.service;

import com.example.demo.document.model.SubmissionDocuments;

import java.util.List;
import java.util.Map;

public interface SubmissionDocumentsService {
    void saveDocument(SubmissionDocuments document);
    List<SubmissionDocuments> getDocumentsBySubmission(String submittedBy, String assignmentSlotId);
    SubmissionDocuments getDocumentById(String submissionDocumentId);
    Map<String, String> validateDocument(SubmissionDocuments document);
    String generateUniqueDocumentId(String submittedBy, String assignmentSlotId);
}
