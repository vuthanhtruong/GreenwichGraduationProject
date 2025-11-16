package com.example.demo.document.dao;

import com.example.demo.document.model.SpecializedSubmissionDocuments;

import java.util.List;
import java.util.Map;

public interface SpecializedSubmissionDocumentsDAO {
    void saveDocument(SpecializedSubmissionDocuments document);
    List<SpecializedSubmissionDocuments> getDocumentsBySubmission(String submittedBy, String assignmentSlotId);
    SpecializedSubmissionDocuments getDocumentById(String submissionDocumentId);
    Map<String, String> validateDocument(SpecializedSubmissionDocuments document);
    String generateUniqueDocumentId(String submittedBy, String assignmentSlotId);
}
