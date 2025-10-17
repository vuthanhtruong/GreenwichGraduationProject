package com.example.demo.studentRequiredSubjects.studentRequiredSpecializedSubjects.model;

import com.example.demo.subject.specializedSubject.model.SpecializedSubject;
import com.example.demo.user.staff.model.Staffs;
import com.example.demo.user.student.model.Students;
import com.example.demo.studentRequiredSubjects.studentRequiredMajorSubjects.model.StudentRequiredSubjects;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "StudentRequiredSpecializedSubjects")
@Getter
@Setter
public class StudentRequiredSpecializedSubjects extends StudentRequiredSubjects {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SubjectID", nullable = false, insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SpecializedSubject specializedSubject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AssignedBy")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Staffs assignedBy;

    public StudentRequiredSpecializedSubjects() {
        super();
    }

    public StudentRequiredSpecializedSubjects(Students student, SpecializedSubject specializedSubject, String requiredReason, Staffs assignedBy) {
        super(student, specializedSubject, requiredReason);
        this.specializedSubject = specializedSubject;
        this.assignedBy = assignedBy;
    }
}