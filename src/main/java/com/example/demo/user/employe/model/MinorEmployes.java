package com.example.demo.user.employe.model;

import com.example.demo.entity.Enums.Gender;
import com.example.demo.user.person.model.Persons;
import com.example.demo.campus.model.Campuses;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Table(name = "MinorEmployes")
@PrimaryKeyJoinColumn(name = "ID")
@Getter
@Setter
@OnDelete(action = OnDeleteAction.CASCADE)
public class MinorEmployes extends Persons implements MinorEmployesInterface {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CampusID", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Campuses campus;

    @Column(nullable = false, updatable = false)
    private LocalDate createdDate = LocalDate.now();

    @Override
    public String getDefaultAvatarPath() {
        if (getAvatar() != null) {
            return null;
        }
        return getGender() == Gender.MALE ? "/DefaultAvatar/Staff_Boy.png" : "/DefaultAvatar/Staff_Girl.png";
    }

    @Override
    public String getEmploymentInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Campus: ").append(campus != null ? campus.getCampusName() : "N/A").append("\n");
        sb.append("Created Date: ").append(createdDate != null ? createdDate.toString() : "N/A");
        return sb.toString();
    }

    @Override
    public String getRoleType() {
        return null;
    }
}