package com.example.demo.timtable.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Table(name = "Slots")
@Getter
@Setter
public class Slots {

    @Id
    @Column(name = "SlotID")
    private String slotId;

    @Column(name = "SlotName", nullable = true, length = 100)
    private String slotName;

    @Column(name = "StartTime", nullable = false)
    private LocalTime startTime;

    @Column(name = "EndTime", nullable = false)
    private LocalTime endTime;
}