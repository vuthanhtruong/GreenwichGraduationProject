package com.example.demo.Curriculum.model;

import com.example.demo.admin.model.Admins;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "Curriculums")
@Getter
@Setter
@NoArgsConstructor
public class Curriculum {
    @Id
    @Column(name = "CurriculumID")
    private String curriculumId;

    @Column(name = "Name", nullable = false, length = 255)
    private String name;

    @Column(name = "Description", nullable = true, length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CreatorID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Admins creator;

    @CreationTimestamp
    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Curriculum(String curriculumId, String name, String description, Admins creator, LocalDateTime createdAt) {
        this.curriculumId = curriculumId;
        this.name = name;
        this.description = description;
        this.creator = creator;
        this.createdAt = createdAt;
    }
}