package com.example.demo.classes.abstractClasses.model;

import com.example.demo.entity.Enums.Sessions;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "classes")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public abstract class Classes {

    @Id
    @Column(name = "ClassID", nullable = false, updatable = false)
    private String classId;

    @Column(name = "NameClass", nullable = false)
    private String nameClass;

    @Column(name = "SlotQuantity")
    private Integer slotQuantity;

    @Column(name = "Session")
    @Enumerated(EnumType.STRING)
    private Sessions session;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    public Classes() {}

    public Classes(String classId, String nameClass, Integer slotQuantity, Sessions session, LocalDateTime createdAt) {
        if (classId == null || classId.trim().isEmpty())
            throw new IllegalArgumentException("Class ID cannot be null or empty");
        if (nameClass == null || nameClass.trim().isEmpty())
            throw new IllegalArgumentException("Class name cannot be null or empty");
        if (slotQuantity != null && slotQuantity < 0)
            throw new IllegalArgumentException("Slot quantity cannot be negative");

        this.classId = classId;
        this.nameClass = nameClass;
        this.slotQuantity = slotQuantity;
        this.session = session;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    // 🔹 Abstract methods để các lớp con override — tránh instanceof và proxy narrowing
    public abstract String getCreatorName();
    public abstract String getSubjectType();
}
