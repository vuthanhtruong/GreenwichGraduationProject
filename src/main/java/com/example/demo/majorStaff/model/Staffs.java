package com.example.demo.majorStaff.model;

import com.example.demo.employe.model.MajorEmployes;
import com.example.demo.entity.Admins;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "Staffs")
@PrimaryKeyJoinColumn(name = "ID") // Liên kết với khóa chính từ Person
@Getter
@Setter
public class Staffs extends MajorEmployes {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Admins creator;

    @Override
    public String getRoleType() {
        return "STAFF";
    }
}
