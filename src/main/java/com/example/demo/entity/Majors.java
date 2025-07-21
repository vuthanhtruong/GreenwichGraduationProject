package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Majors")
@Getter
@Setter
public class Majors {

    @Id
    @Column(name = "MajorID")
    private String majorId;

    @Column(name = "MajorName", nullable = false, length = 255)
    private String majorName;
}