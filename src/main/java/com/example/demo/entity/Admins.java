package com.example.demo.entity;

import com.example.demo.entity.AbstractClasses.Persons;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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