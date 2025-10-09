package com.example.demo.student_class.model;

import com.example.demo.classes.model.SpecializedClasses;
import com.example.demo.entity.Enums.Notifications;
import com.example.demo.staff.model.Staffs;
import com.example.demo.student.model.Students;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "Students_SpecializedClasses")
@Getter
@Setter
@PrimaryKeyJoinColumns({
        @PrimaryKeyJoinColumn(name = "StudentID", referencedColumnName = "StudentID"),
        @PrimaryKeyJoinColumn(name = "ClassID", referencedColumnName = "ClassID")
})
public class SpecializedStudents_Classes extends Students_Classes {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ClassID", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SpecializedClasses specializedClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AddedBy", nullable = false)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Staffs addedBy;

    public SpecializedStudents_Classes() {}

    public SpecializedStudents_Classes(Students student, SpecializedClasses specializedClass, Notifications notification,
                                       Staffs addedBy, LocalDateTime createdAt) {
        super(student, specializedClass, createdAt);
        this.specializedClass = specializedClass;
        this.addedBy = addedBy;
        this.setNotification(notification);
    }
}