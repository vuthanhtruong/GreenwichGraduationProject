package com.example.demo.specializedClasses.model;

import com.example.demo.Specialization.model.Specialization;
import com.example.demo.classes.model.Classes;
import com.example.demo.specializedSubject.model.SpecializedSubject;
import com.example.demo.staff.model.Staffs;
import com.example.demo.entity.Enums.Sessions;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@PrimaryKeyJoinColumn(name = "ClassID")
@Getter
@Setter
@OnDelete(action = OnDeleteAction.CASCADE)
public class SpecializedClasses extends Classes {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SpecializedSubjectID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SpecializedSubject specializedSubject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Staffs creator;

    // Constructors
    public SpecializedClasses() {
    }

    public SpecializedClasses(String classId, String nameClass, Integer slotQuantity, Sessions session, SpecializedSubject specializedSubject, Staffs creator, LocalDateTime createdAt) {
        super(classId, nameClass, slotQuantity, session, createdAt);
        this.specializedSubject = specializedSubject;
        this.creator = creator;
    }
}