package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "MinorClasses")
@Getter
@Setter
public class MinorClasses {

    @Id
    @Column(name = "MinorClassID")
    private String minorClassId;

    @Column(name = "NameClass", nullable = true, length = 255)
    private String nameClass;

    @Column(name = "SlotQuantity")
    private Integer slotQuantity;

    @Column(name = "Session", nullable = true)
    @Enumerated(EnumType.STRING)
    private Sessions session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MinorSubjectID", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MinorSubjects minorSubject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private DeputyStaffs creator;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    public MinorClasses() {}

    public MinorClasses(String minorClassId, String nameClass, Integer slotQuantity, Sessions session, MinorSubjects minorSubject, DeputyStaffs creator, LocalDateTime createdAt) {
        this.minorClassId = minorClassId;
        this.nameClass = nameClass;
        this.slotQuantity = slotQuantity;
        this.session = session;
        this.minorSubject = minorSubject;
        this.creator = creator;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }
}