package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;

@Entity
@Table(name = "ParentAccounts")
@PrimaryKeyJoinColumn(name = "ID")
@Getter
@Setter
public class ParentAccounts extends Persons {

    @Column(name = "Password", nullable = false, length = 255)
    private String password;

    @Column(name = "RelationshipToStudent", nullable = true, length = 50)
    private String relationshipToStudent; // e.g., "Father", "Mother", "Guardian"

    @Column(name = "CreatedDate", nullable = false, updatable = false)
    private LocalDate createdDate = LocalDate.now();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CreatorID", nullable = true)
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
        return getGender() == Gender.MALE ? "/DefaultAvatar/Parent_Male.png" : "/DefaultAvatar/Parent_Female.png";
    }

    @Override
    public String getRoleType() {
        return "PARENT";
    }

    @Override
    public String getPassword() {
        return password;
    }
}