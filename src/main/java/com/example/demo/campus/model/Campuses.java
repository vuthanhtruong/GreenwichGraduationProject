package com.example.demo.campus.model;

import com.example.demo.admin.model.Admins;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Table(name = "Campuses")
@Getter
@Setter
public class Campuses {

    @Id
    @Column(name = "CampusID", nullable = true)
    private String campusId;

    @Column(name = "CampusName", nullable = true)
    private String campusName;

    @Column(name = "OpeningDay")
    private LocalDate openingDay;

    @Column(name = "Description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Admins creator;

    @Lob
    @Column(name = "Avatar", nullable = true)
    private byte[] avatar;

    // Constructors
    public Campuses() {
    }

    public Campuses(String campusId, String campusName, LocalDate openingDay, String description, Admins creator) {
        this.campusId = campusId;
        this.campusName = campusName;
        this.openingDay = openingDay;
        this.description = description;
        this.creator = creator;
    }

    // toString method
    @Override
    public String toString() {
        return "Campus{" +
                "campusId='" + campusId + '\'' +
                ", campusName='" + campusName + '\'' +
                ", openingDay=" + openingDay +
                ", description='" + description + '\'' +
                ", creator=" + (creator != null ? creator.getId() : null) +
                ", avatar=" + (avatar != null ? "present" : "null") +
                '}';
    }
}