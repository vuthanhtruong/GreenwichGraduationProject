package com.example.demo.entity;

import com.example.demo.entity.AbstractClasses.Persons;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Table(name = "Employes")
@PrimaryKeyJoinColumn(name = "ID") // Liên kết với khóa chính từ Person
@Getter
@Setter
@OnDelete(action = OnDeleteAction.CASCADE)
public class Employes extends Persons {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CampusID", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Campuses campus;  // Liên kết với Employee (có thể NULL)

    @Column(nullable = false, updatable = false)
    private LocalDate createdDate = LocalDate.now();

    @Override
    public String getRoleType() {
        throw new UnsupportedOperationException("Employes must be either Staffs or Lecturers");
    }

}
