package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("MINOR")
@Getter
@Setter
public class MinorClasses extends Classes {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MinorSubjectID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MinorSubjects minorSubject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private DeputyStaffs creator;

    // Constructors
    public MinorClasses() {
    }

    public MinorClasses(String classId, String nameClass, Integer slotQuantity, Sessions session, MinorSubjects minorSubject, DeputyStaffs creator, LocalDateTime createdAt) {
        super(classId, nameClass, slotQuantity, session, createdAt);
        this.minorSubject = minorSubject;
        this.creator = creator;
    }
}