// File: SupportTicketRequestsDocument.java
package com.example.demo.supportTickets.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "SupportTicketRequestsDocuments")
@Getter
@Setter
public class SupportTicketRequestsDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "DocumentID")
    private String documentId;

    @Column(name = "FileName", nullable = false, length = 255)
    private String fileName;

    @Column(name = "FileType", length = 100)
    private String fileType;

    @Column(name = "FileSize")
    private Long fileSize;

    @Lob
    @Column(name = "FileData", nullable = false,columnDefinition = "LONGBLOB")
    private byte[] fileData;

    @Column(name = "UploadedAt", nullable = false, updatable = false)
    private LocalDateTime uploadedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RequestID", nullable = false)
    private SupportTicketRequests supportTicketRequest;

    public SupportTicketRequestsDocument() {}

    public SupportTicketRequestsDocument(String fileName, String fileType, Long fileSize, byte[] fileData, SupportTicketRequests request) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.fileData = fileData;
        this.supportTicketRequest = request;
    }
}