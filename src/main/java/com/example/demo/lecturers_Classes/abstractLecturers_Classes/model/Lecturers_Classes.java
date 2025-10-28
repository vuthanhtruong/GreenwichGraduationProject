package com.example.demo.lecturers_Classes.abstractLecturers_Classes.model;

import com.example.demo.entity.Enums.Notifications;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "Lecturers_Classes")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public abstract class Lecturers_Classes {

    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "lecturerId", column = @Column(name = "LecturerID", nullable = false)),
            @AttributeOverride(name = "classId", column = @Column(name = "ClassID", nullable = false))
    })
    private LecturersClassesId id;

    @Enumerated(EnumType.STRING)
    @Column(name = "Notification")
    private Notifications notification;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    protected Lecturers_Classes() {}

    protected Lecturers_Classes(String lecturerId, String classId, LocalDateTime createdAt) {
        this.id = new LecturersClassesId(lecturerId, classId);
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    // Abstract info for each subclass
    public abstract String getLecturerId();
    public abstract String getLecturerName();
    public abstract Object getLecturerEntity();

    // Abstract class info (each subclass must implement)
    public abstract String getClassId();
    public abstract String getClassName();
    public abstract String getSubjectName();
    public abstract String getSubjectCode();
    public abstract String getSession();
    public abstract Integer getSlotQuantity();

}
