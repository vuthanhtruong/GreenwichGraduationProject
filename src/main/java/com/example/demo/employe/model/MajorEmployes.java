package com.example.demo.employe.model;

import com.example.demo.entity.Enums.Gender;
import com.example.demo.person.model.Persons;
import com.example.demo.campus.model.Campuses;
import com.example.demo.major.model.Majors;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Table(name = "MajorEmployes")
@PrimaryKeyJoinColumn(name = "ID")
@Getter
@Setter
@OnDelete(action = OnDeleteAction.CASCADE)
public class MajorEmployes extends Persons {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MajorID", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Majors majorManagement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CampusID", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Campuses campus;

    @Column(nullable = false, updatable = false)
    private LocalDate createdDate = LocalDate.now();

    public String getDefaultAvatarPath() {
        if (getAvatar() != null) {
            return null;
        }
        return getGender() == Gender.MALE ? "/DefaultAvatar/Staff_Boy.png" : "/DefaultAvatar/Staff_Girl.png";
    }

    @Override
    public String getRoleType() {
        return null;
    }
}