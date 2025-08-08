package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "classes")
public class MajorClasses {

    @Id
    @Column(name = "ClassID")
    private String classId;

    @Column(name = "NameClass", nullable = true)
    private String nameClass;

    @Column(name = "SlotQuantity")
    private Integer slotQuantity;

    @Column(name = "Session", nullable = true)
    @Enumerated(EnumType.STRING)
    private Sessions session;

    @ManyToOne
    @JoinColumn(name = "SubjectID", nullable = true)
    private MajorSubjects subject;

    @ManyToOne
    @JoinColumn(name = "Creator", nullable = true)
    private Staffs creator;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    // Constructors
    public MajorClasses() {
    }

    public MajorClasses(String classId, String nameClass, Integer slotQuantity, Sessions session, MajorSubjects subject, Staffs creator, LocalDateTime createdAt) {
        this.classId = classId;
        this.nameClass = nameClass;
        this.slotQuantity = slotQuantity;
        this.session = session;
        this.subject = subject;
        this.creator = creator;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getNameClass() {
        return nameClass;
    }

    public void setNameClass(String nameClass) {
        this.nameClass = nameClass;
    }

    public Integer getSlotQuantity() {
        return slotQuantity;
    }

    public void setSlotQuantity(Integer slotQuantity) {
        this.slotQuantity = slotQuantity;
    }

    public Sessions getSession() {
        return session;
    }

    public void setSession(Sessions session) {
        this.session = session;
    }

    public MajorSubjects getSubject() {
        return subject;
    }

    public void setSubject(MajorSubjects subject) {
        this.subject = subject;
    }

    public Staffs getCreator() {
        return creator;
    }

    public void setCreator(Staffs creator) {
        this.creator = creator;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}