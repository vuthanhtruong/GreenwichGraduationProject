package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Entity
@Table(name = "MinorLecturers")
@PrimaryKeyJoinColumn(name = "ID")
@Getter
@Setter
public class MinorLecturers extends Employes {

    @Column(name = "Password", nullable = false, length = 255)
    private String password;

    @Column(name = "Type", nullable = true, length = 50)
    private String type;

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
            return null;
        }
        return getGender() == Gender.MALE ? "/DefaultAvatar/MinorLecturer_Boy.png" : "/DefaultAvatar/MinorLecturer_Girl.png";
    }

    @Override
    public String getRoleType() {
        return "MINOR_LECTURER";
    }

    @Override
    public String getPassword() {
        return password;
    }
}