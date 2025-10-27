package com.example.demo.classes.majorClasses.model;

import com.example.demo.classes.abstractClasses.model.Classes;
import com.example.demo.entity.Enums.Sessions;
import com.example.demo.subject.majorSubject.model.MajorSubjects;
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
public class MajorClasses extends Classes {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SubjectID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MajorSubjects subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Staffs creator;

    public MajorClasses() {}

    public MajorClasses(String classId, String nameClass, Integer slotQuantity, Sessions session,
                        MajorSubjects subject, Staffs creator, LocalDateTime createdAt) {
        super(classId, nameClass, slotQuantity, session, createdAt);
        this.subject = subject;
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
        return (subject != null)
                ? subject.getSubjectName()
                : "Unknown Subject";
    }
}
