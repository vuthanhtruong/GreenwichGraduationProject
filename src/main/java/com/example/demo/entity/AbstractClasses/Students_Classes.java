package com.example.demo.entity.AbstractClasses;

import com.example.demo.entity.Enums.Notifications;
import com.example.demo.entity.Students;
import com.example.demo.entity.StudentsClassesId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "Students_Classes")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter

public abstract class Students_Classes {

    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "studentId", column = @Column(name = "StudentID", nullable = false)),
            @AttributeOverride(name = "classId",   column = @Column(name = "ClassID",   nullable = false))
    })
    private StudentsClassesId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("studentId")
    @JoinColumn(name = "StudentID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Students student;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("classId")
    @JoinColumn(name = "ClassID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Classes classEntity;

    @Enumerated(EnumType.STRING)
    @Column(name = "Notification", nullable = true)
    private Notifications notification;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    public Students_Classes() {}

    public Students_Classes(Students student, Classes classEntity, LocalDateTime createdAt) {
        this.id = new StudentsClassesId(student.getId(), classEntity.getClassId());
        this.student = student;
        this.classEntity = classEntity;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }
}