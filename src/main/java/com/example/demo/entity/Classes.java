package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.time.LocalDateTime;

@Entity
@Table(name = "classes")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "class_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
public abstract class Classes {

    @Id
    @Column(name = "ClassID")
    private String classId;

    @Column(name = "NameClass")
    private String nameClass;

    @Column(name = "SlotQuantity")
    private Integer slotQuantity;

    @Column(name = "Session")
    @Enumerated(EnumType.STRING)
    private Sessions session;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    // Constructors
    public Classes() {
    }

    public Classes(String classId, String nameClass, Integer slotQuantity, Sessions session, LocalDateTime createdAt) {
        this.classId = classId;
        this.nameClass = nameClass;
        this.slotQuantity = slotQuantity;
        this.session = session;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }
}