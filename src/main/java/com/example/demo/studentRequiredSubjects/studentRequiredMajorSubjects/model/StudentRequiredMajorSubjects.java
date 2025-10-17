package com.example.demo.studentRequiredSubjects.studentRequiredMajorSubjects.model;

import com.example.demo.user.staff.model.Staffs;
import com.example.demo.user.student.model.Students;
import com.example.demo.subject.majorSubject.model.MajorSubjects;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "StudentRequiredMajorSubjects")
@Getter
@Setter
public class StudentRequiredMajorSubjects extends StudentRequiredSubjects {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SubjectID", nullable = false, insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MajorSubjects majorSubject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AssignedBy")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Staffs assignedBy;

    public StudentRequiredMajorSubjects() {
        super();
    }

    public StudentRequiredMajorSubjects(Students student, MajorSubjects majorSubject, String requiredReason, Staffs assignedBy) {
        super(student, majorSubject, requiredReason);
        this.majorSubject = majorSubject;
        this.assignedBy = assignedBy;
    }
}
