package com.example.demo.lecturers_Classes.abstractLecturers_Classes.model;

import com.example.demo.classes.abstractClass.model.Classes;
import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.entity.Enums.Notifications;
import com.example.demo.lecturers_Classes.majorLecturers_MajorClasses.model.LecturersClassesId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
            @AttributeOverride(name = "classId",    column = @Column(name = "ClassID",    nullable = false))
    })
    private LecturersClassesId id;

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

    public Lecturers_Classes() {}

    public Lecturers_Classes(String lecturerId, Classes classEntity, LocalDateTime createdAt) {
        this.id = new LecturersClassesId(lecturerId, classEntity.getClassId());
        this.classEntity = classEntity;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public String getSubjectName(){
        Classes c = getClassEntity();
        if(c instanceof MajorClasses majorClasses){
            return majorClasses.getSubject().getSubjectName();
        } else if (c instanceof SpecializedClasses specializedClasses) {
            return specializedClasses.getSpecializedSubject().getSubjectName();
        }
        return null;
    }
}