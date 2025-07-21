package com.example.demo.dto;

import java.time.LocalDate;

public class CampusesDTO {
    private String campusId;
    private String campusName;
    private LocalDate openingDay;
    private String description;

    public CampusesDTO(String campusId, String campusName, LocalDate openingDay, String description) {
        this.campusId = campusId;
        this.campusName = campusName;
        this.openingDay = openingDay;
        this.description = description;
    }

    // Getters and Setters
    public String getCampusId() { return campusId; }
    public void setCampusId(String campusId) { this.campusId = campusId; }
    public String getCampusName() { return campusName; }
    public void setCampusName(String campusName) { this.campusName = campusName; }
    public LocalDate getOpeningDay() { return openingDay; }
    public void setOpeningDay(LocalDate openingDay) { this.openingDay = openingDay; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}