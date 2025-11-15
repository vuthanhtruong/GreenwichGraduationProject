package com.example.demo.students_Classes.students_MinorClasses.model;

import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.students_Classes.abstractStudents_Class.model.Students_Classes;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import com.example.demo.user.student.model.Students;
import com.example.demo.entity.Enums.YourNotification;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "Students_MinorClasses")
@Getter
@Setter
@PrimaryKeyJoinColumns({
        @PrimaryKeyJoinColumn(name = "StudentID", referencedColumnName = "StudentID"),
        @PrimaryKeyJoinColumn(name = "ClassID", referencedColumnName = "ClassID")
})
public class Students_MinorClasses extends Students_Classes {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AddedBy")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private DeputyStaffs addedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ClassID", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MinorClasses minorClass;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", length = 50, nullable = false)
    private YourNotification notificationType;

    public Students_MinorClasses() {
        this.notificationType = YourNotification.NOTIFICATION_003;
    }

    public Students_MinorClasses(Students student, MinorClasses minorClass,
                                 LocalDateTime createdAt, DeputyStaffs addedBy) {
        super(student, minorClass, createdAt);
        this.addedBy = addedBy;
        this.notificationType = YourNotification.NOTIFICATION_003;
    }

    @Override
    public String getSubjectName() {
        return ((MinorClasses) getClassEntity())
                .getMinorSubject()
                .getSubjectName();
    }

    @Override
    public String getSubjectType() {
        return "Minor";
    }
}
