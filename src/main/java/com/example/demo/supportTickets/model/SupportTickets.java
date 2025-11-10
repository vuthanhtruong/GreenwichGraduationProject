package com.example.demo.supportTickets.model;

import com.example.demo.user.admin.model.Admins;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "SupportTickets")
@Getter
@Setter
public class SupportTickets {

    @Id
    @Column(name = "SupportTicketID")
    private String supportTicketId;

    @Column(name = "TicketName", nullable = false, length = 255)
    private String ticketName;

    @Column(name = "Description", nullable = true, length = 1000)
    private String description;

    @Column(name = "Cost", nullable = false)
    private Double cost = 0.0; // KHỞI TẠO MẶC ĐỊNH

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CreatorID", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Admins creator;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    // Trong SupportTickets.java
    @OneToMany(mappedBy = "supportTicket", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SupportTicketDocuments> documents = new ArrayList<>(); // THÊM DÒNG NÀY

    public SupportTickets() {
        this.createdAt = LocalDateTime.now();
    }

    public SupportTickets(String supportTicketId, String ticketName, String description, Double cost, Admins creator, LocalDateTime createdAt) {
        this.supportTicketId = supportTicketId;
        this.ticketName = ticketName;
        this.description = description;
        this.cost = cost;
        this.creator = creator;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }
}