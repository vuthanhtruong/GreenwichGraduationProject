package com.example.demo.students_Classes.abstractStudents_Class.model;

import com.example.demo.classes.abstractClasses.model.Classes;
import com.example.demo.user.student.model.Students;
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
    private StudentsClassesId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("studentId")
    @JoinColumn(name = "StudentID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Students student;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("classId")
    @JoinColumn(name = "ClassID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Classes classEntity;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    protected Students_Classes() {}

    protected Students_Classes(Students student, Classes classEntity, LocalDateTime createdAt) {
        this.student = student;
        this.classEntity = classEntity;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.id = new StudentsClassesId(student.getId(), classEntity.getClassId());
    }

    @PrePersist
    public void prePersist() {
        if (this.id == null && student != null && classEntity != null) {
            this.id = new StudentsClassesId(student.getId(), classEntity.getClassId());
        }
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    // 🔹 Giao diện trừu tượng thay vì instanceof
    public abstract String getSubjectName();
    public abstract String getSubjectType();
}
