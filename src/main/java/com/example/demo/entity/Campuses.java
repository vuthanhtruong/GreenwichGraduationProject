package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "campuses")
public class Campuses {

    @Id
    @Column(name = "CampusID")
    private String campusId;

    @Column(name = "CampusName", nullable = false)
    private String campusName;

    @Column(name = "OpeningDay")
    private LocalDate openingDay;

    @Column(name = "Description") // Thay 'Describe' bằng 'Description'
    private String description;

    // Constructors
    public Campuses() {
    }

    public Campuses(String campusId, String campusName, LocalDate openingDay, String description) {
        this.campusId = campusId;
        this.campusName = campusName;
        this.openingDay = openingDay;
        this.description = description;
    }

    // Getters and Setters
    public String getCampusId() {
        return campusId;
    }

    public void setCampusId(String campusId) {
        this.campusId = campusId;
    }

    public String getCampusName() {
        return campusName;
    }

    public void setCampusName(String campusName) {
        this.campusName = campusName;
    }

    public LocalDate getOpeningDay() {
        return openingDay;
    }

    public void setOpeningDay(LocalDate openingDay) {
        this.openingDay = openingDay;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // toString method (optional)
    @Override
    public String toString() {
        return "Campus{" +
                "campusId='" + campusId + '\'' +
                ", campusName='" + campusName + '\'' +
                ", openingDay=" + openingDay +
                ", description='" + description + '\'' +
                '}';
    }
}
