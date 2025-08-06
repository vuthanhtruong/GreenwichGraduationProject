package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Entity
@Table(name = "DeputyStaffs")
@PrimaryKeyJoinColumn(name = "ID")
@Getter
@Setter
public class DeputyStaffs extends Employes {

    @Column(name = "Password", nullable = false, length = 255)
    private String password;

    public void setPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.password = passwordEncoder.encode(password);
    }

    @Override
    public String getRoleType() {
        return "DEPUTY_STAFF";
    }

    @Override
    public String getPassword() {
        return password;
    }
}