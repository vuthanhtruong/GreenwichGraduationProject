package com.example.demo.classes.specializedClasses.model;

import com.example.demo.classes.abstractClasses.model.Classes;
import com.example.demo.entity.Enums.Sessions;
import com.example.demo.subject.specializedSubject.model.SpecializedSubject;
import com.example.demo.user.staff.model.Staffs;
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
public class SpecializedClasses extends Classes {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SpecializedSubjectID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SpecializedSubject specializedSubject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Staffs creator;

    public SpecializedClasses() {}

    public SpecializedClasses(String classId, String nameClass, Integer slotQuantity, Sessions session,
                              SpecializedSubject specializedSubject, Staffs creator, LocalDateTime createdAt) {
        super(classId, nameClass, slotQuantity, session, createdAt);
        this.specializedSubject = specializedSubject;
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
        return (specializedSubject != null)
                ? specializedSubject.getSubjectName()
                : "Unknown Subject";
    }
}
