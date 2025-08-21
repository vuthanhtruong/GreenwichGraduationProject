package com.example.demo.classes.model;

import com.example.demo.entity.Enums.Sessions;
import com.example.demo.subject.model.MajorSubjects;
import com.example.demo.majorstaff.model.Staffs;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("MAJOR")
@Getter
@Setter
public class MajorClasses extends Classes {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SubjectID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MajorSubjects subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Staffs creator;

    // Constructors
    public MajorClasses() {
    }

    public MajorClasses(String classId, String nameClass, Integer slotQuantity, Sessions session, MajorSubjects subject, Staffs creator, LocalDateTime createdAt) {
        super(classId, nameClass, slotQuantity, session, createdAt);
        this.subject = subject;
        this.creator = creator;
    }
}