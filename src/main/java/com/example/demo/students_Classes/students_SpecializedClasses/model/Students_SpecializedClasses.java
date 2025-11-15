package com.example.demo.students_Classes.students_SpecializedClasses.model;

import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.entity.Enums.YourNotification;
import com.example.demo.students_Classes.abstractStudents_Class.model.Students_Classes;
import com.example.demo.user.staff.model.Staffs;
import com.example.demo.user.student.model.Students;
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
public class Students_SpecializedClasses extends Students_Classes {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AddedBy")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Staffs addedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ClassID", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SpecializedClasses specializedClass;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", length = 50, nullable = false)
    private YourNotification notificationType;

    public Students_SpecializedClasses() {
        this.notificationType = YourNotification.NOTIFICATION_004;
    }

    public Students_SpecializedClasses(Students student,
                                       SpecializedClasses specializedClass,
                                       Staffs addedBy,
                                       LocalDateTime createdAt) {
        super(student, specializedClass, createdAt);
        this.addedBy = addedBy;
        this.notificationType = YourNotification.NOTIFICATION_004;
    }

    @Override
    public String getSubjectName() {
        return ((SpecializedClasses) getClassEntity()).getSpecializedSubject().getSubjectName();
    }

    @Override
    public String getSubjectType() {
        return "Specialized";
    }
}
