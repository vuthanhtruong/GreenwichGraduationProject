package com.example.demo.entity;

import com.example.demo.entity.AbstractClasses.MinorEmployes;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "DeputyStaffs")
@PrimaryKeyJoinColumn(name = "ID")
@Getter
@Setter
public class DeputyStaffs extends MinorEmployes {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Admins creator;

    @Override
    public String getRoleType() {
        return "DEPUTY_STAFF";
    }
}