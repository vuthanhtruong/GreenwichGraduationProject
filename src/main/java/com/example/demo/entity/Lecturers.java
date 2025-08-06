package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Entity
@Table(name = "Lecturers")
@PrimaryKeyJoinColumn(name = "ID")
@Getter
@Setter
public class Lecturers extends Employes {

    @Column(name = "Type", nullable = true, length = 50)
    @Enumerated(EnumType.STRING)
    private EmploymentTypes employmentTypes;

    @Column(name = "Password", nullable = false, length = 255)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AddedBy", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Staffs creator;

    public void setPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.password = passwordEncoder.encode(password);
    }

    public String getDefaultAvatarPath() {
        if (getAvatar() != null) {
            return null; // Avatar exists, no default needed
        }
        return getGender() == Gender.MALE ? "/DefaultAvatar/Teacher_Boy.png" : "/DefaultAvatar/Teacher_Girl.png";
    }

    @Override
    public String getRoleType() {
        return "LECTURER";
    }

    @Override
    public String getPassword() {
        return password;
    }
}