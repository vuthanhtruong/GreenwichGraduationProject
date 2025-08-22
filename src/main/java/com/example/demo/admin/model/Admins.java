package com.example.demo.admin.model;

import com.example.demo.person.model.Persons;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Admins")
@PrimaryKeyJoinColumn(name = "ID")
@Getter
@Setter
public class Admins extends Persons {

    @Lob
    @Column(name = "FaceData", columnDefinition = "LONGTEXT", nullable = true)
    private String faceData;

    @Override
    public String getRoleType() {
        return "ADMIN";
    }

}