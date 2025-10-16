package com.example.demo.classes.model;

import com.example.demo.deputyStaff.model.DeputyStaffs;
import com.example.demo.entity.Enums.Sessions;
import com.example.demo.staff.model.Staffs;
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
        this.classId = classId;
        this.nameClass = nameClass;
        this.slotQuantity = slotQuantity;
        this.session = session;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public String getCreatorName() {
        Hibernate.initialize(this); // Initialize the proxy to determine the subclass
        if (this instanceof MajorClasses majorClasses) {
            Hibernate.initialize(majorClasses.getCreator());
            Staffs creator = majorClasses.getCreator();
            return creator != null ? creator.getFirstName() + " " + creator.getLastName() : "Unknown Creator";
        } else if (this instanceof MinorClasses minorClasses) {
            Hibernate.initialize(minorClasses.getCreator());
            DeputyStaffs creator = minorClasses.getCreator();
            return creator != null ? creator.getFirstName() + " " + creator.getLastName() : "Unknown Creator";
        }
        return "Unknown Creator";
    }
}