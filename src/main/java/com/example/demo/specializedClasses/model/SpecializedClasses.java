package com.example.demo.specializedClasses.model;

import com.example.demo.Specialization.model.Specialization;
import com.example.demo.classes.model.Classes;
import com.example.demo.staff.model.Staffs;
import com.example.demo.entity.Enums.Sessions;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("SPECIALIZED")
@Getter
@Setter
public class SpecializedClasses extends Classes {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SpecializationID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Specialization specialization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Staffs creator;

    // Constructors
    public SpecializedClasses() {
    }

    public SpecializedClasses(String classId, String nameClass, Integer slotQuantity, Sessions session, Specialization specialization, Staffs creator, LocalDateTime createdAt) {
        super(classId, nameClass, slotQuantity, session, createdAt);
        this.specialization = specialization;
        this.creator = creator;
    }
}