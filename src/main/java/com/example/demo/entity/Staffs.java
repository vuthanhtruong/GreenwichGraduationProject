package com.example.demo.entity;

import com.example.demo.entity.AbstractClasses.MajorEmployes;
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
