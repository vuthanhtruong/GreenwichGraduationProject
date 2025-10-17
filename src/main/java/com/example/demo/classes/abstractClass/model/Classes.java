package com.example.demo.classes.abstractClass.model;

import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.classes.majorClasses.model.MinorClasses;
import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import com.example.demo.entity.Enums.Sessions;
import com.example.demo.user.staff.model.Staffs;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.time.LocalDateTime;

@Entity
@Table(name = "classes")
@Inheritance(strategy = InheritanceType.JOINED)
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

    public Classes() {
    }

    public Classes(String classId, String nameClass, Integer slotQuantity, Sessions session, LocalDateTime createdAt) {
        if (classId == null || classId.trim().isEmpty()) {
            throw new IllegalArgumentException("Class ID cannot be null or empty");
        }
        if (nameClass == null || nameClass.trim().isEmpty()) {
            throw new IllegalArgumentException("Class name cannot be null or empty");
        }
        if (slotQuantity != null && slotQuantity < 0) {
            throw new IllegalArgumentException("Slot quantity cannot be negative");
        }
        this.classId = classId;
        this.nameClass = nameClass;
        this.slotQuantity = slotQuantity;
        this.session = session;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public String getCreatorName() {
        Hibernate.initialize(this); // Ensure the proxy is initialized
        if (this instanceof MajorClasses majorClasses) {
            Hibernate.initialize(majorClasses.getCreator());
            Staffs creator = majorClasses.getCreator();
            return creator != null ? creator.getFirstName() + " " + creator.getLastName() : "Unknown Creator";
        } else if (this instanceof MinorClasses minorClasses) {
            Hibernate.initialize(minorClasses.getCreator());
            DeputyStaffs creator = minorClasses.getCreator();
            return creator != null ? creator.getFirstName() + " " + creator.getLastName() : "Unknown Creator";
        } else if (this instanceof SpecializedClasses specializedClasses) {
            Hibernate.initialize(specializedClasses.getCreator());
            Staffs creator = specializedClasses.getCreator();
            return creator != null ? creator.getFirstName() + " " + creator.getLastName() : "Unknown Creator";
        }
        return "Unknown Creator";
    }

    public String getSubjectType() {
        Hibernate.initialize(this); // Ensure the proxy is initialized
        if (this instanceof MajorClasses majorClasses) {
            Hibernate.initialize(majorClasses.getSubject());
            return majorClasses.getSubject() != null ? majorClasses.getSubject().getSubjectName() : "Unknown Subject";
        } else if (this instanceof MinorClasses) {
            return "Minor Subject";
        } else if (this instanceof SpecializedClasses specializedClasses) {
            Hibernate.initialize(specializedClasses.getSpecializedSubject());
            return specializedClasses.getSpecializedSubject() != null ? specializedClasses.getSpecializedSubject().getSubjectName() : "Unknown Subject";
        }
        return "Unknown Subject";
    }
}