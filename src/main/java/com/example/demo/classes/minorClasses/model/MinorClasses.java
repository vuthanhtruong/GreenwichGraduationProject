package com.example.demo.classes.minorClasses.model;

import com.example.demo.classes.abstractClasses.model.Classes;
import com.example.demo.entity.Enums.Sessions;
import com.example.demo.subject.minorSubject.model.MinorSubjects;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@PrimaryKeyJoinColumn(name = "ClassID")
@Getter
@Setter
@OnDelete(action = OnDeleteAction.CASCADE)
public class MinorClasses extends Classes {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MinorSubjectID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MinorSubjects minorSubject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private DeputyStaffs creator;

    public MinorClasses() {}

    public MinorClasses(String classId, String nameClass, Integer slotQuantity, Sessions session,
                        MinorSubjects minorSubject, DeputyStaffs creator, LocalDateTime createdAt) {
        super(classId, nameClass, slotQuantity, session, createdAt);
        this.minorSubject = minorSubject;
        this.creator = creator;
    }

    @Override
    public String getCreatorName() {
        return (creator != null)
                ? creator.getFirstName() + " " + creator.getLastName()
                : "Unknown Creator";
    }

    @Override
    public String getSubjectType() {
        return (minorSubject != null)
                ? minorSubject.getSubjectName()
                : "Minor Subject";
    }
}
