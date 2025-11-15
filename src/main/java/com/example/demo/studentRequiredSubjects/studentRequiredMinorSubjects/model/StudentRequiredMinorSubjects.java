package com.example.demo.studentRequiredSubjects.studentRequiredMinorSubjects.model;

import com.example.demo.entity.Enums.YourNotification;
import com.example.demo.studentRequiredSubjects.studentRequiredMajorSubjects.model.StudentRequiredSubjects;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import com.example.demo.user.student.model.Students;
import com.example.demo.subject.minorSubject.model.MinorSubjects;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "StudentRequiredMinorSubjects")
@Getter
@Setter
public class StudentRequiredMinorSubjects extends StudentRequiredSubjects {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SubjectID", nullable = false, insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MinorSubjects minorSubject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AssignedBy")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private DeputyStaffs assignedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", length = 50, nullable = false)
    private YourNotification notificationType;

    public StudentRequiredMinorSubjects() {
        super();
        this.notificationType = YourNotification.NOTIFICATION_015;
    }

    public StudentRequiredMinorSubjects(Students student, MinorSubjects minorSubject, String requiredReason, DeputyStaffs assignedBy) {
        super(student, minorSubject, requiredReason);
        this.minorSubject = minorSubject;
        this.assignedBy = assignedBy;
    }
}
