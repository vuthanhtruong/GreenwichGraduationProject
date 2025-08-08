package com.example.demo.entity;

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
    @Column(name = "CampusID")
    private String campusId;

    @Column(name = "CampusName", nullable = false)
    private String campusName;

    @Column(name = "OpeningDay")
    private LocalDate openingDay;

    @Column(name = "Description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Admins creator;

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

    // toString method (optional)
    @Override
    public String toString() {
        return "Campus{" +
                "campusId='" + campusId + '\'' +
                ", campusName='" + campusName + '\'' +
                ", openingDay=" + openingDay +
                ", description='" + description + '\'' +
                ", creator=" + (creator != null ? creator.getId() : null) +
                '}';
    }
}