package com.example.demo.entity;

import com.example.demo.entity.Enums.UpgradeStatus;
import com.example.demo.student.model.Students;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "UpgradeStudents")
@Getter
@Setter
public class UpgradeStudents {

    @EmbeddedId
    private UpgradeStudentsId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("studentId")
    @JoinColumn(name = "StudentID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Students student;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    public UpgradeStudents() {
        this.createdAt = LocalDateTime.now();
    }

    public UpgradeStudents(Students student, UpgradeStatus upgradeStatus) {
        this.id = new UpgradeStudentsId(student.getId(), upgradeStatus);
        this.student = student;
        this.createdAt = LocalDateTime.now();
    }
}