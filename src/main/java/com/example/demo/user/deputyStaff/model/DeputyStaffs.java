package com.example.demo.user.deputyStaff.model;

import com.example.demo.user.admin.model.Admins;
import com.example.demo.user.employe.model.MinorEmployes;
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