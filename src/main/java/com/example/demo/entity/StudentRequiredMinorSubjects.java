package com.example.demo.entity;

import com.example.demo.entity.AbstractClasses.StudentRequiredSubjects;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "StudentRequiredMinorSubjects")
@PrimaryKeyJoinColumn(name = "StudentID", referencedColumnName = "StudentID")
@SecondaryTable(name = "StudentRequiredSubjects", pkJoinColumns = {
        @PrimaryKeyJoinColumn(name = "StudentID", referencedColumnName = "StudentID"),
        @PrimaryKeyJoinColumn(name = "SubjectID", referencedColumnName = "SubjectID")
})
@Getter
@Setter
public class StudentRequiredMinorSubjects extends StudentRequiredSubjects {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SubjectID", nullable = false, insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MinorSubjects minorSubject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AssignedBy", nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private DeputyStaffs assignedBy;

    public StudentRequiredMinorSubjects() {
        super();
    }

    public StudentRequiredMinorSubjects(Students student, MinorSubjects minorSubject, String requiredReason, DeputyStaffs assignedBy) {
        super(student, minorSubject, requiredReason);
        this.minorSubject = minorSubject;
        this.assignedBy = assignedBy;
    }
}