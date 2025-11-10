// File: SupportTicketDocuments.java
package com.example.demo.supportTickets.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "SupportTicketDocuments")
@Getter
@Setter
public class SupportTicketDocuments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DocumentID")
    private Long documentId;

    @Column(name = "FileName", nullable = false, length = 255)
    private String fileName;

    @Column(name = "FileType", length = 100)
    private String fileType; // MIME type: application/pdf, image/jpeg, ...

    @Column(name = "FileSize")
    private Long fileSize; // bytes

    @Lob
    @Column(name = "FileData", nullable = false, columnDefinition = "LONGBLOB")
    private byte[] fileData;

    // Quan hệ: 1 SupportTickets → nhiều Documents
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SupportTicketID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SupportTickets supportTicket;

    @Column(name = "UploadedAt", nullable = false)
    private LocalDateTime uploadedAt = LocalDateTime.now();

    // Constructor
    public SupportTicketDocuments() {}

    public SupportTicketDocuments(String fileName, String fileType, Long fileSize, byte[] fileData, SupportTickets supportTicket) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.fileData = fileData;
        this.supportTicket = supportTicket;
        this.uploadedAt = LocalDateTime.now();
    }
}