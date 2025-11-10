// File: SupportTicketRequests.java
package com.example.demo.supportTickets.model;

import com.example.demo.entity.Enums.Status;
import com.example.demo.user.student.model.Students;
import com.example.demo.user.staff.model.Staffs;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "SupportTicketRequests")
@Getter
@Setter
public class SupportTicketRequests {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "RequestID")
    private String requestId;

    @Column(name = "Title", nullable = false, length = 255)
    private String title;

    @Column(name = "Description", nullable = true, length = 2000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RequesterID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Students requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HandlerID", nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Staffs handler;

    @Column(name = "SupportTicketID", length = 50)
    private String supportTicketId;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = false, length = 50)
    private Status status;

    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @Column(name = "CompletedAt", nullable = true)
    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "supportTicketRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SupportTicketRequestsDocument> documents = new ArrayList<>();

    public SupportTicketRequests() {}

    public SupportTicketRequests(String title, String description, Students requester, String supportTicketId) {
        this.title = title;
        this.description = description;
        this.requester = requester;
        this.supportTicketId = supportTicketId;
    }

    public void addDocument(SupportTicketRequestsDocument doc) {
        documents.add(doc);
        doc.setSupportTicketRequest(this);
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}