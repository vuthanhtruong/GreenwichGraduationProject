package com.example.demo.entity.AbstractClasses;

import com.example.demo.entity.Campuses;
import com.example.demo.entity.Majors;
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
public abstract class MajorEmployes extends Persons {

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

    @Override
    public abstract String getRoleType();
}